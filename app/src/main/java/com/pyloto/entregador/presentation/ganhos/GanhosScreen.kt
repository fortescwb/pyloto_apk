package com.pyloto.entregador.presentation.ganhos

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pyloto.entregador.domain.model.Ganhos
import com.pyloto.entregador.domain.model.GanhosExtratoItem
import com.pyloto.entregador.presentation.ganhos.components.ExtratoCorridasSection
import com.pyloto.entregador.presentation.ganhos.components.IndicadoresSection
import com.pyloto.entregador.presentation.ganhos.components.PeriodoSelector
import com.pyloto.entregador.presentation.ganhos.components.SaldoCard
import com.pyloto.entregador.presentation.theme.PylotoColors
import com.pyloto.entregador.presentation.theme.PylotoTheme
import java.math.BigDecimal
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

/**
 * Tela de Ganhos — estilo extrato bancário.
 *
 * Exibe o saldo líquido, indicadores-chave (R$/km, R$/hora, km rodados,
 * tempo online), seletor de período e a lista de corridas realizadas.
 *
 * @param onHomeClick navega para Home
 * @param onCorridasClick navega para Corridas
 * @param onPerfilClick navega para Perfil
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GanhosScreen(
    onHomeClick: () -> Unit,
    onCorridasClick: () -> Unit,
    onPerfilClick: () -> Unit,
    viewModel: GanhosViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // ── Snackbar de erro ────────────────────────────────────
    LaunchedEffect(uiState.erro) {
        uiState.erro?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.limparErro()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Ganhos",
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                },
                actions = {
                    IconButton(onClick = { viewModel.loadGanhos() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Atualizar",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PylotoColors.MilitaryGreen
                )
            )
        },
        bottomBar = {
            GanhosBottomNavigation(
                onHomeClick = onHomeClick,
                onCorridasClick = onCorridasClick,
                onGanhosClick = { /* já está na tela */ },
                onPerfilClick = onPerfilClick
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = PylotoColors.Parchment
    ) { innerPadding ->

        // ── Estado de carregamento ──────────────────────────
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    CircularProgressIndicator(color = PylotoColors.MilitaryGreen)
                    Text(
                        text = "Carregando extrato…",
                        style = MaterialTheme.typography.bodyLarge,
                        color = PylotoColors.TextSecondary
                    )
                }
            }
            return@Scaffold
        }

        // ── Conteúdo principal ──────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Seletor de período
            PeriodoSelector(
                selecionado = uiState.periodoSelecionado,
                onSelecionar = viewModel::selecionarPeriodo
            )

            // 2. Card de saldo (estilo bancário)
            AnimatedVisibility(
                visible = uiState.ganhos != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                uiState.ganhos?.let { ganhos ->
                    SaldoCard(
                        totalBruto = ganhos.totalBruto.toDouble(),
                        totalLiquido = ganhos.totalLiquido.toDouble(),
                        totalCorridas = ganhos.totalCorridas,
                        mediaValorCorrida = ganhos.mediaValorCorrida.toDouble(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            uiState.ganhos?.let { ganhos ->
                FinanceStatusSection(
                    ganhos = ganhos,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // 3. Indicadores-chave (R$/km, R$/hora, km, tempo)
            IndicadoresSection(
                ganhoPorKm = uiState.ganhoPorKm,
                ganhoPorHora = uiState.ganhoPorHora,
                totalKm = uiState.totalKmRodados,
                tempoOnlineMinutos = uiState.tempoOnlineMinutos
            )

            uiState.ganhos?.takeIf { it.extrato.isNotEmpty() }?.let { ganhos ->
                FinancialStatementSection(
                    extrato = ganhos.extrato,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // 4. Extrato de corridas realizadas
            if (uiState.corridasRealizadas.isNotEmpty()) {
                ExtratoCorridasSection(
                    corridas = uiState.corridasRealizadas,
                    onCorridaClick = { /* TODO: navegar para detalhes */ }
                )
            }

            // Espaço extra para não colar no bottom bar
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// BOTTOM NAVIGATION — "Ganhos" selecionado
// ═══════════════════════════════════════════════════════════════

@Composable
private fun FinanceStatusSection(
    ganhos: Ganhos,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Saldo de repasse",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = PylotoColors.Black
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FinanceMetric("Disponivel", ganhos.saldo.disponivel)
                FinanceMetric("Pendente", ganhos.saldo.pendente)
                FinanceMetric("Falhou", ganhos.saldo.falhou)
            }

            HorizontalDivider(color = PylotoColors.OutlineVariant)

            Text(
                text = buildString {
                    append("Pix ")
                    append(if (ganhos.pix.recebimentoValido) "validado" else "com pendencia")
                    if (ganhos.pix.tipo.isNotBlank()) {
                        append(" • ")
                        append(ganhos.pix.tipo)
                    }
                },
                style = MaterialTheme.typography.bodyMedium,
                color = if (ganhos.pix.recebimentoValido) {
                    PylotoColors.MilitaryGreen
                } else {
                    PylotoColors.StatusRejected
                }
            )

            if (ganhos.pix.pendencias.isNotEmpty()) {
                Text(
                    text = ganhos.pix.pendencias.joinToString(" "),
                    style = MaterialTheme.typography.bodySmall,
                    color = PylotoColors.TextSecondary
                )
            }

            if (ganhos.financeiro.suspensaoAtiva || ganhos.saldo.mensalidadePendente > BigDecimal.ZERO) {
                HorizontalDivider(color = PylotoColors.OutlineVariant)
                Text(
                    text = "Mensalidade em aberto: ${formatCurrency(ganhos.saldo.mensalidadePendente)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (ganhos.financeiro.suspensaoAtiva) {
                        PylotoColors.StatusRejected
                    } else {
                        PylotoColors.Black
                    }
                )
                if (ganhos.financeiro.motivoSuspensao.isNotBlank()) {
                    Text(
                        text = ganhos.financeiro.motivoSuspensao,
                        style = MaterialTheme.typography.bodySmall,
                        color = PylotoColors.StatusRejected
                    )
                }
            }
        }
    }
}

@Composable
private fun FinanceMetric(
    label: String,
    value: BigDecimal
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = PylotoColors.TextSecondary
        )
        Text(
            text = formatCurrency(value),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = PylotoColors.Black
        )
    }
}

@Composable
private fun FinancialStatementSection(
    extrato: List<GanhosExtratoItem>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Extrato financeiro",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = PylotoColors.Black,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                val items = extrato.take(8)
                items.forEachIndexed { index, item ->
                    FinancialStatementItem(item = item)
                    if (index < items.lastIndex) {
                        HorizontalDivider(
                            color = PylotoColors.OutlineVariant,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FinancialStatementItem(item: GanhosExtratoItem) {
    val positive = item.valor >= BigDecimal.ZERO
    val valueColor = if (positive) PylotoColors.MilitaryGreen else PylotoColors.StatusRejected

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = item.titulo,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = PylotoColors.Black,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${item.tipo.replace('_', ' ')} • ${item.status}",
                style = MaterialTheme.typography.labelSmall,
                color = PylotoColors.TextSecondary
            )
            if (item.taxaPlataforma > BigDecimal.ZERO) {
                Text(
                    text = "Taxa ao solicitante: ${formatCurrency(item.taxaPlataforma)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = PylotoColors.TextSecondary
                )
            }
            Text(
                text = formatIsoDateLabel(item.ocorridoEm),
                style = MaterialTheme.typography.labelSmall,
                color = PylotoColors.TextDisabled
            )
        }

        Text(
            text = if (positive) "+ ${formatCurrency(item.valor)}" else formatCurrency(item.valor),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}

private fun formatCurrency(value: BigDecimal): String {
    return "R$ %.2f".format(value.toDouble())
}

private fun formatIsoDateLabel(value: String): String {
    if (value.isBlank()) return "-"
    return runCatching {
        OffsetDateTime.parse(value)
            .format(DateTimeFormatter.ofPattern("dd/MM - HH:mm"))
    }.recoverCatching {
        LocalDate.parse(value)
            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
    }.getOrElse { value }
}

@Composable
private fun GanhosBottomNavigation(
    onHomeClick: () -> Unit,
    onCorridasClick: () -> Unit,
    onGanhosClick: () -> Unit,
    onPerfilClick: () -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        val navColors = NavigationBarItemDefaults.colors(
            selectedIconColor = PylotoColors.MilitaryGreen,
            selectedTextColor = PylotoColors.MilitaryGreen,
            indicatorColor = PylotoColors.MilitaryGreen.copy(alpha = 0.1f)
        )

        NavigationBarItem(
            selected = false,
            onClick = onHomeClick,
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text("Início") },
            colors = navColors
        )
        NavigationBarItem(
            selected = false,
            onClick = onCorridasClick,
            icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
            label = { Text("Corridas") },
            colors = navColors
        )
        NavigationBarItem(
            selected = true,
            onClick = onGanhosClick,
            icon = { Icon(Icons.Default.AccountBalance, contentDescription = null) },
            label = { Text("Ganhos") },
            colors = navColors
        )
        NavigationBarItem(
            selected = false,
            onClick = onPerfilClick,
            icon = { Icon(Icons.Default.Person, contentDescription = null) },
            label = { Text("Perfil") },
            colors = navColors
        )
    }
}

// ═══════════════════════════════════════════════════════════════
// PREVIEW
// ═══════════════════════════════════════════════════════════════

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun GanhosScreenPreview() {
    PylotoTheme {
        Scaffold(
            containerColor = PylotoColors.Parchment
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Preview — use emulador para ver com dados mock",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PylotoColors.TextSecondary
                )
            }
        }
    }
}
