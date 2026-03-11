package com.pyloto.entregador.presentation.home.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pyloto.entregador.presentation.theme.PylotoColors
import com.pyloto.entregador.presentation.theme.PylotoTheme
import java.text.NumberFormat
import java.util.Locale

/**
 * Seção de estatísticas diárias do entregador.
 *
 * Grid 2x2 com cards animados mostrando:
 * - Ganhos do dia (destaque dourado com gradiente)
 * - Número de entregas realizadas
 * - Tempo online acumulado
 * - Tempo restante de jornada
 *
 * Todos os dados são scaffold/placeholder enquanto a
 * integração com o CORE não está implementada.
 */
@Composable
fun DailyStatsSection(
    earnings: Double,
    deliveries: Int,
    timeOnline: String,
    timeRemaining: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Hoje",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = PylotoColors.Black
        )

        // Grid 2x2 de estatísticas
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Linha 1: Ganhos + Entregas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                EarningsCard(
                    earnings = earnings,
                    modifier = Modifier.weight(1f)
                )
                DeliveriesCard(
                    deliveries = deliveries,
                    modifier = Modifier.weight(1f)
                )
            }

            // Linha 2: Tempo Online + Tempo Restante
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TimeOnlineCard(
                    timeOnline = timeOnline,
                    modifier = Modifier.weight(1f)
                )
                TimeRemainingCard(
                    timeRemaining = timeRemaining,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/**
 * Card de Ganhos — destaque máximo com gradiente dourado.
 */
@Composable
private fun EarningsCard(
    earnings: Double,
    modifier: Modifier = Modifier
) {
    val currencyFormatter = remember {
        NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    }

    StatCard(
        modifier = modifier,
        icon = Icons.Default.AttachMoney,
        label = "Ganhos",
        value = currencyFormatter.format(earnings),
        containerGradient = Brush.linearGradient(
            colors = listOf(
                PylotoColors.Gold,
                PylotoColors.Sepia
            )
        ),
        contentColor = Color.White,
        iconColor = Color.White
    )
}

/**
 * Card de Entregas realizadas.
 */
@Composable
private fun DeliveriesCard(
    deliveries: Int,
    modifier: Modifier = Modifier
) {
    StatCard(
        modifier = modifier,
        icon = Icons.Default.CheckCircle,
        label = "Entregas",
        value = deliveries.toString(),
        containerColor = Color.White,
        contentColor = PylotoColors.Black,
        iconColor = PylotoColors.MilitaryGreen,
        borderStroke = BorderStroke(
            width = 1.dp,
            color = PylotoColors.MilitaryGreen.copy(alpha = 0.2f)
        )
    )
}

/**
 * Card de Tempo Online acumulado.
 */
@Composable
private fun TimeOnlineCard(
    timeOnline: String,
    modifier: Modifier = Modifier
) {
    StatCard(
        modifier = modifier,
        icon = Icons.Default.Schedule,
        label = "Tempo Online",
        value = timeOnline,
        containerColor = Color.White,
        contentColor = PylotoColors.Black,
        iconColor = PylotoColors.TechBlue,
        borderStroke = BorderStroke(
            width = 1.dp,
            color = PylotoColors.TechBlue.copy(alpha = 0.2f)
        )
    )
}

/**
 * Card de Tempo Restante de jornada.
 */
@Composable
private fun TimeRemainingCard(
    timeRemaining: String,
    modifier: Modifier = Modifier
) {
    StatCard(
        modifier = modifier,
        icon = Icons.Default.Timer,
        label = "Tempo Restante",
        value = timeRemaining,
        containerColor = Color.White,
        contentColor = PylotoColors.Black,
        iconColor = PylotoColors.MilitaryGreen,
        borderStroke = BorderStroke(
            width = 1.dp,
            color = PylotoColors.MilitaryGreen.copy(alpha = 0.2f)
        )
    )
}

/**
 * Card genérico de estatística reutilizável.
 * Suporta cor sólida ou gradiente, borda opcional.
 */
@Composable
private fun StatCard(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    containerColor: Color? = null,
    containerGradient: Brush? = null,
    contentColor: Color = PylotoColors.Black,
    iconColor: Color = contentColor,
    borderStroke: BorderStroke? = null
) {
    // Animação de entrada (fade + slide)
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "card_alpha"
    )

    val offsetY by animateFloatAsState(
        targetValue = if (visible) 0f else 20f,
        animationSpec = tween(durationMillis = 500),
        label = "card_offset"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .offset(y = offsetY.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor ?: Color.Transparent
        ),
        border = borderStroke,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (containerGradient != null) {
                        Modifier.background(containerGradient)
                    } else {
                        Modifier
                    }
                )
                .padding(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Ícone + Label
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium,
                        color = contentColor.copy(alpha = 0.9f)
                    )
                }

                // Valor em destaque
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════
// PREVIEW
// ════════════════════════════════════════════════════════════════

@Preview(showBackground = true)
@Composable
private fun DailyStatsSectionPreview() {
    PylotoTheme {
        Surface(
            color = PylotoColors.Parchment,
            modifier = Modifier.fillMaxSize()
        ) {
            DailyStatsSection(
                earnings = 245.50,
                deliveries = 12,
                timeOnline = "5h 32m",
                timeRemaining = "4h 28m",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
