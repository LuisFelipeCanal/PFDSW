package com.mercadovivo.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mercadovivo.app.models.Huarique
import com.mercadovivo.app.models.Plato
import com.mercadovivo.app.ui.components.InfoRow
import com.mercadovivo.app.ui.components.SectionHeader

@Composable
fun HuariqueDetailScreen(
    huarique: Huarique,
    onBack: () -> Unit,
    onOpenMap: (String) -> Unit,
    onSeeAllMenu: () -> Unit,
    onDishClick: (Plato) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFBF0))
            .verticalScroll(rememberScrollState())
    ) {
        // Hero Image
        Box(modifier = Modifier.fillMaxWidth().height(260.dp)) {
            AsyncImage(
                model = if (huarique.photos.isNotEmpty()) huarique.photos.first() else "https://placeholder.com/600",
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            
            // Back button and Actions
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onBack, modifier = Modifier.background(Color.White.copy(alpha = 0.5f), CircleShape)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                }
                Row {
                    IconButton(onClick = {}, modifier = Modifier.background(Color.White.copy(alpha = 0.5f), CircleShape)) {
                        Icon(Icons.Default.Share, contentDescription = null)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = {}, modifier = Modifier.background(Color.White.copy(alpha = 0.5f), CircleShape)) {
                        Icon(Icons.Default.FavoriteBorder, contentDescription = null)
                    }
                }
            }

            Surface(
                modifier = Modifier.align(Alignment.BottomStart).padding(16.dp),
                color = Color(0xFFE27553),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("Abierto ahora", color = Color.White, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), fontSize = 12.sp)
            }
        }

        Column(modifier = Modifier.padding(24.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(huarique.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Surface(color = Color(0xFFFDEEE9), shape = RoundedCornerShape(20.dp)) {
                    Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("⭐", fontSize = 12.sp)
                        Text("${huarique.rating ?: 0.0}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
            
            Text(huarique.description, style = MaterialTheme.typography.bodyMedium, color = Color.Gray)

            Spacer(modifier = Modifier.height(24.dp))
            InfoRow(label = "📍 Dirección", value = huarique.address)
            InfoRow(label = "🕒 Horario", value = huarique.horario ?: "8:00 AM - 6:00 PM")
            InfoRow(label = "📞 Teléfono", value = huarique.phone ?: "+51 987 654 321")

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = { onOpenMap(huarique.id) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE27553)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Ver en mapa", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Carta Digital", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(
                    "Ver todo →", 
                    color = Color(0xFFE27553), 
                    fontSize = 14.sp,
                    modifier = Modifier.clickable { onSeeAllMenu() }
                )
            }
            
            Text("Platos principales", color = Color.Gray, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                items(huarique.menuPlates) { plato ->
                    PlatoMiniCard(plato, onClick = { onDishClick(plato) })
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Reseñas", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Ver todas (156) →", color = Color(0xFFE27553), fontSize = 14.sp)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            // Review Mock
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Surface(modifier = Modifier.size(48.dp), shape = CircleShape, color = Color.LightGray) {
                        Text("👤", modifier = Modifier.wrapContentSize())
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("María González", fontWeight = FontWeight.Bold)
                            Text("2 días atrás", color = Color.Gray, fontSize = 12.sp)
                        }
                        Text("⭐⭐⭐⭐⭐", fontSize = 10.sp)
                        Text(
                            "Excelente comida criolla, el lomo saltado es espectacular. El sabor es auténtico y las porciones...",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun PlatoMiniCard(plato: Plato, onClick: () -> Unit) {
    Card(
        modifier = Modifier.width(160.dp).clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            AsyncImage(
                model = if (plato.photoLabel.isNotEmpty()) plato.photoLabel else "https://placeholder.com/200",
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(100.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(12.dp)) {
                Text(plato.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1)
                Text("S/ ${plato.price}", color = Color(0xFFE27553), fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}
