package com.mercadovivo.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
fun AddressesScreen(
    huariqueViewModel: HuariqueViewModel,
    onBack: () -> Unit
) {
    val isLocationEnabled = huariqueViewModel.isLocationEnabled

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
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBack, 
                    modifier = Modifier.size(40.dp).background(Color.White.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Configuración de ubicación", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Gestiona tu privacidad y GPS", color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = if (isLocationEnabled) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                        ) {
                            Icon(
                                imageVector = if (isLocationEnabled) Icons.Default.LocationOn else Icons.Default.LocationOff,
                                contentDescription = null,
                                tint = if (isLocationEnabled) Color(0xFF2E7D32) else Color(0xFFC62828),
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = if (isLocationEnabled) "Ubicación activada" else "Ubicación desactivada",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = if (isLocationEnabled) "La app usa tu GPS para buscar huariques" else "Los resultados no serán por cercanía",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                    Switch(
                        checked = isLocationEnabled,
                        onCheckedChange = { 
                            huariqueViewModel.isLocationEnabled = it 
                            // Si se desactiva, limpiamos la ubicación guardada para dejar de usarla
                            if (!it) huariqueViewModel.userLocation = null
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF4CAF50)
                        )
                    )
                }
            }

            // Info Adicional
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFFDEEE9),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
                    Icon(
                        Icons.Default.GpsFixed,
                        contentDescription = null,
                        tint = Color(0xFFE27553),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Activar la ubicación nos permite mostrarte los huariques que están a solo unas cuadras de ti y darte indicaciones precisas para llegar.",
                        fontSize = 13.sp,
                        color = Color(0xFF8D4F38)
                    )
                }
            }
            
            Text(
                "Nota: Si desactivas la ubicación, algunas funciones del mapa y el filtro de 'Populares cerca de ti' podrían no funcionar correctamente.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}
