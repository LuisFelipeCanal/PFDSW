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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpSupportScreen(onBack: () -> Unit) {
    var message by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = androidx.compose.ui.platform.LocalContext.current

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
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
                Column {
                    Row {
                        IconButton(onClick = onBack, modifier = Modifier.size(40.dp).background(Color.White.copy(alpha = 0.2f), CircleShape)) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text("Ayuda y soporte", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Text("Estamos aquí para ayudarte", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        SupportButton(
                            icon = Icons.Default.Email, 
                            label = "Correo", 
                            value = "Enviar email", 
                            modifier = Modifier.weight(1f).clickable {
                                val intent = android.content.Intent(android.content.Intent.ACTION_SENDTO).apply {
                                    data = android.net.Uri.parse("mailto:luisfelipeblesthewolves@gmail.com")
                                }
                                context.startActivity(intent)
                            }
                        )
                        SupportButton(
                            icon = Icons.Default.Phone, 
                            label = "WhatsApp", 
                            value = "Chat directo", 
                            modifier = Modifier.weight(1f).clickable {
                                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW).apply {
                                    data = android.net.Uri.parse("https://wa.me/51977201451")
                                }
                                context.startActivity(intent)
                            }
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                SectionTitle("¿EN QUÉ PODEMOS AYUDARTE?")
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        HelpCategoryCard(Icons.Default.MenuBook, "Guía de uso", Modifier.weight(1f))
                        HelpCategoryCard(Icons.Default.BugReport, "Reportar un error", Modifier.weight(1f))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        HelpCategoryCard(Icons.Default.Lightbulb, "Sugerir mejora", Modifier.weight(1f))
                        HelpCategoryCard(Icons.Default.HelpCenter, "Otro motivo", Modifier.weight(1f))
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ChatBubbleOutline, contentDescription = null, tint = Color(0xFFE27553))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Envíanos un mensaje", fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        TextField(
                            value = message,
                            onValueChange = { message = it },
                            modifier = Modifier.fillMaxWidth().height(100.dp),
                            placeholder = { Text("Describe tu problema o pregunta...", fontSize = 12.sp) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFFDEEE9).copy(alpha = 0.5f),
                                unfocusedContainerColor = Color(0xFFFDEEE9).copy(alpha = 0.5f),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { 
                                if (message.isNotBlank()) {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Mensaje enviado. Nos pondremos en contacto pronto.")
                                        message = ""
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE27553)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Enviar mensaje", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                SectionTitle("USO DE LA APP")
                FAQItem("¿Cómo encuentro huariques cerca de mí?")
                FAQItem("¿Cómo guardo un huarique en favoritos?")
                
                SectionTitle("CUENTA")
                FAQItem("¿Cómo cambio mi contraseña?")
                FAQItem("¿Puedo usar la app sin registrarme?")
                
                SectionTitle("TÉCNICO")
                FAQItem("¿Por qué el mapa no muestra mi ubicación?")

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFFDEEE9).copy(alpha = 0.5f)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Contacto directo", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(12.dp))
                        ContactInfoItem(Icons.Default.Email, "luisfelipeblesthewolves@gmail.com")
                        ContactInfoItem(Icons.Default.Phone, "+51 977 201 451")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Horario de atención: Lunes a viernes, 9:00 am – 6:00 pm (hora Lima, Perú)", fontSize = 11.sp, color = Color.Gray)
                    }
                }
                
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
fun SupportButton(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String, modifier: Modifier) {
    Surface(
        modifier = modifier.height(60.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color.White.copy(alpha = 0.2f)
    ) {
        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.size(32.dp), shape = CircleShape, color = Color.White.copy(alpha = 0.2f)) {
                Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.padding(6.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(label, color = Color.White, fontSize = 10.sp)
                Text(value, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            }
        }
    }
}

@Composable
fun HelpCategoryCard(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, modifier: Modifier) {
    Card(
        modifier = modifier.height(60.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.size(32.dp), shape = CircleShape, color = Color(0xFFFDEEE9)) {
                Icon(icon, contentDescription = null, tint = Color(0xFFE27553), modifier = Modifier.padding(6.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ContactInfoItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
        Icon(icon, contentDescription = null, tint = Color(0xFFE27553), modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, fontSize = 12.sp, color = Color(0xFFE27553))
    }
}
