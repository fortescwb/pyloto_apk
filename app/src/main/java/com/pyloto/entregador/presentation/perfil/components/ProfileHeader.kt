package com.pyloto.entregador.presentation.perfil.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pyloto.entregador.presentation.theme.PylotoColors
import com.pyloto.entregador.presentation.theme.PylotoTheme

/**
 * Header do perfil com foto, nome, rating e total de corridas.
 *
 * - Fundo verde militar com gradiente para consistência com HomeHeader
 * - Avatar circular com borda dourada (ou ícone placeholder)
 * - Botão de câmera para alterar foto
 * - Nome em branco, subtítulo "Entregador Pyloto" em dourado
 * - Rating com estrela dourada + total de corridas
 *
 * @param nome nome completo do entregador
 * @param fotoUrl URL da foto (null = avatar placeholder)
 * @param rating avaliação média (0.0–5.0)
 * @param totalCorridas número de corridas finalizadas
 * @param onEditPhoto callback ao clicar no botão de câmera
 */
@Composable
fun ProfileHeader(
    nome: String,
    fotoUrl: String?,
    rating: Double,
    totalCorridas: Int,
    onEditPhoto: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.Transparent,
        shadowElevation = 4.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            PylotoColors.MilitaryGreen,
                            PylotoColors.GreenDark
                        )
                    )
                )
                .padding(horizontal = 16.dp, vertical = 24.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // ── Avatar ───────────────────────────────────────
                Box(contentAlignment = Alignment.BottomEnd) {
                    // Foto ou placeholder
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(PylotoColors.Parchment)
                            .border(
                                width = 3.dp,
                                color = PylotoColors.Gold,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (fotoUrl != null) {
                            // TODO: Substituir por AsyncImage (Coil) quando
                            // a integração de imagens estiver pronta.
                            // AsyncImage(model = fotoUrl, contentDescription = ...)
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Foto do perfil",
                                tint = PylotoColors.MilitaryGreen,
                                modifier = Modifier.size(52.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Foto do perfil",
                                tint = PylotoColors.MilitaryGreen,
                                modifier = Modifier.size(52.dp)
                            )
                        }
                    }

                    // Botão de câmera
                    IconButton(
                        onClick = onEditPhoto,
                        modifier = Modifier.size(32.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = PylotoColors.Gold,
                            contentColor = PylotoColors.Black
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Alterar foto",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // ── Nome ─────────────────────────────────────────
                Text(
                    text = nome,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = "Entregador Pyloto",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PylotoColors.Gold,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                // ── Rating + Total Corridas ──────────────────────
                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Rating
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = PylotoColors.Gold,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = String.format("%.1f", rating),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    // Separador
                    Box(
                        modifier = Modifier
                            .size(width = 1.dp, height = 20.dp)
                            .background(Color.White.copy(alpha = 0.3f))
                    )

                    // Total de corridas
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = totalCorridas.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "corridas",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
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
private fun ProfileHeaderPreview() {
    PylotoTheme {
        ProfileHeader(
            nome = "João da Silva",
            fotoUrl = null,
            rating = 4.8,
            totalCorridas = 342,
            onEditPhoto = {}
        )
    }
}
