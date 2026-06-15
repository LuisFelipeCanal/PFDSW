package com.mercadovivo.app.data

import com.google.firebase.firestore.FirebaseFirestore
import com.mercadovivo.app.models.Huarique
import com.mercadovivo.app.models.Plato
import com.mercadovivo.app.models.Review
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class HuariqueRepository {
    private val db = FirebaseFirestore.getInstance()
    private val collection = db.collection("huariques")

    private val mockHuariques = listOf(
        Huarique(
            id = "1",
            name = "El Rincón Criollo",
            description = "Auténtica comida criolla peruana con recetas tradicionales de generación en generación.",
            address = "Jr. Ancash 456, Cercado de Lima",
            district = "Lima",
            lat = -12.0431,
            lng = -77.0282,
            rating = 4.8,
            categories = listOf("Comida Criolla"),
            horario = "8:00 AM - 6:00 PM",
            phone = "+51 987 654 321",
            photos = listOf("https://images.unsplash.com/photo-1594212699903-ec8a3eca50f5?q=80&w=1000"),
            menuPlates = listOf(
                Plato(id="p1", name="Lomo Saltado", price=18.50, rating=4.9, description="Carne de res salteada con cebolla, tomate, ají y papas fritas", ingredients=listOf("Carne de res", "Cebolla", "Tomate", "Ají amarillo", "Papas", "Salsa de soya", "Vinagre")),
                Plato(id="p2", name="Ají de Gallina", price=15.00, rating=4.8, description="Pollo deshilachado en crema de ají amarillo con nueces", ingredients=listOf("Pollo", "Ají amarillo", "Pan", "Leche", "Nueces"))
            ),
            isVerified = true
        )
    )

    fun getHuariques(): List<Huarique> = mockHuariques

    fun getHuariquesFlow(): Flow<List<Huarique>> = flow {
        // Emitimos los locales primero para que la app no espere y no se "rompa" el canal
        emit(mockHuariques)
        try {
            val snapshot = collection.get().await()
            val list = snapshot.toObjects(Huarique::class.java)
            if (list.isNotEmpty()) emit(list)
        } catch (e: Exception) {
            // Si falla Firebase, los mocks ya están emitidos
        }
    }

    suspend fun saveHuarique(huarique: Huarique): Result<String> {
        return try {
            val docRef = if (huarique.id.isEmpty()) {
                collection.add(huarique).await()
            } else {
                collection.document(huarique.id).set(huarique).await()
                collection.document(huarique.id)
            }
            Result.success(docRef.id)
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

    fun findById(id: String): Huarique? = mockHuariques.firstOrNull { it.id == id }
    
    suspend fun findByIdAsync(id: String): Huarique? {
        return try {
            collection.document(id).get().await().toObject(Huarique::class.java)
        } catch (e: Exception) {
            findById(id)
        }
    }
}
