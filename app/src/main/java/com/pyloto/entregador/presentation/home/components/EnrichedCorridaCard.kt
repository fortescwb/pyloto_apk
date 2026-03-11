package com.pyloto.entregador.presentation.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pyloto.entregador.domain.model.Corrida
import com.pyloto.entregador.presentation.theme.PylotoColors
import com.pyloto.entregador.presentation.theme.PylotoTheme
import java.text.NumberFormat
import java.util.Locale

/**
 * Card enriquecido de corrida com visual premium.
 *
 * Features:
 * - Badge "Prioritário" quando aplicável
 * - Valor em destaque com fundo dourado
 * - Ícones circulares coloridos (verde=origem, azul=destino)
 * - Informações de distância, tempo e quantidade de itens
 * - Botões de ação: "Aceitar" (primário) e "Detalhes" (secundário)
 *
 * Os callbacks onAccept e onViewDetails estão preparados para
 * integração com o ViewModel/CORE quando disponível.
 *
 * @param corrida modelo de domínio da corrida
 * @param onAccept callback ao aceitar a corrida (recebe corridaId)
 * @param onViewDetails callback ao ver detalhes (recebe corridaId)
 */
@Composable
fun EnrichedCorridaCard(
    corrida: Corrida,
    onAccept: (String) -> Unit,
    onViewDetails: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val currencyFormatter = remember {
        NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
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
        Box {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Linha 1: Valor + Info Básica (distância e tempo)
                ValueAndInfoRow(
                    value = corrida.valor.toDouble(),
                    distance = corrida.distanciaKm,
                    estimatedTime = corrida.tempoEstimadoMin,
                    currencyFormatter = currencyFormatter
                )

                // Divider
                HorizontalDivider(
                    color = Color(0xFFE0E0E0),
                    thickness = 1.dp
                )

                // Linha 2: Origem (Coleta)
                LocationSection(
                    icon = Icons.Default.Store,
                    iconBackgroundColor = PylotoColors.MilitaryGreen,
                    title = corrida.cliente.nome,
                    address = corrida.origem.enderecoFormatado,
                    additionalInfo = "${corrida.itens} ${if (corrida.itens == 1) "item" else "itens"}"
                )

                // Divider
                HorizontalDivider(
                    color = Color(0xFFE0E0E0),
                    thickness = 1.dp
                )

                // Linha 3: Destino (Entrega)
                LocationSection(
                    icon = Icons.Default.LocationOn,
                    iconBackgroundColor = PylotoColors.TechBlue,
                    title = "Endereço de entrega",
                    address = corrida.destino.enderecoFormatado,
                    additionalInfo = null
                )

                // Botões de Ação
                ActionButtonsRow(
                    onAccept = { onAccept(corrida.id) },
                    onViewDetails = { onViewDetails(corrida.id) }
                )
            }

            // Badge de Prioridade (canto superior direito)
            if (corrida.prioridade) {
                PriorityBadge(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                )
            }
        }
    }
}

/**
 * Linha com valor em destaque + distância + tempo estimado.
 */
@Composable
private fun ValueAndInfoRow(
    value: Double,
    distance: Double,
    estimatedTime: Int,
    currencyFormatter: NumberFormat
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Valor em destaque (fundo dourado)
        Surface(
            color = PylotoColors.Gold,
            shape = RoundedCornerShape(12.dp),
            shadowElevation = 2.dp
        ) {
            Text(
                text = currencyFormatter.format(value),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = PylotoColors.Black,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        // Info secundária (distância e tempo)
        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Route,
                    contentDescription = null,
                    tint = PylotoColors.TextSecondary,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = String.format(Locale("pt", "BR"), "%.1f km", distance),
                    style = MaterialTheme.typography.bodyMedium,
                    color = PylotoColors.TextSecondary
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = PylotoColors.TextSecondary,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "$estimatedTime min",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PylotoColors.TextSecondary
                )
            }
        }
    }
}

/**
 * Seção de localização com ícone circular colorido.
 * Usada para origem (verde) e destino (azul).
 */
@Composable
private fun LocationSection(
    icon: ImageVector,
    iconBackgroundColor: Color,
    title: String,
    address: String,
    additionalInfo: String?
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Ícone circular
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(iconBackgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }

        // Informações
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = PylotoColors.Black
            )
            Text(
                text = address,
                style = MaterialTheme.typography.bodyMedium,
                color = PylotoColors.TextSecondary
            )
            if (additionalInfo != null) {
                Text(
                    text = additionalInfo,
                    style = MaterialTheme.typography.bodySmall,
                    color = PylotoColors.TechBlue,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

/**
 * Botões de ação: Aceitar (primário verde) e Detalhes (secundário).
 */
@Composable
private fun ActionButtonsRow(
    onAccept: () -> Unit,
    onViewDetails: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Botão Aceitar (primário)
        Button(
            onClick = onAccept,
            modifier = Modifier.weight(1f),
            colors = ButtonDefaults.buttonColors(
                containerColor = PylotoColors.MilitaryGreen,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            Text(
                text = "Aceitar",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        }

        // Botão Detalhes (secundário)
        OutlinedButton(
            onClick = onViewDetails,
            modifier = Modifier.wrapContentWidth(),
            border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
            shape = RoundedCornerShape(12.dp),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Text(
                text = "Detalhes",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = PylotoColors.TextPrimary
            )
        }
    }
}

/**
 * Badge de prioridade em dourado.
 */
@Composable
private fun PriorityBadge(
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = PylotoColors.Gold,
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.PriorityHigh,
                contentDescription = "Prioritário",
                tint = PylotoColors.Black,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = "Prioritário",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = PylotoColors.Black
            )
        }
    }
}

// ════════════════════════════════════════════════════════════════
// PREVIEW
// ════════════════════════════════════════════════════════════════

@Preview(showBackground = true)
@Composable
private fun EnrichedCorridaCardPreview() {
    PylotoTheme {
        Surface(
            color = PylotoColors.Parchment,
            modifier = Modifier.fillMaxSize()
        ) {
            // Dados mock para preview — serão substituídos por dados reais
            // quando a integração com o CORE estiver pronta
            Text(
                text = "Preview: EnrichedCorridaCard\n(requer instância de Corrida do domínio)",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
