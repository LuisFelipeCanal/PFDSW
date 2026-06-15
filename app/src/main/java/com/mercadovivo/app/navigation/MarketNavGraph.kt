package com.mercadovivo.app.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.compose.ui.graphics.Color
import com.mercadovivo.app.auth.AuthViewModel
import com.mercadovivo.app.auth.LoginScreen
import com.mercadovivo.app.auth.RegisterScreen
import com.mercadovivo.app.ui.*

object MarketRoutes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val MAP = "map/{selectedId}"
    const val DETAIL = "detail/{id}"
    const val MENU = "menu/{id}"
    const val DISH_DETAIL = "dish/{id}/{dishId}"
    const val VIDEO_PREP = "video/{id}/{dishId}"
    const val FAVORITES = "favorites"
    const val PROFILE = "profile"
    const val EDIT_PROFILE = "edit_profile"
    const val ADDRESSES = "addresses"
    const val NOTIFICATIONS = "notifications"
    const val PRIVACY = "privacy"
    const val HELP = "help"
    const val ADMIN_PANEL = "admin_panel"
    const val ADMIN_EDIT = "admin_edit/{huariqueId}"

    fun mapRoute(selectedId: String = "all") = "map/$selectedId"
    fun detailRoute(id: String) = "detail/$id"
    fun menuRoute(id: String) = "menu/$id"
    fun dishDetailRoute(id: String, dishId: String) = "dish/$id/$dishId"
    fun videoPrepRoute(id: String, dishId: String) = "video/$id/$dishId"
    fun adminEditRoute(huariqueId: String = "new") = "admin_edit/$huariqueId"
}

