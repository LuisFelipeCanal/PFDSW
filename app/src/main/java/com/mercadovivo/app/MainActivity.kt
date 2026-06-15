package com.mercadovivo.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mercadovivo.app.auth.AuthViewModel
import com.mercadovivo.app.navigation.MarketRoutes
import com.mercadovivo.app.navigation.MercadoVivoNavGraph
import com.mercadovivo.app.ui.HuariqueViewModel
import com.mercadovivo.app.ui.components.MarketBottomNavigationBar
import com.mercadovivo.app.ui.theme.MercadoVivoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MercadoVivoTheme {
                MercadoVivoApp()
            }
        }
    }
}

@Composable
fun MercadoVivoApp() {
    val authViewModel: AuthViewModel = viewModel()
    val huariqueViewModel: HuariqueViewModel = viewModel()
    val navController = rememberNavController()
    
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    // Rutas principales donde SI debe aparecer la barra
    val mainRoutes = listOf(
        MarketRoutes.HOME,
        MarketRoutes.MAP,
        MarketRoutes.FAVORITES,
        MarketRoutes.PROFILE,
        MarketRoutes.DETAIL,
        MarketRoutes.ADMIN_PANEL,
        MarketRoutes.ADMIN_EDIT
    )

    // Solo mostrar si el usuario está autenticado y la ruta es una de las principales
    val showBottomBar = authViewModel.currentUser != null && 
                       mainRoutes.any { currentRoute?.startsWith(it.substringBefore("/")) == true }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                MarketBottomNavigationBar(navController = navController)
            }
        }
    ) { innerPadding ->
        MercadoVivoNavGraph(
            authViewModel = authViewModel, 
            huariqueViewModel = huariqueViewModel,
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
