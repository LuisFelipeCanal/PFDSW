package com.mercadovivo.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.mercadovivo.app.models.Huarique
import com.mercadovivo.app.ui.components.HuariqueCard
import com.mercadovivo.app.ui.components.MarketTopBar
import com.mercadovivo.app.ui.components.SectionHeader

@Composable
fun MapScreen(
    huariques: List<Huarique>,
    selectedId: String = "all",
    onOpenDetail: (String) -> Unit = {},
    onBack: () -> Unit = {}
) {
    val selected = huariques.firstOrNull { it.id == selectedId } ?: huariques.firstOrNull()
    
    val initialPos = LatLng(selected?.lat ?: -12.1191, selected?.lng ?: -77.0349)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialPos, 15f)
    }

    // Update camera if selection changes
    LaunchedEffect(selectedId) {
        selected?.let {
            if (it.lat != null && it.lng != null) {
                cameraPositionState.position = CameraPosition.fromLatLngZoom(LatLng(it.lat, it.lng), 15f)
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        MarketTopBar(
            title = "Mapa de huariques",
            subtitle = "Encuentra el mejor sabor",
            onBack = onBack
        )

        Box(modifier = Modifier
            .fillMaxWidth()
            .weight(0.4f)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(isMyLocationEnabled = false),
                uiSettings = MapUiSettings(zoomControlsEnabled = true)
            ) {
                huariques.forEach { huarique ->
                    if (huarique.lat != null && huarique.lng != null) {
                        Marker(
                            state = MarkerState(position = LatLng(huarique.lat, huarique.lng)),
                            title = huarique.name,
                            snippet = huarique.address,
                            onClick = {
                                onOpenDetail(huarique.id)
                                true
                            }
                        )
                    }
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.6f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                SectionHeader(title = "Huariques cercanos")
            }

            items(huariques) { huarique ->
                HuariqueCard(
                    huarique = huarique,
                    onClick = { onOpenDetail(huarique.id) },
                    selected = huarique.id == selectedId
                )
            }
        }
    }
}
