package com.mercadovivo.app.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.mercadovivo.app.data.HuariqueRepository
import com.mercadovivo.app.models.Huarique
import com.mercadovivo.app.models.Ingredient
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AdminHuariqueEditScreen(
    huarique: Huarique? = null,
    isAdmin: Boolean = false,
    onBack: () -> Unit
) {
    val repository = remember { HuariqueRepository() }
    val scope = rememberCoroutineScope()
    
    var name by remember { mutableStateOf(huarique?.name ?: "") }
    var description by remember { mutableStateOf(huarique?.description ?: "") }
    var ownerFirstName by remember { mutableStateOf(huarique?.ownerFirstName ?: "") }
    var ownerLastName by remember { mutableStateOf(huarique?.ownerLastName ?: "") }
    var businessType by remember { mutableStateOf(huarique?.businessType ?: "") }
    var branchesCount by remember { mutableStateOf(huarique?.branchesCount?.toString() ?: "1") }
    var isStreetFront by remember { mutableStateOf(huarique?.isStreetFront ?: true) }
    var email by remember { mutableStateOf(huarique?.email ?: "") }
    var ownerPhone by remember { mutableStateOf(huarique?.ownerPhone ?: "") }
    var phone by remember { mutableStateOf(huarique?.phone ?: "") }
    var address by remember { mutableStateOf(huarique?.address ?: "") }
    var district by remember { mutableStateOf(huarique?.district ?: "") }
    var suggestedCategory by remember { mutableStateOf(huarique?.suggestedCategory ?: "") }
    
    // Separamos el horario en apertura y cierre para mejor control
    var openTime by remember { mutableStateOf(huarique?.horario?.split("-")?.firstOrNull()?.trim() ?: "08:00 AM") }
    var closeTime by remember { mutableStateOf(huarique?.horario?.split("-")?.getOrNull(1)?.trim() ?: "06:00 PM") }
    
    var isVerified by remember { mutableStateOf(huarique?.isVerified ?: false) }
    var latLng by remember { mutableStateOf(LatLng(huarique?.lat ?: -12.1191, huarique?.lng ?: -77.0349)) }
    
    // Categorías seleccionadas
    val availableCategories = listOf("Comida Criolla", "Cevicheria", "Parrillas", "Polleria", "Comida Selva", "Chifa", "Postres", "Jugueria", "Bebidas", "Pescados y Mariscos", "Sandwiches", "Caldos y Sopas")
    val selectedCategories = remember { mutableStateListOf<String>().apply { 
        if (huarique != null) addAll(huarique.categories) 
    } }
    
    // Opciones para autocompletado de Tipo de Negocio
    val businessTypeOptions = listOf("Restaurante", "Puesto de Mercado", "Food Truck", "Carrito", "Huarique de Paso")
    var expandedByType by remember { mutableStateOf(false) }

    val photos = remember { mutableStateListOf<String>() }
    val newPhotosUris = remember { mutableStateListOf<android.net.Uri>() }
    val ingredients = remember { mutableStateListOf<Ingredient>() }
    val menuPlates = remember { mutableStateListOf<com.mercadovivo.app.models.Plato>() }
    val menuBeverages = remember { mutableStateListOf<com.mercadovivo.app.models.Plato>() }
    val menuDesserts = remember { mutableStateListOf<com.mercadovivo.app.models.Plato>() }
    
    var isSaving by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    
    // Bandera para evitar que actualizaciones externas (Firebase) 
    // sobrescriban los cambios locales mientras estamos editando.
    val hasInitialized = remember { mutableStateOf(false) }

    LaunchedEffect(huarique) {
        if (huarique != null && !hasInitialized.value) {
            name = huarique.name
            description = huarique.description
            ownerFirstName = huarique.ownerFirstName
            ownerLastName = huarique.ownerLastName
            businessType = huarique.businessType
            branchesCount = huarique.branchesCount.toString()
            isStreetFront = huarique.isStreetFront
            email = huarique.email
            ownerPhone = huarique.ownerPhone
            phone = huarique.phone
            address = huarique.address
            district = huarique.district
            suggestedCategory = huarique.suggestedCategory
            val times = huarique.horario?.split("-") ?: listOf("08:00 AM", "06:00 PM")
            openTime = times.firstOrNull()?.trim() ?: "08:00 AM"
            closeTime = times.getOrNull(1)?.trim() ?: "06:00 PM"
            isVerified = huarique.isVerified
            latLng = LatLng(huarique.lat ?: -12.1191, huarique.lng ?: -77.0349)
            
            photos.clear()
            photos.addAll(huarique.photos)
            ingredients.clear()
            ingredients.addAll(huarique.ingredients)
            menuPlates.clear()
            menuPlates.addAll(huarique.menuPlates)
            menuBeverages.clear()
            menuBeverages.addAll(huarique.menuBeverages)
            menuDesserts.clear()
            menuDesserts.addAll(huarique.menuDesserts)
            
            hasInitialized.value = true
        }
    }

    val photoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { newPhotosUris.add(it) }
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(latLng, 15f)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { 
                    Text(if (isAdmin) (if (huarique == null) "Nuevo Huarique" else "Editar Huarique") else "Inscribir mi Huarique") 
                },
                actions = {
                    if (isSaving) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp).padding(end = 16.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Button(onClick = {
                            isSaving = true
                            val finalHuarique = (huarique ?: Huarique()).copy(
                                name = name,
                                description = description,
                                ownerFirstName = ownerFirstName,
                                ownerLastName = ownerLastName,
                                businessType = businessType,
                                branchesCount = branchesCount.toIntOrNull() ?: 1,
                                isStreetFront = isStreetFront,
                                email = email,
                                ownerPhone = ownerPhone,
                                phone = phone,
                                address = address,
                                district = district,
                                horario = "$openTime - $closeTime",
                                categories = selectedCategories.toList(),
                                suggestedCategory = suggestedCategory,
                                lat = latLng.latitude,
                                lng = latLng.longitude,
                                photos = photos.toList(),
                                ingredients = ingredients.toList(),
                                menuPlates = menuPlates.toList(),
                                menuBeverages = menuBeverages.toList(),
                                menuDesserts = menuDesserts.toList(),
                                isVerified = if (isAdmin) isVerified else (huarique?.isVerified ?: false)
                            )
                            scope.launch {
                                val result = repository.saveHuarique(finalHuarique, newPhotosUris.toList())
                                isSaving = false
                                if (result.isSuccess) {
                                    onBack()
                                } else {
                                    snackbarHostState.showSnackbar("Error al guardar: ${result.exceptionOrNull()?.message}")
                                }
                            }
                        }) {
                            Text(if (isAdmin) "Guardar" else "Enviar Solicitud")
                        }
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (!isAdmin) {
                item {
                    Surface(color = Color(0xFFFFEB3B), shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth()) {
                        Text("¡Aprovecha! Solo un 10% de comisión para nuevos socios.", modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold)
                    }
                }
            }

            if (isAdmin && huarique != null) {
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = if (isVerified) Color(0xFFE8F5E9) else Color(0xFFFFF3E0))) {
                        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(if (isVerified) "Estado: Verificado" else "Estado: Pendiente", fontWeight = FontWeight.Bold, color = if (isVerified) Color(0xFF2E7D32) else Color(0xFFE65100))
                                Text("Visibilidad en la App", style = MaterialTheme.typography.bodySmall)
                            }
                            Switch(checked = isVerified, onCheckedChange = { isVerified = it })
                        }
                    }
                }
            }

            item {
                Text("Fotos del Local", style = MaterialTheme.typography.titleMedium, color = Color(0xFFE27553))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        Surface(modifier = Modifier.size(100.dp).clickable { photoLauncher.launch("image/*") }, color = Color.LightGray.copy(alpha = 0.3f), shape = RoundedCornerShape(12.dp)) {
                            Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.padding(32.dp), tint = Color.Gray)
                        }
                    }
                    items(photos) { photo ->
                        Box(modifier = Modifier.size(100.dp)) {
                            AsyncImage(model = photo, contentDescription = null, modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)), contentScale = ContentScale.Crop)
                            IconButton(onClick = { photos.remove(photo) }, modifier = Modifier.align(Alignment.TopEnd).size(24.dp).background(Color.White, CircleShape)) {
                                Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                    items(newPhotosUris) { uri ->
                        Box(modifier = Modifier.size(100.dp)) {
                            AsyncImage(model = uri, contentDescription = null, modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(12.dp)), contentScale = ContentScale.Crop)
                            IconButton(onClick = { newPhotosUris.remove(uri) }, modifier = Modifier.align(Alignment.TopEnd).size(24.dp).background(Color.White, CircleShape)) {
                                Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }

            item {
                Text("Datos del Local", style = MaterialTheme.typography.titleMedium, color = Color(0xFFE27553))
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre del Local *") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
                
                Text("Categorías *", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(top = 8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    availableCategories.forEach { category ->
                        FilterChip(
                            selected = selectedCategories.contains(category),
                            onClick = {
                                if (selectedCategories.contains(category)) selectedCategories.remove(category)
                                else selectedCategories.add(category)
                            },
                            label = { Text(category) }
                        )
                    }
                }
                
                OutlinedTextField(
                    value = suggestedCategory, 
                    onValueChange = { suggestedCategory = it }, 
                    label = { Text("¿No ves tu categoría? Sugiérela aquí") },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Ej: Pescados y Mariscos") }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Autocompletado para Tipo de Negocio
                ExposedDropdownMenuBox(
                    expanded = expandedByType,
                    onExpandedChange = { expandedByType = !expandedByType }
                ) {
                    OutlinedTextField(
                        value = businessType,
                        onValueChange = { businessType = it },
                        label = { Text("Tipo de negocio *") },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedByType) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )
                    ExposedDropdownMenu(
                        expanded = expandedByType,
                        onDismissRequest = { expandedByType = false }
                    ) {
                        businessTypeOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    businessType = option
                                    expandedByType = false
                                }
                            )
                        }
                    }
                }
                OutlinedTextField(value = branchesCount, onValueChange = { branchesCount = it }, label = { Text("Sucursales") }, modifier = Modifier.fillMaxWidth())
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("¿Es un local a la calle? *")
                    RadioButton(selected = isStreetFront, onClick = { isStreetFront = true })
                    Text("Sí")
                    RadioButton(selected = !isStreetFront, onClick = { isStreetFront = false })
                    Text("No")
                }
            }

            item {
                Text("Datos del Propietario", style = MaterialTheme.typography.titleMedium, color = Color(0xFFE27553))
                OutlinedTextField(value = ownerFirstName, onValueChange = { ownerFirstName = it }, label = { Text("Nombre *") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = ownerLastName, onValueChange = { ownerLastName = it }, label = { Text("Apellido *") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email *") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = ownerPhone, onValueChange = { ownerPhone = it }, label = { Text("Teléfono *") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Teléfono del local *") }, modifier = Modifier.fillMaxWidth())
            }

            item {
                Text("Ubicación y Horario", style = MaterialTheme.typography.titleMedium, color = Color(0xFFE27553))
                OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Dirección *") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = district, onValueChange = { district = it }, label = { Text("Distrito") }, modifier = Modifier.fillMaxWidth())
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = openTime, 
                        onValueChange = { openTime = it }, 
                        label = { Text("Apertura") }, 
                        placeholder = { Text("08:00 AM") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = closeTime, 
                        onValueChange = { closeTime = it }, 
                        label = { Text("Cierre") }, 
                        placeholder = { Text("06:00 PM") },
                        modifier = Modifier.weight(1f)
                    )
                }
                Text("Formato sugerido: HH:MM AM/PM", style = MaterialTheme.typography.labelSmall, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))

                Box(modifier = Modifier.fillMaxWidth().height(200.dp).padding(top = 8.dp)) {
                    GoogleMap(modifier = Modifier.fillMaxSize(), cameraPositionState = cameraPositionState, onMapClick = { latLng = it }) {
                        Marker(state = MarkerState(position = latLng))
                    }
                }
            }

            item {
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Platos", style = MaterialTheme.typography.titleMedium, color = Color(0xFFE27553))
                    IconButton(onClick = { menuPlates.add(com.mercadovivo.app.models.Plato(id = java.util.UUID.randomUUID().toString())) }) {
                        Icon(Icons.Default.Add, contentDescription = null)
                    }
                }
            }
            itemsIndexed(menuPlates) { index, plato ->
                MenuItemEditor(item = plato, onUpdate = { menuPlates[index] = it }, onDelete = { menuPlates.removeAt(index) })
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Bebidas", style = MaterialTheme.typography.titleMedium, color = Color(0xFFE27553))
                    IconButton(onClick = { menuBeverages.add(com.mercadovivo.app.models.Plato(id = java.util.UUID.randomUUID().toString(), category = "bebida")) }) {
                        Icon(Icons.Default.Add, contentDescription = null)
                    }
                }
            }
            itemsIndexed(menuBeverages) { index, bebida ->
                MenuItemEditor(item = bebida, onUpdate = { menuBeverages[index] = it }, onDelete = { menuBeverages.removeAt(index) })
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Postres", style = MaterialTheme.typography.titleMedium, color = Color(0xFFE27553))
                    IconButton(onClick = { menuDesserts.add(com.mercadovivo.app.models.Plato(id = java.util.UUID.randomUUID().toString(), category = "postre")) }) {
                        Icon(Icons.Default.Add, contentDescription = null)
                    }
                }
            }
            itemsIndexed(menuDesserts) { index, postre ->
                MenuItemEditor(item = postre, onUpdate = { menuDesserts[index] = it }, onDelete = { menuDesserts.removeAt(index) })
            }
            
            item { Spacer(modifier = Modifier.height(40.dp)) }
        }
    }
}

