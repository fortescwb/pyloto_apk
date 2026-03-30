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
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Home
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.hilt.navigation.compose.hiltViewModel
import com.pyloto.entregador.core.calendar.CalendarPermissionChecker
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

    val calendarPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            viewModel.onCalendarPermissionGranted()
        }
    }

    LaunchedEffect(uiState.agendaTrabalho) {
        if (uiState.agendaTrabalho != null && !viewModel.hasCalendarPermission()) {
            calendarPermissionLauncher.launch(CalendarPermissionChecker.REQUIRED_PERMISSIONS)
        }
    }

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
                    onRetry = {
                        viewModel.loadCorridas()
                        viewModel.loadOperationalCapacity()
                        viewModel.loadAgendaTrabalho()
                    },
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
                    onAgendarDia = viewModel::agendarDia,
                    onCancelarAgendamento = viewModel::cancelarAgendamento,
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
    onAgendarDia: (String) -> Unit,
    onCancelarAgendamento: (String) -> Unit,
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

        if (uiState.operationalCapacity != null) {
            item(key = "operational_capacity") {
                OperationalCapacityCard(
                    uiState = uiState,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // ── Seção 3: Mapa Compacto (se localização disponível) ──
        if (uiState.agendaTrabalho != null) {
            item(key = "agenda_trabalho") {
                AgendaTrabalhoCard(
                    uiState = uiState,
                    onAgendarDia = onAgendarDia,
                    onCancelarAgendamento = onCancelarAgendamento,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

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
private fun OperationalCapacityCard(
    uiState: HomeUiState,
    modifier: Modifier = Modifier
) {
    val capacity = uiState.operationalCapacity ?: return
    val containerColor = when {
        capacity.isBlocked -> Color(0xFFFFF1F0)
        capacity.isNearLimit -> Color(0xFFFFF7E5)
        else -> Color.White
    }
    val titleColor = when {
        capacity.isBlocked -> Color(0xFFB42318)
        capacity.isNearLimit -> Color(0xFFB54708)
        else -> PylotoColors.MilitaryGreen
    }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Capacidade do baú",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = titleColor
                    )
                    Text(
                        text = "Baú ${capacity.bauCapacidadeLitros}L • regra ${capacity.policyVersion}",
                        style = MaterialTheme.typography.bodySmall,
                        color = PylotoColors.TextSecondary
                    )
                }
                Text(
                    text = when {
                        capacity.isBlocked -> "Bloqueado"
                        capacity.isNearLimit -> "Margem curta"
                        else -> "Disponível"
                    },
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = titleColor
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CapacityMetricColumn(
                    label = "Volume",
                    remaining = "${capacity.remaining.volumeLitros.toInt()} L",
                    limit = "${capacity.limits.volumeLitros.toInt()} L",
                    modifier = Modifier.weight(1f)
                )
                CapacityMetricColumn(
                    label = "Peso",
                    remaining = "${capacity.remaining.pesoKg.toInt()} kg",
                    limit = "${capacity.limits.pesoKg.toInt()} kg",
                    modifier = Modifier.weight(1f)
                )
                CapacityMetricColumn(
                    label = "Valor",
                    remaining = formatCurrency(capacity.remaining.valorReais),
                    limit = formatCurrency(capacity.limits.valorReais),
                    modifier = Modifier.weight(1f)
                )
            }

            if (capacity.blockedReason.isNotBlank() || capacity.nearLimitDimensions.isNotEmpty()) {
                Text(
                    text = capacity.blockedReason.ifBlank {
                        "Próximo do limite em ${capacity.nearLimitDimensions.joinToString(", ") { it.replace("_", " ") }}."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = titleColor
                )
            }
        }
    }
}

@Composable
private fun AgendaTrabalhoCard(
    uiState: HomeUiState,
    onAgendarDia: (String) -> Unit,
    onCancelarAgendamento: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val agenda = uiState.agendaTrabalho ?: return
    val bloqueioAtivo = agenda.bloqueioAbertura.ativo
    val bucketHoje = agenda.operacaoHoje.bucket
    val containerColor = when {
        bloqueioAtivo -> Color(0xFFFFF4E8)
        bucketHoje == "agendado" -> Color(0xFFF3FBF6)
        else -> Color.White
    }
    val titleColor = when {
        bloqueioAtivo -> Color(0xFFB54708)
        bucketHoje == "agendado" -> PylotoColors.MilitaryGreen
        else -> PylotoColors.Black
    }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Agenda de trabalho",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = titleColor
                )
                Text(
                    text = agenda.operacaoHoje.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = PylotoColors.TextSecondary
                )
                if (bloqueioAtivo && agenda.bloqueioAbertura.motivo.isNotBlank()) {
                    Text(
                        text = agenda.bloqueioAbertura.motivo,
                        style = MaterialTheme.typography.bodySmall,
                        color = titleColor
                    )
                }
            }

            agenda.dias.forEach { dia ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = when (dia.status) {
                            "agendado" -> Color(0xFFEAF7EE)
                            "cancelado_tardio", "no_show" -> Color(0xFFFFF1F0)
                            else -> Color.White
                        }
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = dia.titulo,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "${dia.data} • ${dia.inicioLocal} às ${dia.fimLocal}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = PylotoColors.TextSecondary
                                )
                            }
                            Text(
                                text = formatAgendaStatus(dia.status),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = when (dia.status) {
                                    "agendado" -> PylotoColors.MilitaryGreen
                                    "cancelado_tardio", "no_show" -> Color(0xFFB42318)
                                    else -> PylotoColors.TechBlue
                                }
                            )
                        }

                        if (!dia.mensagem.isNullOrBlank()) {
                            Text(
                                text = dia.mensagem,
                                style = MaterialTheme.typography.bodySmall,
                                color = PylotoColors.TextSecondary
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            if (dia.canSchedule) {
                                Button(
                                    onClick = { onAgendarDia(dia.data) },
                                    enabled = !uiState.isUpdatingAgenda
                                ) {
                                    if (uiState.isUpdatingAgenda) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(18.dp),
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Text("Agendar")
                                    }
                                }
                            }

                            if (dia.canCancel && dia.agendamentoId != null) {
                                Button(
                                    onClick = { onCancelarAgendamento(dia.agendamentoId) },
                                    enabled = !uiState.isUpdatingAgenda,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFEEF2F6),
                                        contentColor = PylotoColors.TechBlue
                                    )
                                ) {
                                    Text("Cancelar")
                                }
                            }
                        }
                    }
                }
            }

            val ultimoRegistro = agenda.historico.firstOrNull()
            if (ultimoRegistro != null) {
                Text(
                    text = "Ultimo registro: ${formatAgendaStatus(ultimoRegistro.status)} em ${ultimoRegistro.data}",
                    style = MaterialTheme.typography.bodySmall,
                    color = PylotoColors.TextSecondary
                )
            }
        }
    }
}

@Composable
private fun CapacityMetricColumn(
    label: String,
    remaining: String,
    limit: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = PylotoColors.TextSecondary
        )
        Text(
            text = remaining,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = PylotoColors.Black
        )
        Text(
            text = "limite $limit",
            style = MaterialTheme.typography.bodySmall,
            color = PylotoColors.TextSecondary
        )
    }
}

private fun formatCurrency(value: Double): String {
    return "R$ %.2f".format(value)
}

private fun formatAgendaStatus(value: String): String {
    return when (value) {
        "agendado" -> "Agendado"
        "cancelado" -> "Cancelado"
        "cancelado_tardio" -> "Cancelamento tardio"
        "no_show" -> "No-show"
        "concluido" -> "Concluido"
        else -> value.replace("_", " ").replaceFirstChar { it.uppercase() }
    }
}

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
            icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
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
                onAgendarDia = {},
                onCancelarAgendamento = {},
                onCorridaDetails = {},
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}
