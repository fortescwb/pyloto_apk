package com.pyloto.entregador.presentation.perfil.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.DeliveryDining
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pyloto.entregador.presentation.theme.PylotoColors
import com.pyloto.entregador.presentation.theme.PylotoTheme
import java.math.BigDecimal

/**
 * Bottom Sheet de Financeiro.
 *
 * Exibe resumo financeiro do entregador com dados de ganhos.
 * Todos os campos são somente leitura — apenas visualização.
 *
 * @param totalBruto ganhos brutos totais no período
 * @param totalLiquido ganhos líquidos no período
 * @param totalCorridas total de corridas realizadas
 * @param mediaValorCorrida média de valor por corrida
 * @param onDismiss fecha o sheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceiroSheet(
    totalBruto: Double,
    totalLiquido: Double,
    totalCorridas: Int,
    mediaValorCorrida: Double,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Título
            SheetHeader(
                title = "Financeiro",
                icon = Icons.Default.AccountBalanceWallet,
                iconColor = PylotoColors.Gold
            )

            // ── Card principal: Ganhos ────────────────────────
            EarningsSummaryCard(
                totalBruto = totalBruto,
                totalLiquido = totalLiquido
            )

            // ── Métricas ──────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Corridas",
                    value = totalCorridas.toString(),
                    icon = Icons.Default.DeliveryDining,
                    iconColor = PylotoColors.MilitaryGreen,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Média/corrida",
                    value = "R$ %.2f".format(mediaValorCorrida),
                    icon = Icons.Default.TrendingUp,
                    iconColor = PylotoColors.TechBlue,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Informativo
            Text(
                text = "Para detalhes completos, acesse a tela de Ganhos.",
                style = MaterialTheme.typography.bodySmall,
                color = PylotoColors.TextSecondary
            )
        }
    }
}

/**
 * Card resumo de ganhos brutos e líquidos.
 */
@Composable
private fun EarningsSummaryCard(
    totalBruto: Double,
    totalLiquido: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PylotoColors.Parchment),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ── Ganhos brutos ──────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowUpward,
                        contentDescription = null,
                        tint = PylotoColors.MilitaryGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(
                            text = "Ganhos Brutos",
                            style = MaterialTheme.typography.labelMedium,
                            color = PylotoColors.TextSecondary
                        )
                        Text(
                            text = "Este mês",
                            style = MaterialTheme.typography.labelSmall,
                            color = PylotoColors.TextDisabled
                        )
                    }
                }
                Text(
                    text = "R$ %.2f".format(totalBruto),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = PylotoColors.MilitaryGreen
                )
            }

            HorizontalDivider(color = PylotoColors.OutlineVariant)

            // ── Ganhos líquidos ─────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDownward,
                        contentDescription = null,
                        tint = PylotoColors.Gold,
                        modifier = Modifier.size(20.dp)
                    )
                    Column {
                        Text(
                            text = "Ganhos Líquidos",
                            style = MaterialTheme.typography.labelMedium,
                            color = PylotoColors.TextSecondary
                        )
                        Text(
                            text = "Após descontos",
                            style = MaterialTheme.typography.labelSmall,
                            color = PylotoColors.TextDisabled
                        )
                    }
                }
                Text(
                    text = "R$ %.2f".format(totalLiquido),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = PylotoColors.Gold
                )
            }
        }
    }
}

/**
 * Card de métrica compacto (corridas, média, etc).
 */
@Composable
private fun MetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = iconColor.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(22.dp)
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = PylotoColors.Black
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = PylotoColors.TextSecondary
            )
        }
    }
}

// ════════════════════════════════════════════════════════════════
// PREVIEW
// ════════════════════════════════════════════════════════════════

@Preview(showBackground = true)
@Composable
private fun FinanceiroSheetContentPreview() {
    PylotoTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SheetHeader(
                title = "Financeiro",
                icon = Icons.Default.AccountBalanceWallet,
                iconColor = PylotoColors.Gold
            )
            EarningsSummaryCard(
                totalBruto = 3250.00,
                totalLiquido = 2847.50
            )
        }
    }
}
