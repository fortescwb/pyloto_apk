package com.pyloto.entregador.presentation.corridas.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pyloto.entregador.presentation.corridas.CorridaComDistancia
import com.pyloto.entregador.presentation.theme.PylotoColors

/**
 * Modo Lista: LazyColumn com CorridaListCards ordenados por distância.
 *
 * @param corridasOrdenadas lista de corridas com distância, já ordenadas
 * @param onCorridaClick callback ao clicar em uma corrida
 */
@Composable
fun CorridasListView(
    corridasOrdenadas: List<CorridaComDistancia>,
    onCorridaClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (corridasOrdenadas.isEmpty()) {
        EmptyCorridasState(modifier = modifier)
        return
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "${corridasOrdenadas.size} corridas disponíveis",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                color = PylotoColors.TextSecondary,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        itemsIndexed(
            items = corridasOrdenadas,
            key = { _, item -> item.corrida.id }
        ) { index, corridaComDistancia ->
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + slideInVertically(
                    initialOffsetY = { it / 2 }
                )
            ) {
                CorridaListCard(
                    corridaComDistancia = corridaComDistancia,
                    onClick = onCorridaClick
                )
            }
        }

        // Espaço extra no final para não ficar colando na bottom bar
        item { Spacer(modifier = Modifier.height(8.dp)) }
    }
}

/**
 * Estado vazio — sem corridas disponíveis.
 */
@Composable
private fun EmptyCorridasState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.SearchOff,
            contentDescription = null,
            tint = PylotoColors.TextDisabled,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Nenhuma corrida disponível",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = PylotoColors.TextPrimary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Não há entregas na sua região no momento.\nTente novamente em breve.",
            style = MaterialTheme.typography.bodyMedium,
            color = PylotoColors.TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}
