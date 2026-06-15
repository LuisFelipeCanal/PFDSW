package com.mercadovivo.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.mercadovivo.app.ui.theme.MercadoVivoGradientEnd
import com.mercadovivo.app.ui.theme.MercadoVivoGradientStart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(onBack: () -> Unit) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Actividad", "Configurar")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFBF0))
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(MercadoVivoGradientStart, MercadoVivoGradientEnd)
                    ),
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                )
                .padding(24.dp)
        ) {
            Column {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Row {
                        IconButton(onClick = onBack, modifier = Modifier.size(40.dp).background(Color.White.copy(alpha = 0.2f), CircleShape)) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Notificaciones", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Text("2 sin leer", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                        }
                    }
                    Surface(modifier = Modifier.size(40.dp), shape = CircleShape, color = Color.White.copy(alpha = 0.2f)) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.White)
                            Surface(modifier = Modifier.align(Alignment.TopEnd).size(12.dp), shape = CircleShape, color = Color(0xFFD4183D)) {}
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.White.copy(alpha = 0.2f),
                    contentColor = Color.White,
                    modifier = Modifier.height(48.dp).clip(RoundedCornerShape(24.dp)),
                    indicator = { Box(modifier = Modifier.fillMaxSize().padding(4.dp).background(Color.White, RoundedCornerShape(20.dp))) },
                    divider = {}
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { 
                                Text(
                                    title, 
                                    color = if (selectedTab == index) Color(0xFFE27553) else Color.White,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.zIndex(1f)
                                ) 
                            }
                        )
                    }
                }
            }
        }

        if (selectedTab == 0) {
            ActivityTab()
        } else {
            ConfigTab()
        }
    }
}

@Composable
fun ActivityTab() {
    val items = listOf(
        NotificationData("¡Oferta especial!", "Huarique El Rincón Criollo tiene 20% de descuento en su menú de hoy.", "Hace 5 min", Icons.Default.LocalOffer, true, Color(0xFFE27553)),
        NotificationData("Nuevo huarique cerca tuyo", "La Cevichería de Don Paco abrió a 500 m de tu ubicación habitual.", "Hace 30 min", Icons.Default.Star, true, Color(0xFFF9B36D)),
        NotificationData("Tu reseña fue valorada", "3 personas encontraron útil tu reseña de \"La Picantería del Mercado\".", "Hace 2 h", Icons.Default.NotificationsActive, false, Color(0xFFE27553)),
        NotificationData("Mercado abierto", "El Mercado Surquillo ya está abierto. ¡Descubre sus huariques de hoy!", "Hace 5 h", Icons.Default.LocationOn, false, Color(0xFF4CAF50)),
        NotificationData("Festival Gastronómico", "Este fin de semana: Festival de Comida Fusión en el Mercado Central.", "Ayer", Icons.Default.Campaign, false, Color(0xFF9C27B0))
    )

    Column(modifier = Modifier.padding(24.dp)) {
        Text("Marcar todo como leído", color = Color(0xFFE27553), modifier = Modifier.align(Alignment.End), fontSize = 12.sp)
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            items(items) { data ->
                ActivityCard(data)
            }
            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }
}

@Composable
fun ActivityCard(data: NotificationData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Surface(modifier = Modifier.size(40.dp), shape = CircleShape, color = data.iconColor.copy(alpha = 0.1f)) {
                Icon(data.icon, contentDescription = null, tint = data.iconColor, modifier = Modifier.padding(8.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(data.title, fontWeight = FontWeight.Bold, color = if(data.isNew) data.iconColor else Color.Black)
                    if (data.isNew) {
                        Surface(modifier = Modifier.size(6.dp), shape = CircleShape, color = Color(0xFFD4183D)) {}
                    }
                }
                Text(data.content, fontSize = 12.sp, color = Color.Gray)
                Text(data.time, fontSize = 10.sp, color = Color.LightGray)
            }
        }
    }
}

@Composable
fun ConfigTab() {
    Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Elige qué notificaciones quieres recibir", fontSize = 14.sp, color = Color.Gray)
        
        ConfigItem("Ofertas y descuentos", "Promociones de huariques cerca de ti", true)
        ConfigItem("Nuevos lugares cercanos", "Huariques que abren cerca de tu zona", true)
        ConfigItem("Actividad en reseñas", "Valoraciones de tus comentarios", false)
        ConfigItem("Eventos gastronómicos", "Festivales y eventos en mercados", true)
    }
}

@Composable
fun ConfigItem(title: String, subtitle: String, initialValue: Boolean) {
    var checked by remember { mutableStateOf(initialValue) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold)
                Text(subtitle, fontSize = 12.sp, color = Color.Gray)
            }
            Switch(
                checked = checked, 
                onCheckedChange = { checked = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFFE27553),
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color.LightGray.copy(alpha = 0.5f)
                )
            )
        }
    }
}

data class NotificationData(val title: String, val content: String, val time: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val isNew: Boolean, val iconColor: Color)
