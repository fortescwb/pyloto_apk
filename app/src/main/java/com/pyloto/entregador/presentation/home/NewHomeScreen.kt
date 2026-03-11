package com.pyloto.entregador.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pyloto.entregador.domain.model.DailyStats
import com.pyloto.entregador.presentation.home.components.CompactMapSection
import com.pyloto.entregador.presentation.home.components.DailyGoalCard
import com.pyloto.entregador.presentation.home.components.DailyStatsSection
import com.pyloto.entregador.presentation.home.components.EnrichedCorridaCard
import com.pyloto.entregador.presentation.home.components.HomeHeader
import com.pyloto.entregador.presentation.theme.PylotoColors
import com.pyloto.entregador.presentation.theme.PylotoTheme

/**
 * ═══════════════════════════════════════════════════════════════
 * NOVA HOME SCREEN — VERSÃO REDESENHADA
 * ═══════════════════════════════════════════════════════════════
 *
 * Estrutura:
 * 1. Header fixo (verde militar) com logo Pyloto + status online/offline
 * 2. Scroll container (LazyColumn):
 *    - Dashboard de estatísticas do dia (grid 2x2)
 *    - Meta diária com barra de progresso animada
 *    - Mapa compacto (opcional, expansível)
 *    - Header de pedidos disponíveis
 *    - Lista de pedidos enriquecidos (EnrichedCorridaCard)
 * 3. Bottom Navigation fixo (4 abas: Início, Corridas, Ganhos, Perfil)
 *
 * Todos os dados de estatísticas são scaffold/placeholder enquanto
 * a integração com o CORE e banco de dados não estão prontos.
 * Os callbacks e eventos já estão conectados ao ViewModel.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewHomeScreen(
    onCorridaClick: (String) -> Unit,
    onCorridaAccept: (String) -> Unit = {},
    onPerfilClick: () -> Unit,
    onHistoricoClick: () -> Unit,
    onCorridasClick: () -> Unit = onHistoricoClick,
    onGanhosClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            HomeHeader(
                isOnline = uiState.isOnline,
                cidade = uiState.cidadeAtual,
                regiao = uiState.regiaoAtual,
                onToggleOnline = viewModel::toggleOnlineStatus
            )
        },
        bottomBar = {
            EnhancedBottomNavigation(
                selectedTab = "home",
                onHomeClick = { /* já está na home */ },
                onCorridasClick = onCorridasClick,
                onGanhosClick = onGanhosClick,
                onPerfilClick = onPerfilClick
            )
        },
        containerColor = PylotoColors.Parchment
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                LoadingState(modifier = Modifier.padding(paddingValues))
            }

            uiState.erro != null -> {
                ErrorState(
                    error = uiState.erro,
                    onRetry = viewModel::loadCorridas,
                    modifier = Modifier.padding(paddingValues)
                )
            }

            else -> {
                HomeContent(
                    uiState = uiState,
                    onCorridaAccept = { corridaId ->
                        viewModel.aceitarCorrida(corridaId)
                        onCorridaAccept(corridaId)
                    },
                    onCorridaDetails = onCorridaClick,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// CONTEÚDO SCROLLÁVEL
// ═══════════════════════════════════════════════════════════════

@Composable
private fun HomeContent(
    uiState: HomeUiState,
    onCorridaAccept: (String) -> Unit,
    onCorridaDetails: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 16.dp,
            bottom = 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ── Seção 1: Estatísticas do Dia ─────────────────────
        item(key = "daily_stats") {
            DailyStatsSection(
                earnings = uiState.dailyStats.earnings,
                deliveries = uiState.dailyStats.deliveries,
                timeOnline = uiState.dailyStats.timeOnlineFormatted,
                timeRemaining = uiState.dailyStats.timeRemainingFormatted
            )
        }

        // ── Seção 2: Meta do Dia ─────────────────────────────
        item(key = "daily_goal") {
            DailyGoalCard(
                currentEarnings = uiState.dailyStats.earnings,
                goalAmount = uiState.dailyGoal
            )
        }

        // ── Seção 3: Mapa Compacto (se localização disponível) ──
        if (uiState.localizacaoAtual != null) {
            item(key = "compact_map") {
                CompactMapSection(
                    location = uiState.localizacaoAtual,
                    availableOrders = uiState.corridas.size,
                    onExpand = {
                        // TODO: Navegar para mapa fullscreen
                        // ou abrir modal com mapa expandido
                    }
                )
            }
        }

        // ── Seção 4: Header de Pedidos Disponíveis ───────────
        item(key = "orders_header") {
            AvailableOrdersHeader(
                count = uiState.corridas.size
            )
        }

        // ── Seção 5: Lista de Pedidos ────────────────────────
        if (uiState.corridas.isEmpty()) {
            item(key = "empty_orders") {
                EmptyOrdersState()
            }
        } else {
            items(
                items = uiState.corridas,
                key = { it.id }
            ) { corrida ->
                EnrichedCorridaCard(
                    corrida = corrida,
                    onAccept = onCorridaAccept,
                    onViewDetails = onCorridaDetails
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// COMPONENTES AUXILIARES DA TELA
// ═══════════════════════════════════════════════════════════════

/**
 * Header da seção de pedidos disponíveis com contagem.
 */
@Composable
private fun AvailableOrdersHeader(
    count: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Pedidos Disponíveis",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = PylotoColors.Black
        )
        Text(
            text = "$count disponíveis",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = PylotoColors.TechBlue
        )
    }
}

/**
 * Bottom Navigation melhorado com 4 abas e cores Pyloto.
 * Nova aba "Ganhos" adicionada conforme redesign.
 */
@Composable
private fun EnhancedBottomNavigation(
    selectedTab: String,
    onHomeClick: () -> Unit,
    onCorridasClick: () -> Unit,
    onGanhosClick: () -> Unit,
    onPerfilClick: () -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = selectedTab == "home",
            onClick = onHomeClick,
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text("Início") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PylotoColors.MilitaryGreen,
                selectedTextColor = PylotoColors.MilitaryGreen,
                indicatorColor = PylotoColors.MilitaryGreen.copy(alpha = 0.1f)
            )
        )
        NavigationBarItem(
            selected = selectedTab == "corridas",
            onClick = onCorridasClick,
            icon = { Icon(Icons.Default.List, contentDescription = null) },
            label = { Text("Corridas") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PylotoColors.MilitaryGreen,
                selectedTextColor = PylotoColors.MilitaryGreen,
                indicatorColor = PylotoColors.MilitaryGreen.copy(alpha = 0.1f)
            )
        )
        NavigationBarItem(
            selected = selectedTab == "ganhos",
            onClick = onGanhosClick,
            icon = { Icon(Icons.Default.AccountBalance, contentDescription = null) },
            label = { Text("Ganhos") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PylotoColors.MilitaryGreen,
                selectedTextColor = PylotoColors.MilitaryGreen,
                indicatorColor = PylotoColors.MilitaryGreen.copy(alpha = 0.1f)
            )
        )
        NavigationBarItem(
            selected = selectedTab == "perfil",
            onClick = onPerfilClick,
            icon = { Icon(Icons.Default.Person, contentDescription = null) },
            label = { Text("Perfil") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PylotoColors.MilitaryGreen,
                selectedTextColor = PylotoColors.MilitaryGreen,
                indicatorColor = PylotoColors.MilitaryGreen.copy(alpha = 0.1f)
            )
        )
    }
}

// ═══════════════════════════════════════════════════════════════
// ESTADOS DA TELA
// ═══════════════════════════════════════════════════════════════

/**
 * Estado de carregamento com indicador circular verde Pyloto.
 */
@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                color = PylotoColors.MilitaryGreen
            )
            Text(
                text = "Carregando...",
                style = MaterialTheme.typography.bodyLarge,
                color = PylotoColors.TextSecondary
            )
        }
    }
}