@Composable
private fun MenuItemEditor(item: com.mercadovivo.app.models.Plato, onUpdate: (com.mercadovivo.app.models.Plato) -> Unit, onDelete: () -> Unit) {
    val photoLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
        uri?.let { onUpdate(item.copy(photoLabel = it.toString())) }
    }
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), colors = CardDefaults.cardColors(containerColor = Color.White), border = androidx.compose.foundation.BorderStroke(0.5.dp, Color.LightGray)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Surface(modifier = Modifier.size(60.dp).clickable { photoLauncher.launch("image/*") }, color = Color.LightGray.copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp)) {
                    if (item.photoLabel.isNotEmpty()) AsyncImage(model = item.photoLabel, contentDescription = null, contentScale = ContentScale.Crop)
                    else Icon(Icons.Default.PhotoCamera, contentDescription = null, modifier = Modifier.padding(16.dp), tint = Color.Gray)
                }
                OutlinedTextField(value = item.name, onValueChange = { onUpdate(item.copy(name = it)) }, label = { Text("Nombre") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = item.price.toString(), onValueChange = { onUpdate(item.copy(price = it.toDoubleOrNull() ?: 0.0)) }, label = { Text("Precio") }, modifier = Modifier.weight(0.4f))
                IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red) }
            }
            Text("Ingredientes:", style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(top = 8.dp))
            item.ingredients.forEachIndexed { idx, ing ->
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    TextField(value = ing.name, onValueChange = { 
                        val newList = item.ingredients.toMutableList()
                        newList[idx] = ing.copy(name = it)
                        onUpdate(item.copy(ingredients = newList))
                    }, placeholder = { Text("Insumo") }, modifier = Modifier.weight(1f))
                    TextField(value = ing.amount, onValueChange = { 
                        val newList = item.ingredients.toMutableList()
                        newList[idx] = ing.copy(amount = it)
                        onUpdate(item.copy(ingredients = newList))
                    }, placeholder = { Text("Cant.") }, modifier = Modifier.weight(0.5f))
                    IconButton(onClick = {
                        val newList = item.ingredients.toMutableList()
                        newList.removeAt(idx)
                        onUpdate(item.copy(ingredients = newList))
                    }) { Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp)) }
                }
            }
            TextButton(onClick = { onUpdate(item.copy(ingredients = item.ingredients + com.mercadovivo.app.models.Ingredient())) }, modifier = Modifier.align(Alignment.End)) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Text("Añadir Insumo", fontSize = 12.sp)
            }
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            Text("Multimedia (Dropbox - Opcional)", style = MaterialTheme.typography.labelLarge, color = Color.Gray)
            OutlinedTextField(value = item.videoLabel, onValueChange = { onUpdate(item.copy(videoLabel = it)) }, label = { Text("Link Video") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = item.audioLabel, onValueChange = { onUpdate(item.copy(audioLabel = it)) }, label = { Text("Link Audio") }, modifier = Modifier.fillMaxWidth())
            Text("💡 Si el video ya incluye voz, no es necesario audio por separado.", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        }
    }
}
