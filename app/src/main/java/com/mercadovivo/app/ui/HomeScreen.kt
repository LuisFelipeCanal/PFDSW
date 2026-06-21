package com.mercadovivo.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.android.gms.maps.model.LatLng
import com.mercadovivo.app.models.Huarique
import com.mercadovivo.app.ui.components.MarketBottomNavigationBar
import com.mercadovivo.app.ui.theme.MercadoVivoGradientEnd
import com.mercadovivo.app.ui.theme.MercadoVivoGradientStart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    huariques: List<Huarique>,
    userLocation: LatLng? = null,
    isLoading: Boolean = false,
    isAdmin: Boolean = false,
    onOpenDetail: (String) -> Unit,
    onOpenMap: (String) -> Unit,
    onRegisterLocal: () -> Unit,
    onOpenAdminPanel: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val categories = listOf("Todos", "Comida Criolla", "Cevicheria", "Parrillas", "Polleria", "Comida Selva", "Chifa", "Postres", "Jugueria", "Bebidas", "Pescados y Mariscos", "Sandwiches", "Caldos y Sopas")
    var selectedCategory by remember { mutableStateOf("Todos") }

    // Lógica de filtrado avanzada (Búsqueda + Categoría)
    val filteredHuariques = remember(huariques, searchQuery, selectedCategory) {
        huariques.filter { huarique ->
            val matchesVerified = huarique.isVerified
            val matchesCategory = selectedCategory == "Todos" || huarique.categories.contains(selectedCategory)
            
            val query = searchQuery.lowercase().trim()
            val matchesSearch = query.isEmpty() || 
                huarique.name.lowercase().contains(query) ||
                huarique.description.lowercase().contains(query) ||
                huarique.menuPlates.any { it.name.lowercase().contains(query) } ||
                huarique.menuBeverages.any { it.name.lowercase().contains(query) } ||
                huarique.menuDesserts.any { it.name.lowercase().contains(query) } ||
                huarique.categories.any { it.lowercase().contains(query) }

            matchesVerified && matchesCategory && matchesSearch
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFBF0))
    ) {
        // Header con Barra de Búsqueda Real
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(MercadoVivoGradientStart, MercadoVivoGradientEnd)
                    ),
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                )
                .padding(horizontal = 24.dp, vertical = 32.dp)
        ) {
            Column {
                Text(
                    text = "MercadoVivo",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Descubre los mejores huariques cerca de ti",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
                Spacer(modifier = Modifier.height(24.dp))
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Buscar huariques, platos...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    shape = RoundedCornerShape(24.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            }
        }

        if (isLoading && huariques.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFFE27553))
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Categorías", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.forEach { category ->
                            val isSelected = selectedCategory == category
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedCategory = category },
                                label = { Text(category) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFFE27553),
                                    selectedLabelColor = Color.White
                                ),
                                shape = RoundedCornerShape(20.dp)
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenMap("all") },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                modifier = Modifier.size(40.dp),
                                shape = RoundedCornerShape(20.dp),
                                color = Color(0xFFFDEEE9)
                            ) {
                                Icon(Icons.Default.Place, contentDescription = null, tint = Color(0xFFE27553), modifier = Modifier.padding(8.dp))
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text("Ver en mapa", fontWeight = FontWeight.Bold)
                                Text("Explora huariques cercanos", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }
                        }
                    }
                }

                if (!isAdmin) {
                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onRegisterLocal() },
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    modifier = Modifier.size(32.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFFFDEEE9)
                                ) {
                                    Icon(
                                        Icons.Default.Storefront,
                                        contentDescription = null,
                                        tint = Color(0xFFE27553),
                                        modifier = Modifier.padding(4.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        "Registra tu local",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = Color(0xFFE27553)
                                    )
                                    Text(
                                        "¿Eres dueño? Únete",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("📈", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (searchQuery.isEmpty() && selectedCategory == "Todos") "Populares cerca de ti" else "Resultados de búsqueda", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                }

                // Aplicar lógica de cercanía sobre los resultados ya filtrados por búsqueda
                val nearHuariques = if (userLocation != null) {
                    filteredHuariques.map { huarique ->
                        val distance = if (huarique.lat != null && huarique.lng != null) {
                            com.mercadovivo.app.utils.LocationUtils.calculateDistance(
                                userLocation, 
                                LatLng(huarique.lat, huarique.lng)
                            )
                        } else Float.MAX_VALUE
                        huarique to distance
                    }.sortedBy { it.second }
                     .map { it.first }
                } else {
                    filteredHuariques
                }

                if (nearHuariques.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("No se encontraron resultados", color = Color.Gray)
                        }
                    }
                } else {
                    items(nearHuariques) { huarique ->
                        HuariqueCardHome(
                            huarique = huarique, 
                            userLocation = userLocation,
                            onClick = { onOpenDetail(huarique.id) }
                        )
                    }
                }
                
                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun HuariqueCardHome(
    huarique: Huarique, 
    userLocation: LatLng? = null,
    onClick: () -> Unit
) {
    val distanceText = remember(huarique, userLocation) {
        if (userLocation != null && huarique.lat != null && huarique.lng != null) {
            val meters = com.mercadovivo.app.utils.LocationUtils.calculateDistance(
                userLocation,
                LatLng(huarique.lat, huarique.lng)
            )
            com.mercadovivo.app.utils.LocationUtils.formatDistance(meters)
        } else {
            "--- m"
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(160.dp)) {
                AsyncImage(
                    model = if (huarique.photos.isNotEmpty()) huarique.photos.first() else "https://placeholder.com/400",
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                
                val isOpen = com.mercadovivo.app.utils.TimeUtils.isStoreOpen(huarique.horario)
                
                Surface(
                    modifier = Modifier.padding(12.dp).align(Alignment.TopStart),
                    shape = RoundedCornerShape(8.dp),
                    color = if (isOpen) Color(0xFF4CAF50) else Color.Gray
                ) {
                    Text(
                        if (isOpen) "Abierto" else "Cerrado", 
                        color = Color.White, 
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), 
                        fontSize = 12.sp
                    )
                }
                
                Surface(
                    modifier = Modifier.padding(12.dp).align(Alignment.TopEnd),
                    shape = RoundedCornerShape(8.dp),
                    color = Color.White.copy(alpha = 0.9f)
                ) {
                    Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        val currentRating = huarique.rating ?: 0.0
                        Text(text = "⭐", fontSize = 10.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if(currentRating > 0) currentRating.toString() else "0.0",
                            fontWeight = FontWeight.Bold, 
                            fontSize = 12.sp,
                            color = Color.Black
                        )
                    }
                }
  }
            
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = huarique.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(text = huarique.description, style = MaterialTheme.typography.bodySmall, color = Color.Gray, maxLines = 2)
                Spacer(modifier = Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("📍", fontSize = 12.sp)
                        Text(" $distanceText", style = MaterialTheme.typography.bodySmall)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🕒", fontSize = 12.sp)
                        Text(" ${huarique.horario ?: "Consultar"}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
