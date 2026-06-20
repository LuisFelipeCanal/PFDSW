package com.mercadovivo.app.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.mercadovivo.app.models.UserData
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    suspend fun register(email: String, password: String, displayName: String): Result<String> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw Exception("Error al crear usuario")
            
            // Guardar datos adicionales en Firestore
            val userData = UserData(
                uid = user.uid,
                email = email,
                displayName = displayName,
                role = "USER"
            )
            db.collection("users").document(user.uid).set(userData).await()

            Result.success(user.uid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun login(email: String, password: String): Result<String> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw Exception("Error al iniciar sesión")
            Result.success(user.uid)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        auth.signOut()
    }

    fun getCurrentUser() = auth.currentUser

    suspend fun changePassword(currentPassword: String, newPassword: String): Result<Unit> {
        return try {
            val user = auth.currentUser ?: throw Exception("Usuario no autenticado")
            val credential = EmailAuthProvider.getCredential(user.email!!, currentPassword)
            user.reauthenticate(credential).await()
            user.updatePassword(newPassword).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateDisplayName(name: String) {
        val user = auth.currentUser ?: return
        val profileUpdates = com.google.firebase.auth.userProfileChangeRequest {
            displayName = name
        }
        user.updateProfile(profileUpdates).await()
    }

    suspend fun uploadProfileImage(imageUri: android.net.Uri): String {
        val uid = auth.currentUser?.uid ?: return ""
        val ref = com.google.firebase.storage.FirebaseStorage.getInstance().reference.child("users_photos/$uid")
        ref.putFile(imageUri).await()
        return ref.downloadUrl.await().toString()
    }

    suspend fun getUserData(): UserData? {
        val uid = auth.currentUser?.uid ?: return null
        return try {
            val doc = db.collection("users").document(uid).get().await()
            doc.toObject(UserData::class.java)
        } catch (e: Exception) {
            null
        }
    }
}
