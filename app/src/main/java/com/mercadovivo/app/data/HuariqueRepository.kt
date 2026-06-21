package com.mercadovivo.app.data

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.dropbox.core.DbxRequestConfig
import com.dropbox.core.oauth.DbxCredential
import com.dropbox.core.v2.DbxClientV2
import com.mercadovivo.app.BuildConfig
import com.mercadovivo.app.models.Huarique
import com.mercadovivo.app.models.Review
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.util.UUID

class HuariqueRepository {
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val collection = db.collection("huariques")

    // Configuración de Dropbox protegida para GitHub
    private val APP_KEY = BuildConfig.DROPBOX_APP_KEY
    private val APP_SECRET = BuildConfig.DROPBOX_APP_SECRET
    private val REFRESH_TOKEN = BuildConfig.DROPBOX_REFRESH_TOKEN

    private val dbxClient: DbxClientV2 by lazy {
        val config = DbxRequestConfig.newBuilder("mercado-vivo/v1").build()
        val credential = DbxCredential(
            "", // access token inicial (puede ir vacío)
            -1L, // expiración
            REFRESH_TOKEN,
            APP_KEY,
            APP_SECRET
        )
        DbxClientV2(config, credential)
    }

    fun getHuariquesFlow(): Flow<List<Huarique>> = callbackFlow {
        val subscription = collection.addSnapshotListener { snapshot, error ->
            if (error == null && snapshot != null) {
                val firebaseList = snapshot.toObjects(Huarique::class.java)
                trySend(firebaseList)
            }
        }
        awaitClose { subscription.remove() }
    }

    suspend fun uploadImage(context: android.content.Context, imageUri: Uri): String = withContext(Dispatchers.IO) {
        val fileName = UUID.randomUUID().toString()
        val ref = storage.reference.child("huariques_photos/$fileName")
        
        // COMPRESIÓN Y REDIMENSIONADO DE IMAGEN PARA OPTIMIZACIÓN (Honor x6c)
        val inputStream = context.contentResolver.openInputStream(imageUri)
        val originalBitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        if (originalBitmap == null) throw Exception("No se pudo decodificar la imagen")
        
        // Redimensionar a 512x384 manteniendo proporción o forzando si es necesario
        val scaledBitmap = android.graphics.Bitmap.createScaledBitmap(originalBitmap, 512, 384, true)
        
        val baos = java.io.ByteArrayOutputStream()
        // Reducimos la calidad al 70% para que pese poco pero se vea bien
        scaledBitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 70, baos)
        val data = baos.toByteArray()
        
