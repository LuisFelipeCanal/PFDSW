package com.mercadovivo.app.data

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.mercadovivo.app.models.Huarique
import com.mercadovivo.app.models.Review
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class HuariqueRepository {
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val collection = db.collection("huariques")

    fun getHuariquesFlow(): Flow<List<Huarique>> = callbackFlow {
        val subscription = collection.addSnapshotListener { snapshot, error ->
            if (error == null && snapshot != null) {
                val firebaseList = snapshot.toObjects(Huarique::class.java)
                trySend(firebaseList)
            }
        }
        awaitClose { subscription.remove() }
    }

    suspend fun uploadImage(context: android.content.Context, imageUri: Uri): String {
        val fileName = UUID.randomUUID().toString()
        val ref = storage.reference.child("huariques_photos/$fileName")
        
        // COMPRESIÓN DE IMAGEN ANTES DE SUBIR
        val inputStream = context.contentResolver.openInputStream(imageUri)
        val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
        val baos = java.io.ByteArrayOutputStream()
        
        // Reducimos la calidad al 70% para que pese poco pero se vea bien
        bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 70, baos)
        val data = baos.toByteArray()
        
        ref.putBytes(data).await()
        return ref.downloadUrl.await().toString()
    }

    suspend fun saveHuarique(context: android.content.Context, huarique: Huarique, imageUris: List<Uri> = emptyList()): Result<String> {
        return try {
            // 1. Subir fotos generales del local comprimidas
            val uploadedPhotoUrls = imageUris.map { uploadImage(context, it) }
            
            // 2. Subir fotos individuales de cada plato/bebida/postre comprimidas
            val updatedPlates = huarique.menuPlates.map { plate ->
                if (plate.photoLabel.startsWith("content://")) {
                    plate.copy(photoLabel = uploadImage(context, Uri.parse(plate.photoLabel)))
                } else plate
            }
            val updatedBeverages = huarique.menuBeverages.map { bev ->
                if (bev.photoLabel.startsWith("content://")) {
                    bev.copy(photoLabel = uploadImage(context, Uri.parse(bev.photoLabel)))
                } else bev
            }
            val updatedDesserts = huarique.menuDesserts.map { des ->
                if (des.photoLabel.startsWith("content://")) {
                    des.copy(photoLabel = uploadImage(context, Uri.parse(des.photoLabel)))
                } else des
            }

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