/**
 * Estado de erro com botão de retry.
 */
@Composable
private fun ErrorState(
    error: String?,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = null,
                tint = PylotoColors.StatusRejected,
                modifier = Modifier.size(48.dp)
            )
            Text(
                text = "Erro ao carregar dados",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            if (error != null) {
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyMedium,
                    color = PylotoColors.TextSecondary
                )
            }
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = PylotoColors.MilitaryGreen
                )
            ) {
                Text("Tentar novamente")
            }
        }
    }
}

/**
 * Estado vazio — nenhum pedido disponível.
 */
@Composable
private fun EmptyOrdersState() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.LocalShipping,
                contentDescription = null,
                tint = PylotoColors.TextSecondary,
                modifier = Modifier.size(64.dp)
            )
            Text(
                text = "Nenhum pedido disponível",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Novos pedidos aparecerão aqui em breve",
                style = MaterialTheme.typography.bodyMedium,
                color = PylotoColors.TextSecondary
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// PREVIEW
// ═══════════════════════════════════════════════════════════════

@Preview(showBackground = true, heightDp = 1200)
@Composable
private fun NewHomeScreenPreview() {
    PylotoTheme {
        val mockUiState = HomeUiState(
            isLoading = false,
            isOnline = true,
            cidadeAtual = "Ponta Grossa, PR",
            regiaoAtual = "Centro",
            dailyStats = DailyStats(
                earnings = 245.50,
                deliveries = 12,
                timeOnlineMinutes = 332,
                totalFeeSavings = 24.55,
                averagePerHour = 44.51
            ),
            dailyGoal = 300.0,
            corridas = emptyList(),
            localizacaoAtual = HomeLocation(-25.095, -50.1773),
            erro = null
        )

        Scaffold(
            topBar = {
                HomeHeader(
                    isOnline = mockUiState.isOnline,
                    cidade = mockUiState.cidadeAtual,
                    regiao = mockUiState.regiaoAtual,
                    onToggleOnline = {}
                )
            },
            bottomBar = {
                EnhancedBottomNavigation(
                    selectedTab = "home",
                    onHomeClick = {},
                    onCorridasClick = {},
                    onGanhosClick = {},
                    onPerfilClick = {}
                )
            },
            containerColor = PylotoColors.Parchment
        ) { paddingValues ->
            HomeContent(
                uiState = mockUiState,
                onCorridaAccept = {},
                onCorridaDetails = {},
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}
