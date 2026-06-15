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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mercadovivo.app.ui.theme.MercadoVivoGradientEnd
import com.mercadovivo.app.ui.theme.MercadoVivoGradientStart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressesScreen(onBack: () -> Unit) {
    val addresses = listOf(
        AddressItem("Casa", "Jr. Las Flores 245, Dpto. 3B", "Miraflores, Lima", true, Icons.Default.Home),
        AddressItem("Universidad", "Av. Universitaria 1801", "San Miguel, Lima", false, Icons.Default.School)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFBF0))
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(MercadoVivoGradientStart, MercadoVivoGradientEnd)
                    ),
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                )
                .padding(24.dp)
        ) {
            Row {
                IconButton(onClick = onBack, modifier = Modifier.size(40.dp).background(Color.White.copy(alpha = 0.2f), CircleShape)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Mis direcciones", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Lugares frecuentes guardados", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                // Map Placeholder Card
                Card(
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)) // Light green for grid feel
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFE27553))
                            Text("Tus ubicaciones guardadas", fontSize = 14.sp, color = Color(0xFF33691E))
                        }
                    }
                }
            }

            items(addresses) { address ->
                AddressCard(address)
            }

            item {
                OutlinedButton(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFFE27553))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Agregar dirección", color = Color(0xFFE27553))
                }
            }
            
            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }
}

@Composable
fun AddressCard(item: AddressItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(modifier = Modifier.size(40.dp), shape = CircleShape, color = Color(0xFFFDEEE9)) {
                    Icon(item.icon, contentDescription = null, tint = Color(0xFFE27553), modifier = Modifier.padding(8.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(item.name, fontWeight = FontWeight.Bold)
                        if (item.isDefault) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(color = Color(0xFFFDEEE9), shape = RoundedCornerShape(8.dp)) {
                                Text("Predeterminada", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), fontSize = 10.sp, color = Color(0xFFE27553))
                            }
                        }
                    }
                    Text(item.address, fontSize = 12.sp)
                    Text(item.district, fontSize = 12.sp, color = Color.Gray)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color.LightGray.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                if (!item.isDefault) {
                    TextButton(onClick = { }) {
                        Text("✓ Usar por defecto", color = Color(0xFFE27553), fontSize = 12.sp)
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }
                
                Surface(color = Color(0xFFFDEEE9), shape = RoundedCornerShape(8.dp)) {
                   Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                       Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color(0xFFD4183D))
                       Spacer(modifier = Modifier.width(4.dp))
                       Text("Eliminar", color = Color(0xFFD4183D), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                   }
                }
            }
        }
    }
}

data class AddressItem(val name: String, val address: String, val district: String, val isDefault: Boolean, val icon: androidx.compose.ui.graphics.vector.ImageVector)
