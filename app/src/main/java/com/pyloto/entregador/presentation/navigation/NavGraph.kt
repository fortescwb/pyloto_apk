package com.pyloto.entregador.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.pyloto.entregador.presentation.auth.login.LoginScreen
import com.pyloto.entregador.presentation.chat.ChatScreen
import com.pyloto.entregador.presentation.corrida.ativa.CorridaAtivaScreen
import com.pyloto.entregador.presentation.corrida.disponivel.CorridaDetalhesScreen
import com.pyloto.entregador.presentation.corrida.historico.HistoricoScreen
import com.pyloto.entregador.presentation.corridas.CorridasScreen
import com.pyloto.entregador.presentation.ganhos.GanhosScreen
import com.pyloto.entregador.presentation.home.NewHomeScreen
import com.pyloto.entregador.presentation.notificacoes.NotificacoesScreen
import com.pyloto.entregador.presentation.onboarding.ContractSignatureScreen
import com.pyloto.entregador.presentation.perfil.PerfilScreen

@Composable
fun PylotoNavGraph(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = { requiresOnboarding ->
                    val destination = if (requiresOnboarding) {
                        Routes.CONTRACT_SIGNATURE
                    } else {
                        Routes.HOME
                    }
                    navController.navigate(destination) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.CONTRACT_SIGNATURE) {
            ContractSignatureScreen(
                onOnboardingCompleted = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.CONTRACT_SIGNATURE) { inclusive = true }
                    }
                },
                onSkip = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.CONTRACT_SIGNATURE) { inclusive = true }
                    }
                },
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.HOME) {
            NewHomeScreen(
                onCorridaClick = { corridaId ->
                    navController.navigate(Routes.corridaDetalhes(corridaId))
                },
                onCorridaAccept = { corridaId ->
                    navController.navigate(Routes.corridaAtiva(corridaId))
                },
                onPerfilClick = { navController.navigate(Routes.PERFIL) },
                onNotificacoesClick = { navController.navigate(Routes.NOTIFICACOES) },
                onHistoricoClick = { navController.navigate(Routes.HISTORICO) },
                onCorridasClick = { navController.navigate(Routes.CORRIDAS) },
                onGanhosClick = { navController.navigate(Routes.GANHOS) }
            )
        }

        composable(Routes.NOTIFICACOES) {
            NotificacoesScreen(
                onNavigateBack = { navController.popBackStack() }
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
                },
                onCorridaRecusada = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
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
                onReturnHome = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.HOME) { inclusive = true }
                    }
                },
                onChatClick = { activeCorridaId ->
                    navController.navigate(Routes.chat(activeCorridaId))
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
        ) { backStackEntry ->
            val corridaId = backStackEntry.arguments?.getString("corridaId").orEmpty()
            ChatScreen(
                corridaId = corridaId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
