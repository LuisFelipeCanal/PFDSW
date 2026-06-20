package com.mercadovivo.app.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.mercadovivo.app.data.HuariqueRepository
import com.mercadovivo.app.models.Huarique
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HuariqueViewModel(private val repository: HuariqueRepository = HuariqueRepository()) : ViewModel() {

    var huariques by mutableStateOf<List<Huarique>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var userLocation by mutableStateOf<LatLng?>(null)
    
    var isLocationEnabled by mutableStateOf(true)

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
        return huariques.find { it.id == id }
    }
}
