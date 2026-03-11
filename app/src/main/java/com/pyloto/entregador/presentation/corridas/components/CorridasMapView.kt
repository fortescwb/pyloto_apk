package com.pyloto.entregador.presentation.corridas.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pyloto.entregador.domain.model.Cliente
import com.pyloto.entregador.domain.model.Corrida
import com.pyloto.entregador.domain.model.CorridaStatus
import com.pyloto.entregador.domain.model.CorridaTimestamps
import com.pyloto.entregador.domain.model.Endereco
import com.pyloto.entregador.presentation.corridas.CorridaComDistancia
import com.pyloto.entregador.presentation.theme.PylotoColors
import com.pyloto.entregador.presentation.theme.PylotoTheme
import java.math.BigDecimal
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.cos

/**
 * Modo Mapa: Placeholder visual para o mapa com pins de coleta.
 *
 * O mapa real (Google Maps) será integrado futuramente.
 * Por ora exibe um placeholder estilizado com a localização do entregador
 * ao centro e pins representando os locais de coleta (localização
 * aproximada, raio de ~200m).
 *
 * @param corridasOrdenadas lista de corridas com distância
 * @param entregadorLat latitude do entregador
 * @param entregadorLng longitude do entregador
 * @param onCorridaClick callback ao tocar num pin/card
 */
@Composable
fun CorridasMapView(
    corridasOrdenadas: List<CorridaComDistancia>,
    entregadorLat: Double,
    entregadorLng: Double,
    onCorridaClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val currencyFormatter = remember {
        NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // ══════════════════════════════════════════════════════
        // PLACEHOLDER DO MAPA
        // ══════════════════════════════════════════════════════

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            PylotoColors.SurfaceBright,
                            PylotoColors.SurfaceDim,
                            PylotoColors.Parchment
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            // Grid pattern para simular mapa
            MapPlaceholder(
                corridasOrdenadas = corridasOrdenadas,
                entregadorLat = entregadorLat,
                entregadorLng = entregadorLng
            )
        }

        // ══════════════════════════════════════════════════════
        // LISTA RESUMIDA (scroll horizontal) embaixo do mapa
        // ══════════════════════════════════════════════════════

        if (corridasOrdenadas.isNotEmpty()) {
            MapBottomCards(
                corridasOrdenadas = corridasOrdenadas,
                currencyFormatter = currencyFormatter,
                onCorridaClick = onCorridaClick
            )
        }
    }
}

/**
 * Placeholder visual do mapa com pins posicionados.
 */
@Composable
private fun MapPlaceholder(
    corridasOrdenadas: List<CorridaComDistancia>,
    entregadorLat: Double,
    entregadorLng: Double
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // ── Informação do mapa ──
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Map,
                contentDescription = null,
                tint = PylotoColors.MilitaryGreen.copy(alpha = 0.3f),
                modifier = Modifier.size(80.dp)
            )
            Text(
                text = "Mapa de Coletas",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = PylotoColors.MilitaryGreen.copy(alpha = 0.6f)
            )
            Text(
                text = "Centralizado na sua localização",
                style = MaterialTheme.typography.bodySmall,
                color = PylotoColors.TextSecondary
            )

            // ── Marcador do entregador (centro) ──
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = PylotoColors.TechBlue,
                        shape = CircleShape
                    )
                    .border(
                        width = 3.dp,
                        color = Color.White,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = "Sua localização",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            // ── Legenda de pins ──
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(top = 8.dp)
            ) {
                LegendItem(
                    color = PylotoColors.TechBlue,
                    label = "Você"
                )
                LegendItem(
                    color = PylotoColors.MilitaryGreen,
                    label = "Coleta"
                )
                LegendItem(
                    color = PylotoColors.Gold,
                    label = "Prioridade"
                )
            }

            // ── Nota sobre raio aproximado ──
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = PylotoColors.TechBlue.copy(alpha = 0.08f)
                ),
                elevation = CardDefaults.cardElevation(0.dp),
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                Text(
                    text = "📍 Pins mostram localização aproximada (raio de 200m) dos pontos de coleta",
                    style = MaterialTheme.typography.labelSmall,
                    color = PylotoColors.TechBlue,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(12.dp)
                )
            }

            // ── Resumo das corridas ──
            Text(
                text = "${corridasOrdenadas.size} pontos de coleta no mapa",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = PylotoColors.TextSecondary
            )
        }

        // ── Pins simulados ao redor ──
        corridasOrdenadas.forEachIndexed { index, item ->
            val offsetAngle = (index * (360.0 / corridasOrdenadas.size.coerceAtLeast(1)))
            val radius = 100 + (index * 15)

            // Posiciona pins em círculo ao redor do centro
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(
                        start = (radius * cos(Math.toRadians(offsetAngle))).dp.coerceIn(0.dp, 120.dp),
                        top = (radius * kotlin.math.sin(Math.toRadians(offsetAngle))).dp.coerceIn(0.dp, 120.dp)
                    )
            ) {
                MapPin(
                    isPrioridade = item.corrida.prioridade,
                    distancia = item.distanciaAteColetaFormatada
                )
            }
        }
    }
}

