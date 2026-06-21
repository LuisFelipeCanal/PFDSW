package com.mercadovivo.app.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.mercadovivo.app.models.UserData
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel(private val repository: AuthRepository = AuthRepository()) : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var isAdmin by mutableStateOf(false)
        private set

    var userRole by mutableStateOf("USER")
        private set

    var currentUser by mutableStateOf(repository.getCurrentUser())
        private set

    var userData by mutableStateOf<UserData?>(null)
        private set

    val userId get() = currentUser?.uid

    init {
        checkAdminStatus()
        loadUserData()
    }

    fun refresh() {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            userData = repository.getUserData()
        }
    }

    private fun checkAdminStatus() {
        val uid = userId ?: return
        viewModelScope.launch {
            try {
                val doc = FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(uid)
                    .get()
                    .await()
                
                // Soporte para el campo antiguo isAdmin y el nuevo campo role
                userRole = doc.getString("role") ?: "USER"
                isAdmin = userRole == "ADMIN" || (doc.getBoolean("isAdmin") ?: false)
            } catch (e: Exception) {
                isAdmin = false
                userRole = "USER"
            }
        }
    }

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            repository.login(email, password)
                .onSuccess { 
                    currentUser = repository.getCurrentUser()
                    checkAdminStatus()
                    loadUserData()
                    onSuccess() 
                }
                .onFailure { errorMessage = it.message ?: "Error desconocido" }
            isLoading = false
        }
    }

    fun register(email: String, password: String, displayName: String, phone: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            repository.register(email, password, displayName, phone)
                .onSuccess { 
                    currentUser = repository.getCurrentUser()
                    checkAdminStatus()
                    loadUserData()
                    onSuccess() 
                }
                .onFailure { errorMessage = it.message ?: "Error desconocido" }
            isLoading = false
        }
    }

    fun logout() {
        repository.logout()
        currentUser = null
        isAdmin = false
    }

    // Manual admin activation for the user's specific request
    fun activateAdminMode(password: String): Boolean {
        if (password == "AdminMercado2024") { // Temporary admin password
            isAdmin = true
            return true
        }
        return false
    }

    fun exitAdminMode() {
        isAdmin = false
        // Opcional: Si queremos que persista que NO es admin en esta sesión
        // incluso si en Firestore dice que sí, podríamos manejar una bandera extra.
    }

    fun changePassword(current: String, new: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val result = repository.changePassword(current, new)
            onResult(result)
        }
    }

    fun updateProfile(name: String, phone: String, bio: String, photoUrl: String = "", onResult: (Result<Unit>) -> Unit) {
        val uid = userId ?: return
        viewModelScope.launch {
            isLoading = true
            try {
                // 1. Subir imagen si es una URI local nueva
                var finalPhotoUrl = photoUrl
                if (photoUrl.startsWith("content://")) {
                    finalPhotoUrl = repository.uploadProfileImage(android.net.Uri.parse(photoUrl))
                }

                // 2. Actualizar en Firebase Auth si el nombre cambió
                if (name != currentUser?.displayName) {
                    repository.updateDisplayName(name)
                }

                // 3. Actualizar o Crear en Firestore
                val data = mutableMapOf(
                    "displayName" to name,
                    "phone" to phone,
                    "bio" to bio
                )
                if (finalPhotoUrl.isNotEmpty()) {
                    data["photoUrl"] = finalPhotoUrl
                }

                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(uid)
                    .set(data, com.google.firebase.firestore.SetOptions.merge())
                    .await()
                
                currentUser = repository.getCurrentUser()
                loadUserData() // Recargar datos locales
                onResult(Result.success(Unit))
            } catch (e: Exception) {
                onResult(Result.failure(e))
            } finally {
                isLoading = false
            }
        }
    }

    fun togglePushNotifications(enabled: Boolean) {
        val uid = userId ?: return
        viewModelScope.launch {
            try {
                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(uid)
                    .update("pushNotificationsEnabled", enabled)
                    .await()
                loadUserData()
            } catch (e: Exception) {
                // Manejar error
            }
        }
    }

    fun toggleNearHuariqueNotifications(enabled: Boolean) {
        val uid = userId ?: return
        viewModelScope.launch {
            try {
                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(uid)
                    .update("nearHuariqueNotificationsEnabled", enabled)
                    .await()
                loadUserData()
            } catch (e: Exception) {
                // Manejar error
            }
        }
    }

    fun markNotificationsAsRead() {
        val uid = userId ?: return
        val now = System.currentTimeMillis()
        viewModelScope.launch {
            try {
                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(uid)
                    .update("lastNotificationsReadAt", now)
                    .await()
                loadUserData()
            } catch (e: Exception) {
                // Si falla porque el doc no existe, usamos set con merge
                val data = mapOf("lastNotificationsReadAt" to now)
                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(uid)
                    .set(data, com.google.firebase.firestore.SetOptions.merge())
                    .await()
                loadUserData()
            }
        }
    }

    fun recordVisit(huariqueId: String) {
        val uid = userId ?: return
        val currentVisits = userData?.visitedHuariques?.toMutableList() ?: mutableListOf()
        if (!currentVisits.contains(huariqueId)) {
            currentVisits.add(huariqueId)
            viewModelScope.launch {
                try {
                    FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(uid)
                        .update("visitedHuariques", currentVisits)
                        .await()
                    loadUserData()
                } catch (e: Exception) { }
            }
        }
    }

    fun toggleFavorite(huariqueId: String) {
        val uid = userId ?: return
        val currentFavorites = userData?.favorites?.toMutableList() ?: mutableListOf()
        
        if (currentFavorites.contains(huariqueId)) {
            currentFavorites.remove(huariqueId)
        } else {
            currentFavorites.add(huariqueId)
        }

        viewModelScope.launch {
            try {
                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(uid)
                    .update("favorites", currentFavorites)
                    .await()
                loadUserData()
            } catch (e: Exception) {
                // Manejar error
            }
        }
    }

    fun toggleDishFavorite(dishId: String) {
        val uid = userId ?: return
        val currentFavorites = userData?.favoriteDishes?.toMutableList() ?: mutableListOf()
        
        if (currentFavorites.contains(dishId)) {
            currentFavorites.remove(dishId)
        } else {
            currentFavorites.add(dishId)
        }

        viewModelScope.launch {
            try {
                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(uid)
                    .update("favoriteDishes", currentFavorites)
                    .await()
                loadUserData()
            } catch (e: Exception) {
                // Manejar error
            }
        }
    }
}
