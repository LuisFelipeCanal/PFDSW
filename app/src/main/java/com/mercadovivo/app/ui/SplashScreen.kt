package com.mercadovivo.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mercadovivo.app.ui.theme.MercadoVivoGradientEnd
import com.mercadovivo.app.ui.theme.MercadoVivoGradientStart

@Composable
fun SplashScreen(
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(MercadoVivoGradientStart, MercadoVivoGradientEnd)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo
            Surface(
                modifier = Modifier.size(100.dp),
                shape = RoundedCornerShape(20.dp),
                color = Color.White
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text("🍴", fontSize = 48.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "MercadoVivo",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Descubre sabores auténticos",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Feature Cards
            FeatureItem(Icons.Default.Place, "Encuentra huariques", "Cerca de tu ubicación")
            FeatureItem(Icons.Default.Star, "Lee reseñas", "De la comunidad foodie")
            FeatureItem(Icons.Default.Favorite, "Guarda favoritos", "Y comparte experiencias")

            Spacer(modifier = Modifier.weight(1f))

            // Buttons
            Button(
                onClick = onRegisterClick,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Crear cuenta", color = MercadoVivoGradientStart, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onLoginClick,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Iniciar sesión", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun FeatureItem(icon: ImageVector, title: String, subtitle: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.2f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(10.dp),
                color = Color.White.copy(alpha = 0.3f)
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.padding(8.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, color = Color.White, fontWeight = FontWeight.Bold)
                Text(text = subtitle, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
            }
        }
    }
}
