package com.pyloto.entregador.presentation.perfil.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pyloto.entregador.presentation.theme.PylotoColors
import com.pyloto.entregador.presentation.theme.PylotoTheme

/**
 * Seção de menu do perfil com itens clicáveis.
 *
 * Cada item tem ícone colorido (circle), label, descrição e seta.
 * Segue o mesmo padrão visual dos cards da Home (branco, borda,
 * radius 16dp, elevation 4dp).
 *
 * @param onDadosPessoaisClick abre sheet de dados pessoais
 * @param onFinanceiroClick abre sheet financeiro
 * @param onMetaSemanalClick abre sheet de meta semanal
 * @param onVeiculoClick abre sheet de veículo
 * @param onLogout executa logout
 */
@Composable
fun ProfileMenuSection(
    onDadosPessoaisClick: () -> Unit,
    onFinanceiroClick: () -> Unit,
    onMetaSemanalClick: () -> Unit,
    onVeiculoClick: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Configurações",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = PylotoColors.Black
        )

        // Card com menu items agrupados
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column {
                ProfileMenuItem(
                    icon = Icons.Default.Person,
                    iconColor = PylotoColors.MilitaryGreen,
                    title = "Dados Pessoais",
                    subtitle = "Nome, telefone, documentos",
                    onClick = onDadosPessoaisClick
                )

                MenuDivider()

                ProfileMenuItem(
                    icon = Icons.Default.AccountBalanceWallet,
                    iconColor = PylotoColors.Gold,
                    title = "Financeiro",
                    subtitle = "Ganhos, saldo, extrato",
                    onClick = onFinanceiroClick
                )

                MenuDivider()

                ProfileMenuItem(
                    icon = Icons.Default.EmojiEvents,
                    iconColor = PylotoColors.TechBlue,
                    title = "Meta Semanal",
                    subtitle = "Configure sua meta de ganhos",
                    onClick = onMetaSemanalClick
                )

                MenuDivider()

                ProfileMenuItem(
                    icon = Icons.Default.DirectionsCar,
                    iconColor = PylotoColors.Sepia,
                    title = "Veículo",
                    subtitle = "Dados do veículo cadastrado",
                    onClick = onVeiculoClick
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Botão Sair (destaque vermelho, separado do card)
        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PylotoColors.StatusRejected,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 2.dp,
                pressedElevation = 0.dp
            )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Sair da Conta",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/**
 * Item individual do menu de perfil.
 */
@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Ícone circular
            Surface(
                modifier = Modifier.size(42.dp),
                shape = RoundedCornerShape(12.dp),
                color = iconColor.copy(alpha = 0.12f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier
                        .padding(9.dp)
                        .size(24.dp)
                )
            }

            // Textos
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = PylotoColors.Black
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = PylotoColors.TextSecondary
                )
            }

            // Seta
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = PylotoColors.TextDisabled,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun MenuDivider() {
    HorizontalDivider(
        color = PylotoColors.OutlineVariant,
        thickness = 1.dp,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}

// ════════════════════════════════════════════════════════════════
// PREVIEW
// ════════════════════════════════════════════════════════════════

@Preview(showBackground = true)
@Composable
private fun ProfileMenuSectionPreview() {
    PylotoTheme {
        Surface(color = PylotoColors.Parchment) {
            ProfileMenuSection(
                onDadosPessoaisClick = {},
                onFinanceiroClick = {},
                onMetaSemanalClick = {},
                onVeiculoClick = {},
                onLogout = {},
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
