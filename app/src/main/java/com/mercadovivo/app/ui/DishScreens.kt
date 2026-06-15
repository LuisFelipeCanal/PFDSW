package com.mercadovivo.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.FavoriteBorder
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
import com.mercadovivo.app.ui.theme.MercadoVivoGradientEnd
import com.mercadovivo.app.ui.theme.MercadoVivoGradientStart

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullMenuScreen(
    huarique: Huarique,
    onBack: () -> Unit,
    onDishClick: (Plato) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Platos", "Bebidas", "Postres")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Carta - ${huarique.name}", fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().background(Color(0xFFFFFBF0))) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = Color(0xFFE27553),
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (selectedTab == index) Color(0xFFE27553) else Color(0xFFFDEEE9)
                            ) {
                                Text(
                                    text = title,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    color = if (selectedTab == index) Color.White else Color(0xFFE27553)
                                )
                            }
                        }
                    )
                }
            }

            val currentList = when(selectedTab) {
                0 -> huarique.menuPlates
                1 -> huarique.menuBeverages
                else -> huarique.menuDesserts
            }

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(currentList) { plato ->
                    DishListItem(plato, onClick = { onDishClick(plato) })
                }
            }
        }
    }
}

@Composable
fun DishListItem(plato: Plato, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            AsyncImage(
                model = if(plato.photoLabel.isNotEmpty()) plato.photoLabel else "https://placeholder.com/400",
                contentDescription = null,
                modifier = Modifier.fillMaxWidth().height(160.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(plato.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("⭐", fontSize = 12.sp)
                        Text("${plato.rating}", fontWeight = FontWeight.Bold)
                    }
                }
                Text(plato.description, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Text("S/ ${plato.price}", color = Color(0xFFE27553), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun DishDetailScreen(
    huarique: Huarique,
    plato: Plato,
    onBack: () -> Unit,
    onWatchVideo: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFFFFBF0)).verticalScroll(rememberScrollState())) {
        Box(modifier = Modifier.fillMaxWidth().height(300.dp)) {
            AsyncImage(
                model = if(plato.photoLabel.isNotEmpty()) plato.photoLabel else "https://placeholder.com/400",
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
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
        }

        Column(modifier = Modifier.padding(24.dp)) {
            Text(huarique.name, color = Color.Gray, fontSize = 14.sp)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(plato.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Surface(color = Color(0xFFFFF7EF), shape = RoundedCornerShape(8.dp)) {
                    Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("⭐", fontSize = 12.sp)
                        Text("${plato.rating}", fontWeight = FontWeight.Bold)
                    }
                }
            }
            Text("S/ ${plato.price}", color = Color(0xFFE27553), fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Text(plato.description, style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onWatchVideo,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE27553)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.PlayCircle, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ver video de preparación", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))
            Divider(color = Color.LightGray.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(24.dp))
            
            Text("Ingredientes visibles", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(16.dp))
            
            // Grid de ingredientes
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val chunks = plato.ingredients.chunked(2)
                chunks.forEach { rowIngredients ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        rowIngredients.forEach { ingredient ->
                            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                                Surface(modifier = Modifier.size(6.dp), shape = CircleShape, color = Color(0xFFE27553)) {}
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(ingredient, fontSize = 14.sp)
                            }
                        }
                        if (rowIngredients.size == 1) Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFFFDEEE9)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("💡", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Tip del Chef", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(plato.chefTip.ifEmpty { "Este plato es mejor disfrutarlo recién preparado para sentir todos sus sabores." }, fontSize = 14.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoPrepScreen(
    huarique: Huarique,
    plato: Plato,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Video de Preparación", fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().background(Color.Black).verticalScroll(rememberScrollState())) {
            // Header Card
            Card(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(plato.name, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text(huarique.name, color = Color.Gray, fontSize = 14.sp)
                }
            }

            // Video Player Mock
            Box(modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth().height(220.dp)) {
                AsyncImage(
                    model = if(plato.photoLabel.isNotEmpty()) plato.photoLabel else "https://placeholder.com/400",
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize().background(Color.DarkGray, RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop,
                    alpha = 0.6f
                )
                Surface(
                    modifier = Modifier.align(Alignment.Center).size(64.dp),
                    shape = CircleShape,
                    color = Color(0xFFE27553)
                ) {
                    Icon(Icons.Default.PlayCircle, contentDescription = null, modifier = Modifier.padding(12.dp), tint = Color.White)
                }
                // Video controls mock
                Surface(
                    modifier = Modifier.align(Alignment.BottomCenter).padding(12.dp).fillMaxWidth(),
                    color = Color.Transparent
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("0:45 / 2:30", color = Color.White, fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        LinearProgressIndicator(progress = 0.3f, modifier = Modifier.weight(1f), color = Color(0xFFE27553))
                    }
                }
            }

            // Info Section
            Card(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Sobre este video", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("En este video podrás ver el proceso de preparación de ${plato.name}. Observa las técnicas y pasos principales que hacen especial a este plato.", fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(color = Color(0xFFFDEEE9), shape = RoundedCornerShape(8.dp)) {
                        Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.Top) {
                            Text("ℹ️", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Este video muestra el proceso general. Las cantidades exactas y técnicas específicas son parte del secreto de cada chef.", fontSize = 12.sp)
                        }
                    }
                }
            }

            // RA Card
            Card(
                modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🚀", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Próximamente: Realidad Aumentada", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Pronto podrás visualizar el proceso de preparación en 3D con Realidad Aumentada. Podrás ver cada paso desde todos los ángulos.", color = Color.Gray, fontSize = 12.sp)
                }
            }

            // Ingredients
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Ingredientes del video", color = Color.Gray, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White
                ) {
                   Column(modifier = Modifier.padding(16.dp)) {
                       val chunks = plato.ingredients.chunked(2)
                       chunks.forEach { rowIngredients ->
                           Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                               rowIngredients.forEach { ingredient ->
                                   Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                                       Surface(modifier = Modifier.size(6.dp), shape = CircleShape, color = Color(0xFFE27553)) {}
                                       Spacer(modifier = Modifier.width(8.dp))
                                       Text(ingredient, fontSize = 14.sp, color = Color.Black)
                                   }
                               }
                               if (rowIngredients.size == 1) Spacer(modifier = Modifier.weight(1f))
                           }
                       }
                   }
                }
            }
        }
    }
}
