package com.pyloto.entregador.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.pyloto.entregador.presentation.auth.login.LoginScreen
import com.pyloto.entregador.presentation.auth.register.RegisterScreen
import com.pyloto.entregador.presentation.home.HomeScreen
import com.pyloto.entregador.presentation.corrida.ativa.CorridaAtivaScreen
import com.pyloto.entregador.presentation.corrida.disponivel.CorridaDetalhesScreen
import com.pyloto.entregador.presentation.corrida.historico.HistoricoScreen
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
            HomeScreen(
                onCorridaClick = { corridaId ->
                    navController.navigate(Routes.corridaDetalhes(corridaId))
                },
                onPerfilClick = { navController.navigate(Routes.PERFIL) },
                onHistoricoClick = { navController.navigate(Routes.HISTORICO) },
                onNotificacoesClick = { navController.navigate(Routes.NOTIFICACOES) }
            )
        }

        composable(
            route = "${Routes.CORRIDA_DETALHES}/{corridaId}",
            arguments = listOf(navArgument("corridaId") { type = NavType.StringType })
        ) {
            CorridaDetalhesScreen(
                onNavigateBack = { navController.popBackStack() },
                onCorridaAceita = {
                    navController.navigate(Routes.CORRIDA_ATIVA) {
                        popUpTo(Routes.HOME)
                    }
                }
            )
        }

        composable(Routes.CORRIDA_ATIVA) {
            CorridaAtivaScreen(
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

        composable(Routes.HISTORICO) {
            HistoricoScreen(
                onNavigateBack = { navController.popBackStack() },
                onCorridaClick = { corridaId ->
                    navController.navigate(Routes.corridaDetalhes(corridaId))
                }
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