        ref.putBytes(data).await()
        ref.downloadUrl.await().toString()
    }

    suspend fun uploadVideoToDropbox(context: android.content.Context, videoUri: Uri): String = withContext(Dispatchers.IO) {
        val uid = auth.currentUser?.uid ?: "anonymous"
        val fileName = "video_${System.currentTimeMillis()}.mp4"
        val path = "/videos/$uid/$fileName"

        // 1. Subir el video usando .use para cerrar el stream automáticamente
        context.contentResolver.openInputStream(videoUri)?.use { inputStream ->
            dbxClient.files().uploadBuilder(path)
                .uploadAndFinish(inputStream)
        } ?: throw Exception("No se pudo abrir el video")

        // 2. Obtener o Crear Shared Link
        val sharedLinkUrl = try {
            val sharedLinkMetadata = dbxClient.sharing().createSharedLinkWithSettings(path)
            sharedLinkMetadata.url
        } catch (e: com.dropbox.core.v2.sharing.CreateSharedLinkWithSettingsErrorException) {
            // Si ya existe, obtener los links existentes
            val result = dbxClient.sharing().listSharedLinksBuilder().withPath(path).withDirectOnly(true).start()
            result.links.firstOrNull()?.url ?: throw e
        }
        
        // 3. Convertir a link de descarga directa (FORMA INFALIBLE)
        // Reemplazamos el dominio y nos aseguramos de que no existan parámetros de visualización
        sharedLinkUrl
            .replace("www.dropbox.com", "dl.dropboxusercontent.com")
            .replace("?dl=0", "")
            .replace("&dl=0", "")
            .replace("?dl=1", "")
            .replace("&dl=1", "")
    }

    suspend fun saveHuarique(
        context: android.content.Context, 
        huarique: Huarique, 
        imageUris: List<Uri> = emptyList(),
        onProgress: (String) -> Unit = {}
    ): Result<String> {
        return try {
            // 1. Subir fotos generales del local comprimidas
            if (imageUris.isNotEmpty()) {
                onProgress("Optimizando y subiendo fotos del local...")
            }
            val uploadedPhotoUrls = imageUris.map { uploadImage(context, it) }
            
            // 2. Subir fotos y VIDEOS individuales de cada plato/bebida/postre
            val totalSteps = huarique.menuPlates.size + huarique.menuBeverages.size + huarique.menuDesserts.size
            var currentStep = 0

            val processItem: suspend (com.mercadovivo.app.models.Plato) -> com.mercadovivo.app.models.Plato = { item ->
                currentStep++
                var updatedItem = item
                if (item.photoLabel.startsWith("content://")) {
                    onProgress("Subiendo foto de: ${item.name} ($currentStep/$totalSteps)")
                    updatedItem = updatedItem.copy(photoLabel = uploadImage(context, Uri.parse(item.photoLabel)))
                }
                if (item.videoLabel.startsWith("content://")) {
                    onProgress("Subiendo video de: ${item.name} a Dropbox...")
                    updatedItem = updatedItem.copy(videoLabel = uploadVideoToDropbox(context, Uri.parse(item.videoLabel)))
                }
                updatedItem
            }

            val updatedPlates = huarique.menuPlates.map { processItem(it) }
            val updatedBeverages = huarique.menuBeverages.map { processItem(it) }
            val updatedDesserts = huarique.menuDesserts.map { processItem(it) }

            onProgress("Guardando datos finales...")
            val finalHuarique = if (huarique.id.isEmpty()) {
                huarique.copy(
                    id = UUID.randomUUID().toString(), 
                    photos = huarique.photos + uploadedPhotoUrls,
                    menuPlates = updatedPlates,
                    menuBeverages = updatedBeverages,
                    menuDesserts = updatedDesserts
                )
            } else {
                huarique.copy(
                    photos = huarique.photos + uploadedPhotoUrls,
                    menuPlates = updatedPlates,
                    menuBeverages = updatedBeverages,
                    menuDesserts = updatedDesserts
                )
            }
            
            collection.document(finalHuarique.id).set(finalHuarique).await()
            Result.success(finalHuarique.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteHuarique(id: String): Result<Unit> {
        return try {
            collection.document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveReview(huariqueId: String, review: Review): Result<Unit> {
        return try {
            val huarique = findByIdAsync(huariqueId) ?: throw Exception("Huarique no encontrado")
            
            // Lógica inteligente: Si el ID ya existe, reemplazamos. Si no, añadimos.
            val currentReviews = huarique.reviews.toMutableList()
            val existingIndex = currentReviews.indexOfFirst { it.id == review.id }
            
            if (existingIndex != -1) {
                currentReviews[existingIndex] = review // Editamos la existente
            } else {
                currentReviews.add(review) // Añadimos la nueva
            }
            
            val updatedReviews = currentReviews.toList()
            
            // Calcular el nuevo promedio de estrellas
            val newRating = if (updatedReviews.isNotEmpty()) {
                val average = updatedReviews.map { it.rating }.average()
                (Math.round(average * 10.0) / 10.0).toDouble()
            } else {
                0.0
            }

            val updates = mutableMapOf<String, Any>(
                "reviews" to updatedReviews,
                "rating" to newRating
            )

            collection.document(huariqueId).set(updates, com.google.firebase.firestore.SetOptions.merge()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteReview(huariqueId: String, reviewId: String): Result<Unit> {
        return try {
            val huarique = findByIdAsync(huariqueId) ?: throw Exception("Huarique no encontrado")
            val updatedReviews = huarique.reviews.filter { it.id != reviewId }
            
            // Recalcular promedio tras eliminar
            val newRating = if (updatedReviews.isNotEmpty()) {
                val average = updatedReviews.map { it.rating }.average()
                Math.round(average * 10.0) / 10.0
            } else {
                0.0
            }

            val updates = mapOf(
                "reviews" to updatedReviews,
                "rating" to newRating
            )

            collection.document(huariqueId).update(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun findByIdAsync(id: String): Huarique? {
        return try {
            val doc = collection.document(id).get().await()
            doc.toObject(Huarique::class.java)
        } catch (e: Exception) {
            null
        }
    }
}
