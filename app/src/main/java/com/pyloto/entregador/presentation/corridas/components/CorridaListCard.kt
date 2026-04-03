package com.pyloto.entregador.presentation.corridas.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pyloto.entregador.domain.model.Cliente
import com.pyloto.entregador.domain.model.Corrida
import com.pyloto.entregador.domain.model.CorridaStatus
import com.pyloto.entregador.domain.model.CorridaTimestamps
import com.pyloto.entregador.domain.model.Endereco
import com.pyloto.entregador.domain.model.EnderecoMasking
import com.pyloto.entregador.presentation.corridas.CorridaComDistancia
import com.pyloto.entregador.presentation.theme.PylotoColors
import com.pyloto.entregador.presentation.theme.PylotoTheme
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale

/**
 * Card rico para a tela de Corridas (modo Lista).
 *
 * Exibe:
 * - Badge de prioridade (se aplicável)
 * - Distância até a coleta em destaque
 * - Valor da corrida com fundo dourado
 * - Rota: Origem → Destino com ícones circulares e linha conectora
 * - Detalhes: distância total, tempo estimado, quantidade de itens
 *
 * @param corridaComDistancia wrapper com a corrida e distância até a coleta
 * @param onClick clique no card (abre detalhes)
 */
@Composable
fun CorridaListCard(
    corridaComDistancia: CorridaComDistancia,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val corrida = corridaComDistancia.corrida
    val currencyFormatter = remember {
        NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        onClick = { onClick(corrida.id) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Linha 1: Valor + distância até coleta + métricas ──
            TopRow(
                valor = corrida.valor.toDouble(),
                distanciaAteColeta = corridaComDistancia.distanciaAteColetaFormatada,
                tempoTotalMin = corridaComDistancia.tempoTotalMin,
                ganhoPorKm = corridaComDistancia.ganhoPorKm,
                isPrioridade = corrida.prioridade,
                currencyFormatter = currencyFormatter
            )

            HorizontalDivider(color = PylotoColors.OutlineVariant, thickness = 1.dp)

            // ── Linha 2: Rota visual (origem → destino) ─────────
            val aceita = EnderecoMasking.isCorridaAceita(corrida.status)
            RouteSection(
                origemNome = if (aceita) corrida.cliente.nome else "Coleta",
                origemEndereco = EnderecoMasking.exibirEndereco(corrida.origem, aceita),
                destinoEndereco = EnderecoMasking.exibirEndereco(corrida.destino, aceita)
            )

            HorizontalDivider(color = PylotoColors.OutlineVariant, thickness = 1.dp)

            // ── Linha 3: Detalhes do percurso (distância, tempo, itens) ─────
            DetailsRow(
                distanciaPercurso = corridaComDistancia.distanciaPercursoKm,
                tempoPercurso = corrida.tempoEstimadoMin,
                itens = corrida.itens
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// COMPOSABLES PRIVADOS
// ═══════════════════════════════════════════════════════════════

/**
 * Linha superior: Valor + distância até coleta à esquerda, tempo e ganho/km à direita.
 */
@Composable
private fun TopRow(
    valor: Double,
     distanciaAteColeta: String,
    tempoTotalMin: Int,
    ganhoPorKm: Double,
    isPrioridade: Boolean,
    currencyFormatter: NumberFormat
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // ── Valor + distância até coleta ──
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(PylotoColors.Gold, PylotoColors.GoldDark)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = currencyFormatter.format(valor),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Text(
                text = distanciaAteColeta,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = PylotoColors.TextPrimary
            )

            // ── Badge prioridade ──
            if (isPrioridade) {
                Box(
                    modifier = Modifier
                        .background(
                            color = PylotoColors.Gold.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PriorityHigh,
                            contentDescription = null,
                            tint = PylotoColors.Gold,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "PRIORIDADE",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = PylotoColors.Gold
                        )
                    }
                }
            }
        }

        // ── Métricas resumidas: tempo total + ganho/km ──
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Timer,
                    contentDescription = null,
                    tint = PylotoColors.TextSecondary,
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = "${tempoTotalMin}min total",
                    style = MaterialTheme.typography.labelSmall,
                    color = PylotoColors.TextSecondary
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AttachMoney,
                    contentDescription = null,
                    tint = PylotoColors.TextSecondary,
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = "R$ %.2f/km".format(ganhoPorKm),
                    style = MaterialTheme.typography.labelSmall,
                    color = PylotoColors.TextSecondary
                )
            }
        }
    }
}

/**
 * Seção visual de rota: Origem → Destino com linha conectora.
 */
@Composable
private fun RouteSection(
    origemNome: String,
    origemEndereco: String,
    destinoEndereco: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ── Ícones + Linha conectora ──
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxHeight()
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(PylotoColors.MilitaryGreen, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Store,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }

            // Linha conectora vertical
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .weight(1f)
                    .background(PylotoColors.OutlineVariant)
            )

            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(PylotoColors.TechBlue, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // ── Textos ──
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxHeight()
        ) {
            Column {
                Text(
                    text = origemNome,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = PylotoColors.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = origemEndereco,
                    style = MaterialTheme.typography.bodySmall,
                    color = PylotoColors.TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column {
                Text(
                    text = "Entrega",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = PylotoColors.Black
                )
                Text(
                    text = destinoEndereco,
                    style = MaterialTheme.typography.bodySmall,
                    color = PylotoColors.TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * Linha de detalhes: distância do percurso, tempo do percurso, itens.
 */
@Composable
private fun DetailsRow(
    distanciaPercurso: Double,
    tempoPercurso: Int,
    itens: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        DetailChip(
            icon = Icons.Default.Route,
            label = "%.1f km".format(distanciaPercurso),
            color = PylotoColors.MilitaryGreen
        )
        DetailChip(
            icon = Icons.Default.AccessTime,
            label = "${tempoPercurso}min",
            color = PylotoColors.TechBlue
        )
        DetailChip(
            icon = Icons.Default.Inventory2,
            label = "$itens ${if (itens == 1) "item" else "itens"}",
            color = PylotoColors.Sepia
        )
    }
}

/**
 * Chip compacto com ícone e label.
 */
@Composable
private fun DetailChip(
    icon: ImageVector,
    label: String,
    color: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            color = color
        )
    }
}

// ═══════════════════════════════════════════════════════════════
// PREVIEW
// ═══════════════════════════════════════════════════════════════

@Preview(showBackground = true)
@Composable
private fun CorridaListCardPreview() {
    PylotoTheme {
        val mockCorrida = Corrida(
            id = "001",
            cliente = Cliente("Restaurante Sabor & Arte", "(42) 3025-1122"),
            origem = Endereco(
                "Rua Balduíno Taques", "300", null,
                "Centro", "Ponta Grossa", "84010-050",
                -25.0945, -50.1633
            ),
            destino = Endereco(
                "Rua XV de Novembro", "850", null,
                "Centro", "Ponta Grossa", "84010-020",
                -25.0960, -50.1600
            ),
            valor = BigDecimal("12.50"),
            distanciaKm = 1.2,
            tempoEstimadoMin = 8,
            status = CorridaStatus.DISPONIVEL,
            timestamps = CorridaTimestamps(criadaEm = System.currentTimeMillis()),
            prioridade = true,
            itens = 2
        )

        CorridaListCard(
            corridaComDistancia = CorridaComDistancia(mockCorrida, 0.65),
            onClick = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}
