package com.pyloto.entregador.presentation.ganhos.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pyloto.entregador.presentation.theme.PylotoColors
import com.pyloto.entregador.presentation.theme.PylotoTheme

/**
 * Card principal de saldo — estilo extrato bancário.
 * Exibe ganhos brutos, descontos e líquido com destaque.
 */
@Composable
fun SaldoCard(
    totalBruto: Double,
    totalLiquido: Double,
    totalCorridas: Int,
    mediaValorCorrida: Double,
    modifier: Modifier = Modifier
) {
    val descontos = totalBruto - totalLiquido

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            PylotoColors.MilitaryGreen,
                            PylotoColors.GreenDark
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ── Saldo líquido (destaque) ─────────────────
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Saldo Líquido",
                        style = MaterialTheme.typography.labelMedium,
                        color = PylotoColors.GoldLight
                    )
                    Text(
                        text = "R$ %.2f".format(totalLiquido),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                HorizontalDivider(color = Color.White.copy(alpha = 0.2f))

                // ── Linha: Bruto | Descontos ──────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SaldoLineItem(
                        label = "Ganhos brutos",
                        value = "R$ %.2f".format(totalBruto),
                        icon = Icons.Default.ArrowUpward,
                        iconColor = PylotoColors.GoldLight
                    )
                    SaldoLineItem(
                        label = "Descontos/Taxas",
                        value = "- R$ %.2f".format(descontos),
                        icon = Icons.Default.ArrowDownward,
                        iconColor = PylotoColors.ErrorLight
                    )
                }

                HorizontalDivider(color = Color.White.copy(alpha = 0.2f))

                // ── Linha: Corridas | Média ──────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SaldoLineItem(
                        label = "Corridas",
                        value = totalCorridas.toString(),
                        icon = Icons.Default.TrendingUp,
                        iconColor = PylotoColors.GoldLight
                    )
                    SaldoLineItem(
                        label = "Média/corrida",
                        value = "R$ %.2f".format(mediaValorCorrida),
                        icon = Icons.Default.TrendingUp,
                        iconColor = PylotoColors.GoldLight
                    )
                }
            }
        }
    }
}

@Composable
private fun SaldoLineItem(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(16.dp)
        )
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.7f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// PREVIEW
// ═══════════════════════════════════════════════════════════════

@Preview(showBackground = true)
@Composable
private fun SaldoCardPreview() {
    PylotoTheme {
        SaldoCard(
            totalBruto = 847.50,
            totalLiquido = 742.30,
            totalCorridas = 38,
            mediaValorCorrida = 22.30,
            modifier = Modifier.padding(16.dp)
        )
    }
}
