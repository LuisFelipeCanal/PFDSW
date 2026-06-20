package com.mercadovivo.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.google.android.gms.maps.model.LatLng
import com.mercadovivo.app.auth.AuthViewModel
import com.mercadovivo.app.models.Huarique
import com.mercadovivo.app.ui.theme.MercadoVivoGradientEnd
import com.mercadovivo.app.ui.theme.MercadoVivoGradientStart

@Composable
fun FavoritesScreen(
    huariqueViewModel: HuariqueViewModel,
    authViewModel: AuthViewModel,
    onOpenDetail: (String) -> Unit,
    onOpenDishDetail: (String, String) -> Unit
) {
    val allHuariques = huariqueViewModel.huariques
    val favoriteIds = authViewModel.userData?.favorites ?: emptyList()
    val favoriteDishIds = authViewModel.userData?.favoriteDishes ?: emptyList()
    val userLocation = huariqueViewModel.userLocation
    
    val favoriteHuariques = allHuariques.filter { favoriteIds.contains(it.id) }
    
    // Obtener todos los platos favoritos buscando en todos los huariques
    val favoriteDishes = remember(allHuariques, favoriteDishIds) {
        val list = mutableListOf<Pair<Huarique, com.mercadovivo.app.models.Plato>>()
        allHuariques.forEach { h ->
            (h.menuPlates + h.menuBeverages + h.menuDesserts).forEach { p ->
                if (favoriteDishIds.contains(p.id)) {
                    list.add(h to p)
                }
            }
        }
        list
    }

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Locales", "Sabores")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFBF0))
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(MercadoVivoGradientStart, MercadoVivoGradientEnd)
                    ),
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                )
                .padding(24.dp)
        ) {
            Column(modifier = Modifier.align(Alignment.CenterStart)) {
                Text(
                    text = "Mis Favoritos",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${favoriteHuariques.size} locales, ${favoriteDishes.size} sabores",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
            
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White.copy(alpha = 0.2f),
                contentColor = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .height(48.dp)
                    .clip(RoundedCornerShape(24.dp)),
                indicator = { Box(modifier = Modifier.fillMaxSize().padding(4.dp).background(Color.White, RoundedCornerShape(20.dp))) },
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { 
                            Text(
                                title, 
                                color = if (selectedTab == index) Color(0xFFE27553) else Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.zIndex(1f)
                            ) 
                        }
                    )
                }
            }
        }

        if (selectedTab == 0) {
            // Tab de Locales
            if (favoriteHuariques.isEmpty()) {
                EmptyFavorites("Locales")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(favoriteHuariques) { huarique ->
                        HuariqueCardHome(
                            huarique = huarique,
                            userLocation = userLocation,
                            onClick = { onOpenDetail(huarique.id) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(100.dp)) }
                }
            }
        } else {
            // Tab de Platos
            if (favoriteDishes.isEmpty()) {
                EmptyFavorites("Platos")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(favoriteDishes) { (huarique, plato) ->
                        DishListItem(
                            plato = plato,
                            onClick = { onOpenDishDetail(huarique.id, plato.id) }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(100.dp)) }
                }
            }
        }
    }
}

@Composable
private fun EmptyFavorites(type: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("❤️", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text("No hay $type favoritos", fontWeight = FontWeight.Bold, color = Color.Gray)
            Text("Guarda lo que más te guste", color = Color.Gray, fontSize = 14.sp)
        }
    }
}
