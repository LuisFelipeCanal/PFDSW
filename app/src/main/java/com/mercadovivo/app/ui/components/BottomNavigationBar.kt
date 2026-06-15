package com.mercadovivo.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.mercadovivo.app.navigation.MarketRoutes

@Composable
fun MarketBottomNavigationBar(navController: NavController) {
    val items = listOf(
        NavigationItem("Inicio", MarketRoutes.HOME, Icons.Default.Home),
        NavigationItem("Mapa", MarketRoutes.mapRoute(), Icons.Default.Map),
        NavigationItem("Favoritos", MarketRoutes.FAVORITES, Icons.Default.FavoriteBorder),
        NavigationItem("Perfil", MarketRoutes.PROFILE, Icons.Default.Person)
    )

    NavigationBar(containerColor = Color.White) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            val isSelected = currentRoute == item.route
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) },
                selected = isSelected,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFFE27553),
                    selectedTextColor = Color(0xFFE27553),
                    unselectedIconColor = Color.Gray,
                    unselectedTextColor = Color.Gray,
                    indicatorColor = Color(0xFFFDEEE9)
                ),
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(MarketRoutes.HOME) {
                            saveState = true
                        }
                        launchSingleTop = true
                        // Solo restauramos el estado si NO es la pestaña de Inicio
                        // Esto asegura que Inicio siempre regrese a la vista principal limpia
                        restoreState = item.route != MarketRoutes.HOME
                    }
                }
            )
        }
    }
}

data class NavigationItem(val title: String, val route: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)
