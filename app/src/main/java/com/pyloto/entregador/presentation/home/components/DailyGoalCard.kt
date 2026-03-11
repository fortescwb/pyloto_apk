package com.pyloto.entregador.presentation.home.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pyloto.entregador.presentation.theme.PylotoColors
import com.pyloto.entregador.presentation.theme.PylotoTheme
import java.text.NumberFormat
import java.util.Locale

/**
 * Card de Meta Diária com barra de progresso animada.
 *
 * Exibe:
 * - Meta configurável pelo entregador
 * - Barra de progresso com gradiente verde → dourado
 * - Mensagem motivacional (falta X / meta atingida!)
 * - Animação suave de preenchimento
 *
 * @param currentEarnings ganhos acumulados no dia
 * @param goalAmount meta diária em R$
 */
@Composable
fun DailyGoalCard(
    currentEarnings: Double,
    goalAmount: Double,
    modifier: Modifier = Modifier
) {
    val currencyFormatter = remember {
        NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    }

    // Cálculos
    val progressPercent = remember(currentEarnings, goalAmount) {
        if (goalAmount <= 0) 0f
        else ((currentEarnings / goalAmount) * 100).coerceIn(0.0, 100.0).toFloat()
    }

    val remainingAmount = remember(currentEarnings, goalAmount) {
        (goalAmount - currentEarnings).coerceAtLeast(0.0)
    }

    val isGoalReached = currentEarnings >= goalAmount

    // Animação da barra de progresso
    var animationPlayed by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue = if (animationPlayed) progressPercent / 100f else 0f,
        animationSpec = tween(
            durationMillis = 1000,
            easing = LinearEasing
        ),
        label = "progress_animation"
    )

    LaunchedEffect(Unit) {
        animationPlayed = true
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header: Ícone + Título + Valor da Meta
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Meta",
                        tint = if (isGoalReached) PylotoColors.Gold else PylotoColors.MilitaryGreen,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Meta do Dia",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = PylotoColors.Black
                    )
                }
                Text(
                    text = currencyFormatter.format(goalAmount),
                    style = MaterialTheme.typography.bodyLarge,
                    color = PylotoColors.TextSecondary
                )
            }

            // Barra de Progresso
            GoalProgressBar(
                progress = animatedProgress,
                isGoalReached = isGoalReached
            )

            // Mensagem de status
            GoalStatusMessage(
                isGoalReached = isGoalReached,
                remainingAmount = remainingAmount,
                progressPercent = progressPercent.toInt(),
                currencyFormatter = currencyFormatter
            )
        }
    }
}

@Composable
private fun GoalProgressBar(
    progress: Float,
    isGoalReached: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(12.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFFE0E0E0))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .fillMaxHeight()
                .clip(RoundedCornerShape(6.dp))
                .background(
                    brush = if (isGoalReached) {
                        Brush.linearGradient(
                            colors = listOf(
                                PylotoColors.Gold,
                                PylotoColors.GoldLight
                            )
                        )
                    } else {
                        Brush.linearGradient(
                            colors = listOf(
                                PylotoColors.MilitaryGreen,
                                PylotoColors.Gold
                            )
                        )
                    }
                )
        )
    }
}

@Composable
private fun GoalStatusMessage(
    isGoalReached: Boolean,
    remainingAmount: Double,
    progressPercent: Int,
    currencyFormatter: NumberFormat
) {
    if (isGoalReached) {
        // Meta atingida — celebração
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🎉",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Parabéns! Meta atingida!",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = PylotoColors.Gold
            )
        }
    } else {
        // Falta para atingir a meta
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = buildString {
                    append("Faltam ")
                    append(currencyFormatter.format(remainingAmount))
                    append(" para atingir sua meta")
                },
                style = MaterialTheme.typography.bodyMedium,
                color = PylotoColors.TextSecondary
            )

            // Indicador de progresso em texto
            Text(
                text = "$progressPercent% concluído",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = PylotoColors.MilitaryGreen
            )
        }
    }
}

/**
 * Versão compacta do DailyGoalCard — ideal para espaços reduzidos.
 * Exibe meta, progresso e status em layout horizontal.
 */
@Composable
fun CompactDailyGoalCard(
    currentEarnings: Double,
    goalAmount: Double,
    modifier: Modifier = Modifier
) {
    val currencyFormatter = remember {
        NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    }

    val progressPercent = if (goalAmount <= 0) 0f
    else ((currentEarnings / goalAmount) * 100).coerceIn(0.0, 100.0).toFloat()
    val isGoalReached = currentEarnings >= goalAmount

    var animationPlayed by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue = if (animationPlayed) progressPercent / 100f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "compact_progress"
    )

    LaunchedEffect(Unit) {
        animationPlayed = true
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                tint = if (isGoalReached) PylotoColors.Gold else PylotoColors.MilitaryGreen,
                modifier = Modifier.size(32.dp)
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Meta do Dia",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${progressPercent.toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = PylotoColors.MilitaryGreen
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color(0xFFE0E0E0))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedProgress)
                            .fillMaxHeight()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        PylotoColors.MilitaryGreen,
                                        PylotoColors.Gold
                                    )
                                )
                            )
                    )
                }

                Text(
                    text = if (isGoalReached) {
                        "🎉 Meta atingida!"
                    } else {
                        "${currencyFormatter.format(currentEarnings)} / ${currencyFormatter.format(goalAmount)}"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = PylotoColors.TextSecondary
                )
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════
// PREVIEWS
// ════════════════════════════════════════════════════════════════

@Preview(showBackground = true)
@Composable
private fun DailyGoalCardPreview_InProgress() {
    PylotoTheme {
        Surface(
            color = PylotoColors.Parchment,
            modifier = Modifier.fillMaxSize()
        ) {
            DailyGoalCard(
                currentEarnings = 245.50,
                goalAmount = 300.0,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DailyGoalCardPreview_Reached() {
    PylotoTheme {
        Surface(
            color = PylotoColors.Parchment,
            modifier = Modifier.fillMaxSize()
        ) {
            DailyGoalCard(
                currentEarnings = 320.0,
                goalAmount = 300.0,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CompactDailyGoalCardPreview() {
    PylotoTheme {
        Surface(
            color = PylotoColors.Parchment,
            modifier = Modifier.fillMaxSize()
        ) {
            CompactDailyGoalCard(
                currentEarnings = 180.0,
                goalAmount = 300.0,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
