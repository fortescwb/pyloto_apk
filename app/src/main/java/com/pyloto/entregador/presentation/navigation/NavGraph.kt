package com.pyloto.entregador.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.pyloto.entregador.presentation.auth.login.LoginScreen
import com.pyloto.entregador.presentation.auth.register.RegisterScreen
import com.pyloto.entregador.presentation.home.NewHomeScreen
import com.pyloto.entregador.presentation.corrida.ativa.CorridaAtivaScreen
import com.pyloto.entregador.presentation.corrida.disponivel.CorridaDetalhesScreen
import com.pyloto.entregador.presentation.corrida.historico.HistoricoScreen
import com.pyloto.entregador.presentation.corridas.CorridasScreen
import com.pyloto.entregador.presentation.ganhos.GanhosScreen
import com.pyloto.entregador.presentation.perfil.PerfilScreen
import com.pyloto.entregador.presentation.chat.ChatScreen

@Composable
fun PylotoNavGraph(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // ==================== Auth ====================
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER)
                }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ==================== Main ====================
        composable(Routes.HOME) {
            NewHomeScreen(
                onCorridaClick = { corridaId ->
                    navController.navigate(Routes.corridaDetalhes(corridaId))
                },
                onCorridaAccept = { corridaId ->
                    navController.navigate(Routes.corridaAtiva(corridaId))
                },
                onPerfilClick = { navController.navigate(Routes.PERFIL) },
                onHistoricoClick = { navController.navigate(Routes.HISTORICO) },
                onCorridasClick = { navController.navigate(Routes.CORRIDAS) },
                onGanhosClick = { navController.navigate(Routes.GANHOS) }
            )
        }

        composable(
            route = "${Routes.CORRIDA_DETALHES}/{corridaId}",
            arguments = listOf(navArgument("corridaId") { type = NavType.StringType })
        ) { backStackEntry ->
            val corridaId = backStackEntry.arguments?.getString("corridaId").orEmpty()
            CorridaDetalhesScreen(
                corridaId = corridaId,
                onNavigateBack = { navController.popBackStack() },
                onCorridaAceita = {
                    navController.navigate(Routes.corridaAtiva(corridaId)) {
                        popUpTo(Routes.HOME)
                    }
                }
            )
        }

        composable(
            route = "${Routes.CORRIDA_ATIVA}/{corridaId}",
            arguments = listOf(navArgument("corridaId") { type = NavType.StringType })
        ) { backStackEntry ->
            val corridaId = backStackEntry.arguments?.getString("corridaId").orEmpty()
            CorridaAtivaScreen(
                corridaId = corridaId,
                onCorridaFinalizada = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                },
                onChatClick = { corridaId ->
                    navController.navigate(Routes.chat(corridaId))
                }
            )
        }

        composable(Routes.CORRIDAS) {
            CorridasScreen(
                onCorridaClick = { corridaId ->
                    navController.navigate(Routes.corridaDetalhes(corridaId))
                },
                onHomeClick = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                },
                onGanhosClick = { navController.navigate(Routes.GANHOS) },
                onPerfilClick = { navController.navigate(Routes.PERFIL) }
            )
        }

        composable(Routes.HISTORICO) {
            HistoricoScreen(
                onNavigateBack = { navController.popBackStack() },
                onCorridaClick = { corridaId ->
                    navController.navigate(Routes.corridaDetalhes(corridaId))
                }
            )
        }

        composable(Routes.GANHOS) {
            GanhosScreen(
                onHomeClick = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                },
                onCorridasClick = { navController.navigate(Routes.CORRIDAS) },
                onPerfilClick = { navController.navigate(Routes.PERFIL) }
            )
        }

        composable(Routes.PERFIL) {
            PerfilScreen(
                onNavigateBack = { navController.popBackStack() },
                onGanhosClick = { navController.navigate(Routes.GANHOS) },
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = "${Routes.CHAT}/{corridaId}",
            arguments = listOf(navArgument("corridaId") { type = NavType.StringType })
        ) {
            ChatScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
