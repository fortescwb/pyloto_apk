package com.pyloto.entregador.presentation.perfil.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pyloto.entregador.presentation.theme.PylotoColors
import com.pyloto.entregador.presentation.theme.PylotoTheme

/**
 * Bottom Sheet de Meta Semanal.
 *
 * Permite ao entregador definir sua meta semanal de ganhos
 * através de um slider intuitivo. Exibe a meta atual e permite
 * ajustar de R$ 500 a R$ 5000.
 *
 * @param metaAtual meta semanal atual (Double)
 * @param onSave callback com a nova meta
 * @param onDismiss fecha o sheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MetaSemanalSheet(
    metaAtual: Double,
    onSave: (Double) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val minMeta = 500f
    val maxMeta = 5000f
    var sliderValue by remember(metaAtual) {
        mutableFloatStateOf(metaAtual.toFloat().coerceIn(minMeta, maxMeta))
    }

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
                title = "Meta Semanal",
                icon = Icons.Default.EmojiEvents,
                iconColor = PylotoColors.TechBlue
            )

            // ── Card com valor da meta ────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PylotoColors.Parchment),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Sua meta semanal",
                        style = MaterialTheme.typography.labelMedium,
                        color = PylotoColors.TextSecondary
                    )
                    Text(
                        text = "R$ %.0f".format(sliderValue),
                        style = MaterialTheme.typography.displaySmall,
                        fontWeight = FontWeight.Bold,
                        color = PylotoColors.Gold
                    )
                    Text(
                        text = getMotivationalMessage(sliderValue),
                        style = MaterialTheme.typography.bodySmall,
                        color = PylotoColors.MilitaryGreen,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                }
            }

            // ── Slider ───────────────────────────────────────
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Slider(
                    value = sliderValue,
                    onValueChange = { sliderValue = it },
                    valueRange = minMeta..maxMeta,
                    steps = 8, // 500 em 500
                    modifier = Modifier.fillMaxWidth(),
                    colors = SliderDefaults.colors(
                        thumbColor = PylotoColors.Gold,
                        activeTrackColor = PylotoColors.Gold,
                        inactiveTrackColor = PylotoColors.OutlineVariant,
                        activeTickColor = PylotoColors.GoldDark,
                        inactiveTickColor = PylotoColors.OutlineVariant
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "R$ ${minMeta.toInt()}",
                        style = MaterialTheme.typography.labelSmall,
                        color = PylotoColors.TextDisabled
                    )
                    Text(
                        text = "R$ ${maxMeta.toInt()}",
                        style = MaterialTheme.typography.labelSmall,
                        color = PylotoColors.TextDisabled
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // ── Botão Salvar ──────────────────────────────────
            Button(
                onClick = { onSave(sliderValue.toDouble()) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PylotoColors.TechBlue,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Definir Meta",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

/**
 * Mensagem motivacional baseada no valor da meta.
 */
private fun getMotivationalMessage(meta: Float): String {
    return when {
        meta <= 1000f -> "Comece com passos firmes! 💪"
        meta <= 2000f -> "Uma boa meta para a semana!"
        meta <= 3000f -> "Ambicioso! Você consegue! 🚀"
        meta <= 4000f -> "Meta de campeão! 🏆"
        else -> "Rumo ao topo! Dedicação máxima! ⭐"
    }
}

// ════════════════════════════════════════════════════════════════
// PREVIEW
// ════════════════════════════════════════════════════════════════

@Preview(showBackground = true)
@Composable
private fun MetaSemanalSheetContentPreview() {
    PylotoTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SheetHeader(
                title = "Meta Semanal",
                icon = Icons.Default.EmojiEvents,
                iconColor = PylotoColors.TechBlue
            )
            Text(
                text = "R$ 1500",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = PylotoColors.Gold
            )
        }
    }
}
