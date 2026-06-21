package com.mercadovivo.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.google.android.gms.maps.model.LatLng
import com.mercadovivo.app.auth.AuthViewModel
import com.mercadovivo.app.ui.theme.MercadoVivoGradientEnd
import com.mercadovivo.app.ui.theme.MercadoVivoGradientStart
import com.mercadovivo.app.utils.LocationUtils
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    huariqueViewModel: HuariqueViewModel,
    authViewModel: AuthViewModel,
    onBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Actividad", "Configurar")
    
    val huariques = huariqueViewModel.huariques
    val userLocation = huariqueViewModel.userLocation
    val userData = authViewModel.userData
    val lastReadAt = userData?.lastNotificationsReadAt ?: 0L
    val readNotificationIds = userData?.readNotificationIds ?: emptyList()

    // Generamos notificaciones "reales" basadas en los huariques verificados
    val notifications = remember(huariques, userLocation, lastReadAt, readNotificationIds) {
        huariques.filter { it.isVerified }.sortedByDescending { it.createdAt }.map { huarique ->
            var isUltraNear = false
            val distanceText = if (userLocation != null && huarique.lat != null && huarique.lng != null) {
                val meters = LocationUtils.calculateDistance(userLocation, LatLng(huarique.lat, huarique.lng))
                isUltraNear = meters <= 200f // Radio de 200 metros
                "a ${LocationUtils.formatDistance(meters)} de ti"
            } else {
                ""
            }

            val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
            val timeStr = sdf.format(Date(huarique.createdAt))

            val isNewNotification = if (huarique.createdAt < 1000000000000L) {
                lastReadAt == 0L
            } else {
                huarique.createdAt > lastReadAt
            }
            
            val isActuallyNew = isNewNotification && 
                                !readNotificationIds.contains(huarique.id) &&
                                huarique.createdAt > (System.currentTimeMillis() - 172800000)

            NotificationData(
                id = huarique.id,
                title = if (isUltraNear) "¡Nuevo Huarique a la vuelta!" else "¡Nuevo Huarique abierto!",
                content = if (isUltraNear) 
                    "${huarique.name} está a solo pasos de tu ubicación actual. ¡Ven a probar su sazón!" 
                    else "${huarique.name} ya está disponible $distanceText. ¡No te lo pierdas!",
                time = timeStr,
                icon = Icons.Default.Storefront,
                isNew = isActuallyNew,
                iconColor = if (isUltraNear) Color(0xFF4CAF50) else Color(0xFFF9B36D)
            )
        }
    }

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
                            Text("${notifications.count { it.isNew }} nuevas hoy", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                        }
                    }
                    Surface(modifier = Modifier.size(40.dp), shape = CircleShape, color = Color.White.copy(alpha = 0.2f)) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.White)
                            if (notifications.any { it.isNew }) {
                                Surface(modifier = Modifier.align(Alignment.TopEnd).size(12.dp), shape = CircleShape, color = Color(0xFFD4183D)) {}
                            }
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
            ActivityTab(
                items = notifications,
                onMarkAllAsRead = { authViewModel.markNotificationsAsRead() },
                onItemClick = { authViewModel.markNotificationAsRead(it) }
            )
        } else {
            ConfigTab(
                pushEnabled = userData?.pushNotificationsEnabled ?: true,
                nearEnabled = userData?.nearHuariqueNotificationsEnabled ?: true,
                onTogglePush = { authViewModel.togglePushNotifications(it) },
                onToggleNear = { authViewModel.toggleNearHuariqueNotifications(it) }
            )
        }
    }
}

@Composable
fun ActivityTab(
    items: List<NotificationData>, 
    onMarkAllAsRead: () -> Unit,
    onItemClick: (String) -> Unit
) {
    val hasUnread = items.any { it.isNew }

    Column(modifier = Modifier.padding(24.dp)) {
        if (items.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No hay notificaciones recientes", color = Color.Gray)
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth().height(40.dp), 
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (hasUnread) {
                    Surface(
                        onClick = onMarkAllAsRead,
                        shape = RoundedCornerShape(20.dp),
                        color = Color(0xFFFDEEE9)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.DoneAll,
                                contentDescription = null,
                                tint = Color(0xFFE27553),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Marcar todo como visto",
                                color = Color(0xFFE27553),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(items) { data ->
                    ActivityCard(data, onClick = { onItemClick(data.id) })
                }
                item { Spacer(modifier = Modifier.height(100.dp)) }
            }
        }
    }
}

@Composable
fun ActivityCard(data: NotificationData, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (data.isNew) Color(0xFFFFF7F2) else Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (data.isNew) 2.dp else 0.dp),
        border = if (data.isNew) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDEEE9)) else null
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Surface(
                modifier = Modifier.size(40.dp), 
                shape = CircleShape, 
                color = data.iconColor.copy(alpha = 0.1f)
            ) {
                Icon(data.icon, contentDescription = null, tint = data.iconColor, modifier = Modifier.padding(8.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        text = data.title, 
                        fontWeight = if (data.isNew) FontWeight.Bold else FontWeight.Medium,
                        color = if(data.isNew) Color(0xFFE27553) else Color.Black
                    )
                    if (data.isNew) {
                        Surface(modifier = Modifier.size(8.dp), shape = CircleShape, color = Color(0xFFD4183D)) {}
                    }
                }
                Text(data.content, fontSize = 12.sp, color = if (data.isNew) Color.Black else Color.Gray)
                Text(data.time, fontSize = 10.sp, color = Color.LightGray)
            }
        }
    }
}

@Composable
fun ConfigTab(pushEnabled: Boolean, nearEnabled: Boolean, onTogglePush: (Boolean) -> Unit, onToggleNear: (Boolean) -> Unit) {
    Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Configura tus alertas", fontSize = 14.sp, color = Color.Gray)
        
        ConfigItem("Notificaciones al celular", "Recibe alertas de nuevos locales incluso con la app cerrada", pushEnabled, onTogglePush)
        ConfigItem("Nuevos lugares cercanos", "Huariques que abren a menos de 200m de tu zona", nearEnabled, onToggleNear)
    }
}

@Composable
fun ConfigItem(title: String, subtitle: String, initialValue: Boolean, onCheckedChange: ((Boolean) -> Unit)? = null) {
    var checked by remember(initialValue) { mutableStateOf(initialValue) }
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
                onCheckedChange = { 
                    checked = it
                    onCheckedChange?.invoke(it)
                },
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

data class NotificationData(val id: String, val title: String, val content: String, val time: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val isNew: Boolean, val iconColor: Color)
