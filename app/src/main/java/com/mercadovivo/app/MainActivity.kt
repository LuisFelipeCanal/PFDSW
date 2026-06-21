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
import androidx.compose.runtime.*
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
    
    // Lista local para evitar spam de notificaciones en la misma sesión
    val notifiedIds = remember { mutableSetOf<String>() }

    // Observador de Proximidad Inteligente
    LaunchedEffect(huariqueViewModel.huariques, huariqueViewModel.userLocation, authViewModel.userData) {
        val location = huariqueViewModel.userLocation
        val nearEnabled = authViewModel.userData?.nearHuariqueNotificationsEnabled ?: true
        
        if (location != null && huariqueViewModel.isLocationEnabled && nearEnabled) {
            huariqueViewModel.huariques.filter { it.isVerified }.forEach { huarique ->
                // REGLA 1: Solo si no hemos avisado ya de este local
                if (!notifiedIds.contains(huarique.id) && huarique.lat != null && huarique.lng != null) {
                    
                    // REGLA 2: Solo si el local está ABIERTO ahora mismo
                    val isOpen = com.mercadovivo.app.utils.TimeUtils.isStoreOpen(huarique.horario)
                    
                    if (isOpen) {
                        val distance = com.mercadovivo.app.utils.LocationUtils.calculateDistance(
                            location,
                            LatLng(huarique.lat, huarique.lng)
                        )
                        
                        // REGLA 3: Si está a menos de 200m
                        if (distance <= 200f) {
                            notifiedIds.add(huarique.id) // Lo marcamos como "avisado"
                            com.mercadovivo.app.utils.NotificationHelper.showNotification(
                                context,
                                "¡Huarique cerca!",
                                "${huarique.name} está abierto a ${distance.toInt()}m. ¡Pasa a visitarlo!"
                            )
                        }
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
            onRefresh = {
                huariqueViewModel.refresh()
                authViewModel.refresh()
                if (huariqueViewModel.isLocationEnabled) {
                    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                    try {
                        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                            location?.let {
                                huariqueViewModel.userLocation = LatLng(it.latitude, it.longitude)
                            }
                        }
                    } catch (e: SecurityException) {}
                }
            },
            modifier = Modifier.padding(innerPadding)
        )
    }
}
