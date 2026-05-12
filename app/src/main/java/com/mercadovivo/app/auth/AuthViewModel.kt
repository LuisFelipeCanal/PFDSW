package com.mercadovivo.app.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val repo: AuthRepository = AuthRepository()) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _userId = MutableStateFlow<String?>(null)
    val userId: StateFlow<String?> = _userId

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun register(email: String, password: String, displayName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val res = repo.register(email, password, displayName)
            if (res.isSuccess) {
                _userId.value = res.getOrNull()
            } else {
                _error.value = res.exceptionOrNull()?.localizedMessage
            }
            _isLoading.value = false
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val res = repo.login(email, password)
            if (res.isSuccess) {
                _userId.value = res.getOrNull()
            } else {
                _error.value = res.exceptionOrNull()?.localizedMessage
            }
            _isLoading.value = false
        }
    }

    fun logout() {
        repo.logout()
        _userId.value = null
    }
}

