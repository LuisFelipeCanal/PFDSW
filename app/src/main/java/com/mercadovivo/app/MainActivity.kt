package com.mercadovivo.app

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
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
    val context = LocalContext.current

    // Gestión de ubicación y notificaciones
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Manejar resultados si es necesario
    }

    LaunchedEffect(Unit) {
        val permissions = mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        
        permissionLauncher.launch(permissions.toTypedArray())
    }

    // Actualizador de ubicación en tiempo real cuando está habilitada
    LaunchedEffect(huariqueViewModel.isLocationEnabled) {
        if (huariqueViewModel.isLocationEnabled) {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        huariqueViewModel.userLocation = LatLng(it.latitude, it.longitude)
                    }
                }
            } catch (e: SecurityException) {
                // Sin permisos
            }
        } else {
            huariqueViewModel.userLocation = null
        }
    }
    
    // Observador de Proximidad para Notificaciones Push Locales
    LaunchedEffect(huariqueViewModel.huariques, huariqueViewModel.userLocation, authViewModel.userData) {
        val location = huariqueViewModel.userLocation
        val nearEnabled = authViewModel.userData?.nearHuariqueNotificationsEnabled ?: true
        
        if (location != null && huariqueViewModel.isLocationEnabled && nearEnabled) {
            huariqueViewModel.huariques.filter { it.isVerified }.forEach { huarique ->
                if (huarique.lat != null && huarique.lng != null) {
                    val distance = com.mercadovivo.app.utils.LocationUtils.calculateDistance(
                        location,
                        LatLng(huarique.lat, huarique.lng)
                    )
                    
                    // Si está a menos de 200m y es "nuevo" para esta sesión
                    if (distance <= 200f) {
                        // Verificamos si ya notificamos este local en el NotificationsScreen
                        // Para pruebas, lanzamos la notificación del sistema
                        com.mercadovivo.app.utils.NotificationHelper.showNotification(
                            context,
                            "¡Huarique cerca!",
                            "${huarique.name} está a solo ${distance.toInt()}m. ¡Pasa a visitarlo!"
                        )
                    }
                }
            }
        }
    }
    
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
