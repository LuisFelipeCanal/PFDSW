package com.mercadovivo.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
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
fun PrivacySecurityScreen(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFBF0))
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
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
                    Text("Privacidad y seguridad", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Text("Tu información está protegida", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                }
            }
        }

        Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
            // Certified App Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color.White.copy(alpha = 0.2f), // Transparent/blurry look in image
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.5f))
            ) {
                // Actually in the image it looks like it has a darker orange background but transparent
                Box(modifier = Modifier.background(Color(0xFFE27553).copy(alpha = 0.1f)).padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Shield, contentDescription = null, tint = Color(0xFFE27553))
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Aplicación certificada", fontWeight = FontWeight.Bold, color = Color(0xFFE27553))
                            Text("Datos cifrados · Sin venta de datos · Cumple Ley N° 29733", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                }
            }

            SectionTitle("CONTROL DE DATOS")
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column {
                    PrivacyToggleItem(Icons.Default.Language, "Compartir ubicación", "Permite mostrar huariques cercanos a ti", true)
                    Divider(modifier = Modifier.padding(horizontal = 16.dp), color = Color.LightGray.copy(alpha = 0.5f))
                    PrivacyToggleItem(Icons.Default.Storage, "Datos de uso anónimos", "Ayúdanos a mejorar la app de forma anónima", true)
                    Divider(modifier = Modifier.padding(horizontal = 16.dp), color = Color.LightGray.copy(alpha = 0.5f))
                    PrivacyToggleItem(Icons.Default.Visibility, "Perfil público", "Otros usuarios pueden ver tus reseñas y favoritos", false)
                }
            }

            SectionTitle("SEGURIDAD DE CUENTA")
            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column {
                    SecurityActionItem(Icons.Default.Lock, "Cambiar contraseña", "Última actualización: hace 30 días")
                    Divider(modifier = Modifier.padding(horizontal = 16.dp), color = Color.LightGray.copy(alpha = 0.5f))
                    SecurityActionItem(Icons.Default.VerifiedUser, "Autenticación en dos pasos", "Protege tu cuenta con SMS o app de autenticación")
                    Divider(modifier = Modifier.padding(horizontal = 16.dp), color = Color.LightGray.copy(alpha = 0.5f))
                    SecurityActionItem(Icons.Default.Devices, "Sesiones activas", "1 dispositivo conectado")
                }
            }

            SectionTitle("PREGUNTAS FRECUENTES")
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                FAQItem("¿Qué datos recopilamos?")
                FAQItem("¿Cómo usamos tu ubicación?")
                FAQItem("¿Están mis datos seguros?")
                FAQItem("¿Puedo eliminar mi cuenta?")
            }
            
            OutlinedButton(
                onClick = { },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
            ) {
                Text("Solicitar eliminación de cuenta", color = Color(0xFFD4183D))
            }
            
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(text, fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
}

@Composable
fun PrivacyToggleItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String, initialValue: Boolean) {
    var checked by remember { mutableStateOf(initialValue) }
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(modifier = Modifier.size(36.dp), shape = CircleShape, color = Color(0xFFFDEEE9)) {
            Icon(icon, contentDescription = null, tint = Color(0xFFE27553), modifier = Modifier.padding(8.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(subtitle, fontSize = 11.sp, color = Color.Gray)
        }
        Switch(
            checked = checked, 
            onCheckedChange = { checked = it },
            colors = SwitchDefaults.colors(checkedTrackColor = Color(0xFFE27553))
        )
    }
}

@Composable
fun SecurityActionItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { }.padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(modifier = Modifier.size(36.dp), shape = CircleShape, color = Color(0xFFFDEEE9)) {
            Icon(icon, contentDescription = null, tint = Color(0xFFE27553), modifier = Modifier.padding(8.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(subtitle, fontSize = 11.sp, color = Color.Gray)
        }
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
    }
}

@Composable
fun FAQItem(question: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(question, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Icon(Icons.Default.ExpandMore, contentDescription = null, tint = Color.Gray)
        }
    }
}
