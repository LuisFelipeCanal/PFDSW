package com.mercadovivo.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.mercadovivo.app.data.HuariqueRepository
import com.mercadovivo.app.models.Huarique
import com.mercadovivo.app.models.Ingredient
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminHuariqueEditScreen(
    huarique: Huarique? = null,
    onBack: () -> Unit
) {
    val repository = remember { HuariqueRepository() }
    val scope = rememberCoroutineScope()
    
    var name by remember { mutableStateOf(huarique?.name ?: "") }
    var description by remember { mutableStateOf(huarique?.description ?: "") }
    var address by remember { mutableStateOf(huarique?.address ?: "") }
    var district by remember { mutableStateOf(huarique?.district ?: "") }
    var latLng by remember { mutableStateOf(LatLng(huarique?.lat ?: -12.1191, huarique?.lng ?: -77.0349)) }
    
    val ingredients = remember { mutableStateListOf<Ingredient>().apply { 
        if (huarique != null) addAll(huarique.ingredients)
    } }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(latLng, 15f)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (huarique == null) "Nuevo Huarique" else "Editar Huarique") },
                actions = {
                    Button(onClick = {
                        val newHuarique = (huarique ?: Huarique()).copy(
                            name = name,
                            description = description,
                            address = address,
                            district = district,
                            lat = latLng.latitude,
                            lng = latLng.longitude,
                            ingredients = ingredients.toList()
                        )
                        scope.launch {
                            repository.saveHuarique(newHuarique)
                            onBack()
                        }
                    }) {
                        Text("Guardar")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Dirección") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = district, onValueChange = { district = it }, label = { Text("Distrito") }, modifier = Modifier.fillMaxWidth())
            }

            item {
                Text("Ubicación en el mapa (Toca para mover)", style = MaterialTheme.typography.titleMedium)
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)) {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        onMapClick = { latLng = it }
                    ) {
                        Marker(state = MarkerState(position = latLng))
                    }
                }
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Ingredientes", style = MaterialTheme.typography.titleMedium)
                    IconButton(onClick = { ingredients.add(Ingredient()) }) {
                        Icon(Icons.Default.Add, contentDescription = "Añadir ingrediente")
                    }
                }
            }

            itemsIndexed(ingredients) { index, ingredient ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextField(value = ingredient.name, onValueChange = { ingredients[index] = ingredient.copy(name = it) }, placeholder = { Text("Nombre") }, modifier = Modifier.weight(1f))
                    TextField(value = ingredient.amount, onValueChange = { ingredients[index] = ingredient.copy(amount = it) }, placeholder = { Text("Cantidad") }, modifier = Modifier.weight(0.5f))
                    IconButton(onClick = { ingredients.removeAt(index) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Borrar")
                    }
                }
            }

            item {
                Text("Grabación del Propietario", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = { /* TODO: Implement Recording and Dropbox Upload */ }) {
                    Text("Grabar Audio / Subir a Dropbox")
                }
                if (huarique?.audioPath != null) {
                    Text("Archivo actual: ${huarique.audioPath}", style = MaterialTheme.typography.bodySmall)
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(32.dp))
                if (huarique != null) {
                    Button(
                        onClick = {
                            scope.launch {
                                repository.deleteHuarique(huarique.id)
                                onBack()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Eliminar Huarique")
                    }
                }
            }
        }
    }
}
