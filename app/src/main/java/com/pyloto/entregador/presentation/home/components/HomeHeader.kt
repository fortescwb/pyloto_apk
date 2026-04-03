package com.pyloto.entregador.presentation.home.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pyloto.entregador.presentation.theme.PylotoColors
import com.pyloto.entregador.presentation.theme.PylotoTheme

/**
 * Header premium da tela inicial com identidade Pyloto.
 *
 * - Logo "PYLOTO" em dourado sobre fundo verde militar
 * - Toggle Online/Offline animado
 * - Chip com localização atual do entregador
 *
 * @param isOnline estado atual online/offline
 * @param cidade cidade do entregador (ex: "Ponta Grossa, PR")
 * @param regiao bairro/região (ex: "Centro")
 * @param onToggleOnline callback ao clicar no toggle
 */
@Composable
fun HomeHeader(
    isOnline: Boolean,
    notificationsUnreadCount: Int,
    cidade: String,
    regiao: String,
    onNotificationsClick: () -> Unit,
    onToggleOnline: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = PylotoColors.MilitaryGreen,
        tonalElevation = 4.dp,
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            // Linha 1: Logo Pyloto + Toggle Online/Offline
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Logo Pyloto
                Column {
                    Text(
                        text = "PYLOTO",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = PylotoColors.Gold,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = "Entregador",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNotificationsClick) {
                        BadgedBox(
                            badge = {
                                if (notificationsUnreadCount > 0) {
                                    Badge {
                                        Text(
                                            text = notificationsUnreadCount.coerceAtMost(99).toString(),
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notificacoes",
                                tint = Color.White
                            )
                        }
                    }

                    OnlineToggleButton(
                        isOnline = isOnline,
                        onClick = onToggleOnline
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Linha 2: Localização Atual
            LocationChip(
                cidade = cidade,
                regiao = regiao
            )
        }
    }
}

@Composable
private fun OnlineToggleButton(
    isOnline: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isOnline) {
        PylotoColors.Gold
    } else {
        Color.Gray
    }

    val textColor = if (isOnline) {
        PylotoColors.Black
    } else {
        Color.White
    }

    // Animação suave de escala ao clicar
    val scale by animateFloatAsState(
        targetValue = 1f,
        label = "button_scale"
    )

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = textColor
        ),
        shape = RoundedCornerShape(24.dp),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 2.dp
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Indicador de status
            Text(
                text = if (isOnline) "●" else "○",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (isOnline) "Online" else "Offline",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun LocationChip(
    cidade: String,
    regiao: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.1f))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Navigation,
            contentDescription = "Localização",
            tint = PylotoColors.Gold,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = "$cidade • $regiao",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White
        )
    }
}

// ════════════════════════════════════════════════════════════════
// PREVIEWS
// ════════════════════════════════════════════════════════════════

@Preview(showBackground = true)
@Composable
private fun HomeHeaderPreview_Online() {
    PylotoTheme {
        HomeHeader(
            isOnline = true,
            notificationsUnreadCount = 3,
            cidade = "Ponta Grossa, PR",
            regiao = "Centro",
            onNotificationsClick = {},
            onToggleOnline = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeHeaderPreview_Offline() {
    PylotoTheme {
        HomeHeader(
            isOnline = false,
            notificationsUnreadCount = 0,
            cidade = "Ponta Grossa, PR",
            regiao = "Centro",
            onNotificationsClick = {},
            onToggleOnline = {}
        )
    }
}
