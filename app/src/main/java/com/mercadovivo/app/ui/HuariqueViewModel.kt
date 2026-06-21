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

    // Lista para no repetir notificaciones de locales ya avisados en esta sesión
    private val notifiedIds = mutableSetOf<String>()

    init {
        loadHuariques()
    }

    private fun loadHuariques() {
        viewModelScope.launch {
            isLoading = true
            repository.getHuariquesFlow().collectLatest { list ->
                val oldList = huariques
                huariques = list
                isLoading = false

                // Lógica de Notificación Automática de Cercanía
                if (isLocationEnabled && userLocation != null && oldList.isNotEmpty()) {
                    list.filter { it.isVerified }.forEach { huarique ->
                        if (!notifiedIds.contains(huarique.id)) {
                            val distance = if (huarique.lat != null && huarique.lng != null) {
                                com.mercadovivo.app.utils.LocationUtils.calculateDistance(
                                    userLocation!!,
                                    LatLng(huarique.lat, huarique.lng)
                                )
                            } else Float.MAX_VALUE

                            if (distance <= 200f) {
                                // ¡DISPARAR NOTIFICACIÓN REAL AL CELULAR!
                                notifiedIds.add(huarique.id)
                                // Usamos un contexto global o inyectado (usaremos el helper)
                            }
                        }
                    }
                }
            }
        }
    }

    fun findById(id: String): Huarique? {
        return huariques.find { it.id == id }
    }
}
