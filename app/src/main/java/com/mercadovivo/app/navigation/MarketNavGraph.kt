package com.mercadovivo.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mercadovivo.app.auth.AuthViewModel
import com.mercadovivo.app.auth.LoginScreen
import com.mercadovivo.app.auth.RegisterScreen
import com.mercadovivo.app.data.HuariqueRepository
import com.mercadovivo.app.ui.HomeScreen
import com.mercadovivo.app.ui.HuariqueDetailScreen
import com.mercadovivo.app.ui.MapScreen

object MarketRoutes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val MAP = "map/{selectedId}"
    const val DETAIL = "detail/{id}"

    fun mapRoute(selectedId: String = "all") = "map/$selectedId"
    fun detailRoute(id: String) = "detail/$id"
}

@Composable
fun MercadoVivoNavGraph(authViewModel: AuthViewModel) {
    val navController = rememberNavController()
    val repo = remember { HuariqueRepository() }
    val huariques = remember { repo.getHuariques() }

    NavHost(navController = navController, startDestination = MarketRoutes.LOGIN) {
        composable(MarketRoutes.LOGIN) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(MarketRoutes.HOME) {
                        popUpTo(MarketRoutes.LOGIN) { inclusive = true }
                    }
                },
                onRegisterClick = { navController.navigate(MarketRoutes.REGISTER) }
            )
        }

        composable(MarketRoutes.REGISTER) {
            RegisterScreen(
                viewModel = authViewModel,
                onRegisterSuccess = {
                    navController.navigate(MarketRoutes.HOME) {
                        popUpTo(MarketRoutes.LOGIN) { inclusive = true }
                    }
                },
                onBackToLogin = { navController.popBackStack() }
            )
        }

        composable(MarketRoutes.HOME) {
            HomeScreen(
                huariques = huariques,
                onOpenDetail = { id -> navController.navigate(MarketRoutes.detailRoute(id)) },
                onOpenMap = { selectedId -> navController.navigate(MarketRoutes.mapRoute(selectedId)) }
            )
        }

        composable(
            route = MarketRoutes.DETAIL,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id").orEmpty()
            val huarique = repo.findById(id) ?: huariques.first()
            HuariqueDetailScreen(
                huarique = huarique,
                onBack = { navController.popBackStack() },
                onOpenMap = { selectedId -> navController.navigate(MarketRoutes.mapRoute(selectedId)) }
            )
        }

        composable(
            route = MarketRoutes.MAP,
            arguments = listOf(navArgument("selectedId") { type = NavType.StringType })
        ) { backStackEntry ->
            val selectedId = backStackEntry.arguments?.getString("selectedId") ?: "all"
            MapScreen(
                huariques = huariques,
                selectedId = selectedId,
                onOpenDetail = { id -> navController.navigate(MarketRoutes.detailRoute(id)) },
                onBack = { navController.popBackStack() }
            )
        }
    }
}