/**
 * Pin estilizado para o mapa.
 */
@Composable
private fun MapPin(
    isPrioridade: Boolean,
    distancia: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(
                    color = if (isPrioridade) PylotoColors.Gold else PylotoColors.MilitaryGreen,
                    shape = CircleShape
                )
                .border(
                    width = 2.dp,
                    color = Color.White,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isPrioridade) {
                Icon(
                    imageVector = Icons.Default.PriorityHigh,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        Text(
            text = distancia,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = PylotoColors.TextPrimary
        )
    }
}

/**
 * Item de legenda do mapa.
 */
@Composable
private fun LegendItem(
    color: Color,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, CircleShape)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = PylotoColors.TextSecondary
        )
    }
}

/**
 * Cards compactos horizontais abaixo do mapa (scroll horizontal).
 */
@Composable
private fun MapBottomCards(
    corridasOrdenadas: List<CorridaComDistancia>,
    currencyFormatter: NumberFormat,
    onCorridaClick: (String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(
            horizontal = 16.dp,
            vertical = 8.dp
        ),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        itemsIndexed(
            items = corridasOrdenadas,
            key = { _, item -> item.corrida.id }
        ) { _, item ->
            MapCompactCard(
                nome = item.corrida.cliente.nome,
                distanciaAteColeta = item.distanciaAteColetaFormatada,
                valor = currencyFormatter.format(item.corrida.valor.toDouble()),
                distanciaTotal = "%.1f km".format(item.corrida.distanciaKm),
                isPrioridade = item.corrida.prioridade,
                onClick = { onCorridaClick(item.corrida.id) }
            )
        }
    }
}

/**
 * Card compacto para scroll horizontal no modo Mapa.
 */
@Composable
private fun MapCompactCard(
    nome: String,
    distanciaAteColeta: String,
    valor: String,
    distanciaTotal: String,
    isPrioridade: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .height(90.dp)
            .padding(vertical = 2.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // ── Ícone ──
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = if (isPrioridade)
                            PylotoColors.Gold.copy(alpha = 0.12f)
                        else
                            PylotoColors.MilitaryGreen.copy(alpha = 0.12f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = if (isPrioridade) PylotoColors.Gold else PylotoColors.MilitaryGreen,
                    modifier = Modifier.size(22.dp)
                )
            }

            // ── Texto ──
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = nome,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = PylotoColors.Black,
                    maxLines = 1
                )
                Text(
                    text = "$distanciaAteColeta até coleta · $distanciaTotal total",
                    style = MaterialTheme.typography.labelSmall,
                    color = PylotoColors.TextSecondary,
                    maxLines = 1
                )
            }

            // ── Valor ──
            Box(
                modifier = Modifier
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(PylotoColors.Gold, PylotoColors.GoldDark)
                        ),
                        shape = RoundedCornerShape(6.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = valor,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// PREVIEW
// ═══════════════════════════════════════════════════════════════

@Preview(showBackground = true, heightDp = 600)
@Composable
private fun CorridasMapViewPreview() {
    PylotoTheme {
        val mocks = listOf(
            CorridaComDistancia(
                corrida = Corrida(
                    id = "001",
                    cliente = Cliente("Restaurante Sabor", "(42) 3025-1122"),
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
                    timestamps = CorridaTimestamps(criadaEm = 0),
                    prioridade = true,
                    itens = 2
                ),
                distanciaAteColetaKm = 0.65
            ),
            CorridaComDistancia(
                corrida = Corrida(
                    id = "002",
                    cliente = Cliente("Padaria Grão", "(42) 3026-3344"),
                    origem = Endereco(
                        "Av. Taunay", "1500", null,
                        "Ronda", "Ponta Grossa", "84040-010",
                        -25.1050, -50.1750
                    ),
                    destino = Endereco(
                        "Rua Schamber", "245", null,
                        "Uvaranas", "Ponta Grossa", "84025-200",
                        -25.0870, -50.1520
                    ),
                    valor = BigDecimal("18.00"),
                    distanciaKm = 3.8,
                    tempoEstimadoMin = 15,
                    status = CorridaStatus.DISPONIVEL,
                    timestamps = CorridaTimestamps(criadaEm = 0),
                    itens = 1
                ),
                distanciaAteColetaKm = 2.3
            )
        )

        CorridasMapView(
            corridasOrdenadas = mocks,
            entregadorLat = -25.0940,
            entregadorLng = -50.1620,
            onCorridaClick = {}
        )
    }
}
