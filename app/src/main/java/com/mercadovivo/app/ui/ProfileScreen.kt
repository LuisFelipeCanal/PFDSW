package com.mercadovivo.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mercadovivo.app.auth.AuthViewModel
import com.mercadovivo.app.ui.theme.MercadoVivoGradientEnd
import com.mercadovivo.app.ui.theme.MercadoVivoGradientStart

@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToAddresses: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    onNavigateToHelp: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onNavigateToRegisterLocal: () -> Unit,
    onLogout: () -> Unit
) {
    val userData = authViewModel.userData
    val user = authViewModel.currentUser
    val scrollState = rememberScrollState()
    var showAdminDialog by remember { mutableStateOf(false) }
    var adminPass by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFBF0))
            .verticalScroll(scrollState)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(MercadoVivoGradientStart, MercadoVivoGradientEnd)
                    )
                )
                .padding(24.dp)
        ) {
            Row(
                modifier = Modifier.align(Alignment.CenterStart),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = CircleShape,
                    color = Color.White
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (userData?.photoUrl?.isNotEmpty() == true) {
                            coil.compose.AsyncImage(
                                model = userData.photoUrl,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                        } else {
                            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(40.dp), tint = Color.Gray)
                        }
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = userData?.displayName ?: user?.displayName ?: "Cargando...",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = if (authViewModel.isAdmin) Color(0xFFFFEB3B) else Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = authViewModel.userRole,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (authViewModel.isAdmin) Color.Black else Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = user?.email ?: "",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        Column(modifier = Modifier.padding(24.dp)) {
            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard("12", "Reseñas", Modifier.weight(1f))
                StatCard("2", "Favoritos", Modifier.weight(1f))
                StatCard("8", "Visitas", Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Menu Items
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column {
                    ProfileMenuItem(Icons.Default.Person, "Editar perfil", onNavigateToEditProfile)
                    ProfileMenuItem(Icons.Default.LocationOn, "Configuración de ubicación", onNavigateToAddresses)
                    ProfileMenuItem(Icons.Default.Notifications, "Notificaciones", onNavigateToNotifications)
                    ProfileMenuItem(Icons.Default.Lock, "Privacidad y seguridad", onNavigateToPrivacy)
                    ProfileMenuItem(Icons.Default.Info, "Ayuda y soporte", onNavigateToHelp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            if (authViewModel.isAdmin) {
                Button(
                    onClick = onNavigateToAdmin,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE27553)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Panel de Administrador", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = { authViewModel.exitAdminMode() },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Salir del modo administrador", color = Color.Gray, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(24.dp))
            } else {
                TextButton(onClick = { showAdminDialog = true }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Text("Acceso Administrador", color = Color.Gray)
                }
            }

            // About Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Sobre MercadoVivo", fontWeight = FontWeight.Bold)
                    Text("Versión 1.0.0", color = Color.Gray, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row {
                        Text("Términos de uso", color = Color(0xFFE27553), fontSize = 12.sp, modifier = Modifier.clickable { })
                        Spacer(modifier = Modifier.width(16.dp))
                        Text("Política de privacidad", color = Color(0xFFE27553), fontSize = 12.sp, modifier = Modifier.clickable { })
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    authViewModel.logout()
                    onLogout()
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFDEEE9)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = Color(0xFFD4183D))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Cerrar sesión", color = Color(0xFFD4183D), fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(100.dp))
        }
    }

    if (showAdminDialog) {
        AlertDialog(
            onDismissRequest = { showAdminDialog = false },
            title = { Text("Activar Modo Admin") },
            text = {
                TextField(
                    value = adminPass,
                    onValueChange = { adminPass = it },
                    placeholder = { Text("Contraseña de admin") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (authViewModel.activateAdminMode(adminPass)) {
                        showAdminDialog = false
                    }
                }) {
                    Text("Activar")
                }
            }
        )
    }
}

@Composable
private fun StatCard(value: String, label: String, modifier: Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE27553))
            Text(text = label, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
private fun ProfileMenuItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(36.dp),
            shape = RoundedCornerShape(10.dp),
            color = Color(0xFFFDEEE9)
        ) {
            Icon(icon, contentDescription = null, tint = Color(0xFFE27553), modifier = Modifier.padding(8.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = label, modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium)
        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.LightGray)
    }
}
