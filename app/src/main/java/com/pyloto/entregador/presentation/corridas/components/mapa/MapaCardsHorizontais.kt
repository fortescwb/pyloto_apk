package com.pyloto.entregador.presentation.corridas.components.mapa

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pyloto.entregador.presentation.corridas.CorridaComDistancia
import com.pyloto.entregador.presentation.theme.PylotoColors
import java.text.NumberFormat

/**
 * Fila horizontal de cards compactos embaixo do mapa.
 * Cada card mostra bairro, distância até coleta, distância total e valor.
 */
@Composable
fun MapaCardsHorizontais(
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
            MapaCardCompacto(
                bairro = item.corrida.origem.bairro,
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
 * Mostra apenas o bairro da coleta, não o endereço completo.
 */
@Composable
private fun MapaCardCompacto(
    bairro: String,
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

            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Coleta · $bairro",
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
