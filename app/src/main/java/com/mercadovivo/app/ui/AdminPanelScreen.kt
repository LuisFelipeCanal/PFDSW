package com.mercadovivo.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mercadovivo.app.models.Huarique
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    huariques: List<Huarique>,
    onEditHuarique: (String) -> Unit,
    onAddHuarique: () -> Unit,
    onBack: () -> Unit
) {
    val unverifiedCount = huariques.count { !it.isVerified }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Panel de Administración") 
                        if (unverifiedCount > 0) {
                            Text(
                                "Hay $unverifiedCount solicitudes pendientes", 
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFE27553)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddHuarique, containerColor = Color(0xFFE27553), contentColor = Color.White) {
                Icon(Icons.Default.Add, contentDescription = "Añadir")
            }
        }
    ) { padding ->
        if (huariques.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No hay huariques registrados")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Primero mostramos los NO verificados (Solicitudes)
                val sortedHuariques = huariques.sortedBy { it.isVerified }
                
                items(sortedHuariques) { huarique ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = if (!huarique.isVerified) Color(0xFFFFF3E0) else Color.White
                        ),
                        border = if (!huarique.isVerified) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFB74D)) else null
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = huarique.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    if (!huarique.isVerified) {
                                        Surface(
                                            color = Color(0xFFE27553),
                                            shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp),
                                            modifier = Modifier.padding(start = 8.dp)
                                        ) {
                                            Text(
                                                "PENDIENTE", 
                                                color = Color.White, 
                                                fontSize = 9.sp, 
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                                                fontWeight = FontWeight.Black
                                            )
                                        }
                                    }
                                }
                                Text(text = huarique.district, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }
                            IconButton(onClick = { onEditHuarique(huarique.id) }) {
                                Icon(
                                    imageVector = if (!huarique.isVerified) Icons.Default.Edit else Icons.Default.Edit, 
                                    contentDescription = "Revisar",
                                    tint = if (!huarique.isVerified) Color(0xFFE27553) else Color.Gray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
