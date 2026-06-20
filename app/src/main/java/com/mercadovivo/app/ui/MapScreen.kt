package com.mercadovivo.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.shape.RoundedCornerShape
import com.google.android.gms.maps.CameraUpdateFactory
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.mercadovivo.app.models.Huarique
import com.mercadovivo.app.ui.components.HuariqueCard
import com.mercadovivo.app.ui.components.SectionHeader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    huariques: List<Huarique>,
    huariqueViewModel: HuariqueViewModel,
    selectedId: String = "all",
    onOpenDetail: (String) -> Unit = {},
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val verifiedHuariques = huariques.filter { it.isVerified }
    val userLocation = huariqueViewModel.userLocation
    val isLocationEnabled = huariqueViewModel.isLocationEnabled
    
    // Estado local para manejar la selección actual dentro del mapa
    var currentFocusedId by remember { mutableStateOf(if (selectedId == "all") "" else selectedId) }

    // Ordenamos la lista: 
    // 1. Si hay uno seleccionado, va PRIMERO.
    // 2. El resto se ordena por cercanía si hay ubicación.
    val sortedHuariques = remember(verifiedHuariques, userLocation, currentFocusedId) {
        val baseList = if (userLocation != null) {
            verifiedHuariques.map { huarique ->
                val distance = if (huarique.lat != null && huarique.lng != null) {
                    com.mercadovivo.app.utils.LocationUtils.calculateDistance(
                        userLocation,
                        LatLng(huarique.lat, huarique.lng)
                    )
                } else Float.MAX_VALUE
                huarique to distance
            }.sortedBy { it.second }.map { it.first }
        } else {
            verifiedHuariques
        }

        if (currentFocusedId.isNotEmpty()) {
            val focused = verifiedHuariques.find { it.id == currentFocusedId }
            if (focused != null) {
                // Ponemos el seleccionado al inicio de la lista
                listOf(focused) + baseList.filter { it.id != currentFocusedId }
            } else baseList
        } else {
            baseList
        }
    }

    val focusedHuarique = verifiedHuariques.find { it.id == currentFocusedId }
    val initialPos = LatLng(focusedHuarique?.lat ?: -12.1191, focusedHuarique?.lng ?: -77.0349)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(initialPos, 15f)
    }

    // Efecto para centrar el mapa cuando cambia el foco
    LaunchedEffect(currentFocusedId) {
        val target = verifiedHuariques.find { it.id == currentFocusedId }
        target?.let {
            if (it.lat != null && it.lng != null) {
                cameraPositionState.animate(
                    CameraUpdateFactory.newLatLngZoom(LatLng(it.lat, it.lng), 17f)
                )
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (currentFocusedId.isNotEmpty()) focusedHuarique?.name ?: "Ubicación" else "Mapa de huariques", fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            Box(modifier = Modifier.fillMaxWidth().weight(0.5f)) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(isMyLocationEnabled = isLocationEnabled),
                    uiSettings = MapUiSettings(zoomControlsEnabled = true, myLocationButtonEnabled = isLocationEnabled)
                ) {
                    verifiedHuariques.forEach { huarique ->
                        if (huarique.lat != null && huarique.lng != null) {
                            Marker(
                                state = MarkerState(position = LatLng(huarique.lat, huarique.lng)),
                                title = huarique.name,
                                snippet = huarique.address,
                                onClick = {
                                    if (currentFocusedId == huarique.id) {
                                        onOpenDetail(huarique.id)
                                    } else {
                                        currentFocusedId = huarique.id
                                    }
                                    true
                                }
                            )
                        }
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(0.5f).padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    SectionHeader(
                        title = if (currentFocusedId.isNotEmpty()) "Seleccionado" else "Huariques cercanos"
                    )
                }

                items(sortedHuariques) { huarique ->
                    val isSelected = huarique.id == currentFocusedId
                    
                    Column {
                        HuariqueCard(
                            huarique = huarique,
                            onClick = { 
                                if (isSelected) {
                                    onOpenDetail(huarique.id)
                                } else {
                                    currentFocusedId = huarique.id
                                }
                            },
                            userLocation = userLocation,
                            selected = isSelected
                        )
                        
                        if (isSelected) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    val gmmIntentUri = Uri.parse("google.navigation:q=${huarique.lat},${huarique.lng}")
                                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                                    mapIntent.setPackage("com.google.android.apps.maps")
                                    context.startActivity(mapIntent)
                                },
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Navigation, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Cómo llegar (Abrir GPS)", fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
                
                item { Spacer(modifier = Modifier.height(20.dp)) }
            }
        }
    }
}
