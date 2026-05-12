package com.mercadovivo.app.auth

// Repositorio de autenticación mock para el MVP.
// Cuando integres Firebase, reemplaza estos métodos por FirebaseAuth + Firestore.
class AuthRepository {

    suspend fun register(email: String, password: String, displayName: String): Result<String> {
        return try {
            if (email.isBlank() || password.isBlank() || displayName.isBlank()) {
                throw IllegalArgumentException("Complete todos los campos")
            }
            Result.success("mock-uid-123")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<String> {
        return try {
            if (email.isBlank() || password.isBlank()) {
                throw IllegalArgumentException("Complete email y contraseña")
            }
            Result.success("mock-uid-123")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        // Aquí iría FirebaseAuth.getInstance().signOut()
    }
}