@Composable
fun MercadoVivoNavGraph(
    authViewModel: AuthViewModel,
    huariqueViewModel: HuariqueViewModel,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val huariques = huariqueViewModel.huariques

    NavHost(
        navController = navController, 
        startDestination = MarketRoutes.SPLASH,
        modifier = modifier
    ) {
        composable(MarketRoutes.SPLASH) {
            androidx.compose.runtime.LaunchedEffect(authViewModel.currentUser) {
                if (authViewModel.currentUser != null) {
                    navController.navigate(MarketRoutes.HOME) {
                        popUpTo(MarketRoutes.SPLASH) { inclusive = true }
                    }
                }
            }
            SplashScreen(
                onRegisterClick = { navController.navigate(MarketRoutes.REGISTER) },
                onLoginClick = { 
                    navController.navigate(MarketRoutes.LOGIN) {
                        popUpTo(MarketRoutes.SPLASH) { inclusive = true }
                    }
                }
            )
        }

        composable(MarketRoutes.LOGIN) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(MarketRoutes.HOME) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onRegisterClick = { 
                    if (navController.currentDestination?.route != MarketRoutes.REGISTER) {
                        navController.navigate(MarketRoutes.REGISTER) 
                    }
                },
                onBack = { 
                    if (navController.previousBackStackEntry != null) {
                        navController.popBackStack()
                    }
                }
            )
        }

        composable(MarketRoutes.REGISTER) {
            RegisterScreen(
                viewModel = authViewModel,
                onRegisterSuccess = {
                    navController.navigate(MarketRoutes.LOGIN) {
                        popUpTo(MarketRoutes.REGISTER) { inclusive = true }
                    }
                },
                onBackToLogin = { navController.popBackStack() }
            )
        }

        composable(MarketRoutes.HOME) {
            HomeScreen(
                huariques = huariques,
                isLoading = huariqueViewModel.isLoading,
                onOpenDetail = { id -> 
                    if (navController.currentDestination?.route?.contains("detail") != true) {
                        navController.navigate(MarketRoutes.detailRoute(id)) {
                            launchSingleTop = true
                        }
                    }
                },
                onOpenMap = { selectedId -> 
                    if (navController.currentDestination?.route?.contains("map") != true) {
                        navController.navigate(MarketRoutes.mapRoute(selectedId)) {
                            launchSingleTop = true
                        }
                    }
                }
            )
        }

        composable(
            route = MarketRoutes.DETAIL,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id").orEmpty()
            val huarique = huariqueViewModel.findById(id) ?: huariques.firstOrNull()
            if (huarique != null) {
                HuariqueDetailScreen(
                    huarique = huarique,
                    onBack = { 
                        if (navController.currentDestination?.route?.contains("detail") == true) {
                            navController.popBackStack() 
                        }
                    },
                    onOpenMap = { selectedId -> 
                        if (navController.currentDestination?.route?.contains("map") != true) {
                            navController.navigate(MarketRoutes.mapRoute(selectedId)) {
                                launchSingleTop = true
                            }
                        }
                    },
                    onSeeAllMenu = { 
                        if (navController.currentDestination?.route?.contains("menu") != true) {
                            navController.navigate(MarketRoutes.menuRoute(huarique.id)) {
                                launchSingleTop = true
                            }
                        }
                    },
                    onDishClick = { dish -> 
                        if (navController.currentDestination?.route?.contains("dish") != true) {
                            navController.navigate(MarketRoutes.dishDetailRoute(huarique.id, dish.id)) {
                                launchSingleTop = true
                            }
                        }
                    }
                )
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }

        composable(
            route = MarketRoutes.MENU,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id").orEmpty()
            val huarique = huariqueViewModel.findById(id)
            if (huarique != null) {
                FullMenuScreen(
                    huarique = huarique,
                    onBack = { 
                        if (navController.currentDestination?.route?.contains("menu") == true) {
                            navController.popBackStack()
                        }
                    },
                    onDishClick = { dish -> 
                        navController.navigate(MarketRoutes.dishDetailRoute(huarique.id, dish.id)) {
                            launchSingleTop = true
                        }
                    }
                )
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }

        composable(
            route = MarketRoutes.DISH_DETAIL,
            arguments = listOf(
                navArgument("id") { type = NavType.StringType },
                navArgument("dishId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id").orEmpty()
            val dishId = backStackEntry.arguments?.getString("dishId").orEmpty()
            val huarique = huariqueViewModel.findById(id)
            val dish = huarique?.menuPlates?.find { it.id == dishId } 
                ?: huarique?.menuBeverages?.find { it.id == dishId }
                ?: huarique?.menuDesserts?.find { it.id == dishId }
            
            if (huarique != null && dish != null) {
                DishDetailScreen(
                    huarique = huarique,
                    plato = dish,
                    onBack = { 
                        if (navController.currentDestination?.route?.contains("dish") == true) {
                            navController.popBackStack()
                        }
                    },
                    onWatchVideo = { 
                        navController.navigate(MarketRoutes.videoPrepRoute(id, dishId)) {
                            launchSingleTop = true
                        }
                    }
                )
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFFE27553))
                }
            }
        }

        composable(
            route = MarketRoutes.VIDEO_PREP,
            arguments = listOf(
                navArgument("id") { type = NavType.StringType },
                navArgument("dishId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id").orEmpty()
            val dishId = backStackEntry.arguments?.getString("dishId").orEmpty()
            val huarique = huariqueViewModel.findById(id)
            val dish = huarique?.menuPlates?.find { it.id == dishId }
                ?: huarique?.menuBeverages?.find { it.id == dishId }
                ?: huarique?.menuDesserts?.find { it.id == dishId }
            
            if (huarique != null && dish != null) {
                VideoPrepScreen(
                    huarique = huarique,
                    plato = dish,
                    onBack = { 
                        if (navController.currentDestination?.route?.contains("video") == true) {
                            navController.popBackStack()
                        }
                    }
                )
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFFE27553))
                }
            }
        }

        composable(MarketRoutes.MAP, arguments = listOf(navArgument("selectedId") { type = NavType.StringType })) { backStackEntry ->
            val selectedId = backStackEntry.arguments?.getString("selectedId") ?: "all"
            MapScreen(
                huariques = huariques,
                selectedId = selectedId,
                onOpenDetail = { id -> navController.navigate(MarketRoutes.detailRoute(id)) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(MarketRoutes.FAVORITES) {
            FavoritesScreen(
                favoriteHuariques = emptyList(),
                onOpenDetail = { id -> navController.navigate(MarketRoutes.detailRoute(id)) }
            )
        }

        composable(MarketRoutes.PROFILE) {
            ProfileScreen(
                authViewModel = authViewModel,
                onNavigateToEditProfile = { navController.navigate(MarketRoutes.EDIT_PROFILE) },
                onNavigateToAddresses = { navController.navigate(MarketRoutes.ADDRESSES) },
                onNavigateToNotifications = { navController.navigate(MarketRoutes.NOTIFICATIONS) },
                onNavigateToPrivacy = { navController.navigate(MarketRoutes.PRIVACY) },
                onNavigateToHelp = { navController.navigate(MarketRoutes.HELP) },
                onNavigateToAdmin = { navController.navigate(MarketRoutes.ADMIN_PANEL) },
                onLogout = {
                    navController.navigate(MarketRoutes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(MarketRoutes.EDIT_PROFILE) {
            EditProfileScreen(
                authViewModel = authViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(MarketRoutes.ADDRESSES) {
            AddressesScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(MarketRoutes.NOTIFICATIONS) {
            NotificationsScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(MarketRoutes.PRIVACY) {
            PrivacySecurityScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(MarketRoutes.HELP) {
            HelpSupportScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(MarketRoutes.ADMIN_PANEL) {
            AdminPanelScreen(
                huariques = huariques,
                onEditHuarique = { id: String -> navController.navigate(MarketRoutes.adminEditRoute(id)) },
                onAddHuarique = { navController.navigate(MarketRoutes.adminEditRoute("new")) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = MarketRoutes.ADMIN_EDIT,
            arguments = listOf(navArgument("huariqueId") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("huariqueId")
            val huariqueToEdit = if (id != "new") huariqueViewModel.findById(id!!) else null
            AdminHuariqueEditScreen(
                huarique = huariqueToEdit,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
