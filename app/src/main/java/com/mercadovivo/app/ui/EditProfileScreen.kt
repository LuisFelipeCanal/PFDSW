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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mercadovivo.app.auth.AuthViewModel
import com.mercadovivo.app.ui.theme.MercadoVivoGradientEnd
import com.mercadovivo.app.ui.theme.MercadoVivoGradientStart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    authViewModel: AuthViewModel,
    onBack: () -> Unit
) {
    val user = authViewModel.currentUser
    var name by remember { mutableStateOf(user?.displayName ?: "Usuario Demo") }
    var email by remember { mutableStateOf(user?.email ?: "demo@mercadovivo.pe") }
    var phone by remember { mutableStateOf("+51 987 654 321") }
    var bio by remember { mutableStateOf("Amante de la gastronomía peruana y los mercados tradicionales.") }

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
                .height(200.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(MercadoVivoGradientStart, MercadoVivoGradientEnd)
                    ),
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                )
                .padding(24.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    IconButton(onClick = onBack, modifier = Modifier.size(40.dp).background(Color.White.copy(alpha = 0.2f), CircleShape)) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Editar perfil", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.CenterVertically))
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Box {
                    Surface(
                        modifier = Modifier.size(100.dp),
                        shape = CircleShape,
                        color = Color.White
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(50.dp), tint = Color.Gray)
                        }
                    }
                    Surface(
                        modifier = Modifier.align(Alignment.BottomEnd).size(32.dp),
                        shape = CircleShape,
                        color = Color(0xFFF9B36D),
                        border = androidx.compose.foundation.BorderStroke(2.dp, Color.White)
                    ) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.padding(6.dp), tint = Color.White)
                    }
                }
            }
        }

        Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            EditField("NOMBRE COMPLETO", name) { name = it }
            EditField("CORREO ELECTRÓNICO", email) { email = it }
            EditField("TELÉFONO", phone) { phone = it }
            EditField("SOBRE MÍ", bio, singleLine = false) { bio = it }
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("CONTRASEÑA", fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Cambiar contraseña →", color = Color(0xFFE27553), modifier = Modifier.clickable { })
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = { onBack() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE27553)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Guardar cambios", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun EditField(label: String, value: String, singleLine: Boolean = true, onValueChange: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(label, fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                singleLine = singleLine
            )
        }
    }
}
