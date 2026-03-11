package com.pyloto.entregador.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pyloto.entregador.presentation.home.HomeLocation
import com.pyloto.entregador.presentation.theme.PylotoColors
import com.pyloto.entregador.presentation.theme.PylotoTheme

/**
 * Mapa compacto integrado ao scroll da home.
 *
 * Exibe um placeholder de mapa (200dp) com:
 * - Indicação de pedidos próximos
 * - Botão para expandir para fullscreen
 *
 * TODO: Integrar com Google Maps Compose quando o módulo
 * de mapa estiver configurado para a nova home.
 * Por ora, exibe apenas o scaffold visual.
 *
 * @param location localização atual do entregador
 * @param availableOrders quantidade de pedidos próximos
 * @param onExpand callback para abrir mapa em fullscreen
 */
@Composable
fun CompactMapSection(
    location: HomeLocation,
    availableOrders: Int,
    onExpand: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Placeholder do mapa — será substituído por GoogleMap Compose
            // quando a integração estiver pronta
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(PylotoColors.MilitaryGreen.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Map,
                        contentDescription = null,
                        tint = PylotoColors.MilitaryGreen,
                        modifier = Modifier.size(48.dp)
                    )
                    Text(
                        text = "Mapa de Entregas",
                        style = MaterialTheme.typography.bodyLarge,
                        color = PylotoColors.Black
                    )
                    Text(
                        text = "$availableOrders pedidos próximos",
                        style = MaterialTheme.typography.bodySmall,
                        color = PylotoColors.TextSecondary
                    )
                }
            }

            // Botão de expandir (canto superior direito)
            IconButton(
                onClick = onExpand,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Surface(
                    color = Color.White,
                    shape = MaterialTheme.shapes.small,
                    shadowElevation = 4.dp
                ) {
                    Icon(
                        imageVector = Icons.Default.Fullscreen,
                        contentDescription = "Expandir mapa",
                        tint = PylotoColors.MilitaryGreen,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════
// PREVIEW
// ════════════════════════════════════════════════════════════════

@Preview(showBackground = true)
@Composable
private fun CompactMapSectionPreview() {
    PylotoTheme {
        Surface(
            color = PylotoColors.Parchment,
            modifier = Modifier.fillMaxSize()
        ) {
            CompactMapSection(
                location = HomeLocation(-25.095, -50.1773),
                availableOrders = 3,
                onExpand = {},
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
