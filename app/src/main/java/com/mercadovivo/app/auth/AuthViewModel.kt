package com.mercadovivo.app.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel(private val repository: AuthRepository = AuthRepository()) : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    var isAdmin by mutableStateOf(false)
        private set

    var currentUser by mutableStateOf(repository.getCurrentUser())
        private set

    val userId get() = currentUser?.uid

    init {
        checkAdminStatus()
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
                isAdmin = doc.getBoolean("isAdmin") ?: false
            } catch (e: Exception) {
                isAdmin = false
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
                    onSuccess() 
                }
                .onFailure { errorMessage = it.message ?: "Error desconocido" }
            isLoading = false
        }
    }

    fun register(email: String, password: String, displayName: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            repository.register(email, password, displayName)
                .onSuccess { 
                    currentUser = repository.getCurrentUser()
                    checkAdminStatus()
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

    fun changePassword(current: String, new: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val result = repository.changePassword(current, new)
            onResult(result)
        }
    }
}
