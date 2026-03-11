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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Route
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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

/**
 * Seção de indicadores de performance:
 * - Ganho por km rodado
 * - Ganho por hora online
 * - Total de km rodados
 * - Tempo online
 */
@Composable
fun IndicadoresSection(
    ganhoPorKm: Double,
    ganhoPorHora: Double,
    totalKm: Double,
    tempoOnlineMinutos: Int,
    modifier: Modifier = Modifier
) {
    val horas = tempoOnlineMinutos / 60
    val minutos = tempoOnlineMinutos % 60
    val tempoFormatado = "${horas}h ${minutos}m"

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Indicadores de Performance",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = PylotoColors.Black,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            IndicadorCard(
                label = "R$/km",
                value = "R$ %.2f".format(ganhoPorKm),
                icon = Icons.Default.Route,
                iconColor = PylotoColors.MilitaryGreen,
                modifier = Modifier.weight(1f)
            )
            IndicadorCard(
                label = "R$/hora",
                value = "R$ %.2f".format(ganhoPorHora),
                icon = Icons.Default.AttachMoney,
                iconColor = PylotoColors.Gold,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            IndicadorCard(
                label = "Km rodados",
                value = "%.1f km".format(totalKm),
                icon = Icons.Default.Route,
                iconColor = PylotoColors.TechBlue,
                modifier = Modifier.weight(1f)
            )
            IndicadorCard(
                label = "Tempo online",
                value = tempoFormatado,
                icon = Icons.Default.AccessTime,
                iconColor = PylotoColors.Sepia,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun IndicadorCard(
    label: String,
    value: String,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
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
                    modifier = Modifier.size(20.dp)
                )
            }
            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = PylotoColors.Black
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = PylotoColors.TextSecondary
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// PREVIEW
// ═══════════════════════════════════════════════════════════════

@Preview(showBackground = true)
@Composable
private fun IndicadoresSectionPreview() {
    PylotoTheme {
        IndicadoresSection(
            ganhoPorKm = 2.45,
            ganhoPorHora = 18.75,
            totalKm = 302.8,
            tempoOnlineMinutos = 480,
            modifier = Modifier.padding(16.dp)
        )
    }
}
