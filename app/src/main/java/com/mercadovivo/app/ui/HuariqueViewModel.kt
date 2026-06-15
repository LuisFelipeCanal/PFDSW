package com.mercadovivo.app.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mercadovivo.app.data.HuariqueRepository
import com.mercadovivo.app.models.Huarique
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HuariqueViewModel(private val repository: HuariqueRepository = HuariqueRepository()) : ViewModel() {

    var huariques by mutableStateOf<List<Huarique>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    init {
        loadHuariques()
    }

    private fun loadHuariques() {
        viewModelScope.launch {
            isLoading = true
            repository.getHuariquesFlow().collectLatest {
                huariques = it
                isLoading = false
            }
        }
    }

    fun findById(id: String): Huarique? {
        return huariques.firstOrNull { it.id == id }
    }
}
