package com.pyloto.entregador.presentation.ganhos.components

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pyloto.entregador.domain.model.CorridaStatus
import com.pyloto.entregador.presentation.ganhos.CorridaRealizada
import com.pyloto.entregador.presentation.theme.PylotoColors
import com.pyloto.entregador.presentation.theme.PylotoTheme

/**
 * Lista de movimentações (extrato) — corridas realizadas.
 * Estilo de extrato bancário com linhas de transação.
 */
@Composable
fun ExtratoCorridasSection(
    corridas: List<CorridaRealizada>,
    onCorridaClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // ── Título + contador ────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Extrato de Corridas",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = PylotoColors.Black
            )
            Text(
                text = "${corridas.size} registros",
                style = MaterialTheme.typography.labelSmall,
                color = PylotoColors.TextSecondary
            )
        }

        // ── Card do extrato ──────────────────────────────────
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                corridas.forEachIndexed { index, corrida ->
                    ExtratoItem(
                        corrida = corrida,
                        onClick = { onCorridaClick(corrida.id) }
                    )
                    if (index < corridas.lastIndex) {
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

/**
 * Linha individual do extrato — semelhante a transação bancária.
 */
@Composable
private fun ExtratoItem(
    corrida: CorridaRealizada,
    onClick: () -> Unit
) {
    val isCancelada = corrida.status == CorridaStatus.CANCELADA
    val valorColor = if (isCancelada) PylotoColors.StatusRejected else PylotoColors.MilitaryGreen
    val statusIcon = if (isCancelada) Icons.Default.Cancel else Icons.Default.CheckCircle
    val statusColor = if (isCancelada) PylotoColors.StatusRejected else PylotoColors.StatusApproved

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ── Ícone do estabelecimento ──
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = if (isCancelada)
                            PylotoColors.StatusRejected.copy(alpha = 0.1f)
                        else
                            PylotoColors.MilitaryGreen.copy(alpha = 0.1f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Store,
                    contentDescription = null,
                    tint = if (isCancelada) PylotoColors.StatusRejected else PylotoColors.MilitaryGreen,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // ── Detalhes ──
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = corrida.clienteNome,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = PylotoColors.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Icon(
                        imageVector = statusIcon,
                        contentDescription = null,
                        tint = statusColor,
                        modifier = Modifier.size(14.dp)
                    )
                }
                Text(
                    text = "${corrida.origemBairro} → ${corrida.destinoBairro} · ${corrida.distanciaKm}km · ${corrida.tempoMin}min",
                    style = MaterialTheme.typography.labelSmall,
                    color = PylotoColors.TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = corrida.dataHora,
                    style = MaterialTheme.typography.labelSmall,
                    color = PylotoColors.TextDisabled
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // ── Valor ──
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = if (isCancelada) "Cancelada" else "+ R$ %.2f".format(corrida.valor),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = valorColor
                )
                if (!isCancelada) {
                    Text(
                        text = "R$ %.2f/km".format(
                            if (corrida.distanciaKm > 0) corrida.valor / corrida.distanciaKm else 0.0
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        color = PylotoColors.TextDisabled
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = PylotoColors.TextDisabled,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// PREVIEW
// ═══════════════════════════════════════════════════════════════

@Preview(showBackground = true)
@Composable
private fun ExtratoCorridasSectionPreview() {
    PylotoTheme {
        ExtratoCorridasSection(
            corridas = listOf(
                CorridaRealizada(
                    id = "1", clienteNome = "Restaurante Sabor",
                    origemBairro = "Centro", destinoBairro = "Uvaranas",
                    valor = 18.50, distanciaKm = 3.2, tempoMin = 12,
                    dataHora = "16/02 · 14:32", status = CorridaStatus.FINALIZADA
                ),
                CorridaRealizada(
                    id = "2", clienteNome = "Padaria Grão",
                    origemBairro = "Ronda", destinoBairro = "Estrela",
                    valor = 12.00, distanciaKm = 1.8, tempoMin = 8,
                    dataHora = "16/02 · 13:10", status = CorridaStatus.FINALIZADA
                ),
                CorridaRealizada(
                    id = "3", clienteNome = "Supermercado Condor",
                    origemBairro = "Ronda", destinoBairro = "Uvaranas",
                    valor = 19.30, distanciaKm = 4.0, tempoMin = 15,
                    dataHora = "15/02 · 15:30", status = CorridaStatus.CANCELADA
                )
            ),
            onCorridaClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
