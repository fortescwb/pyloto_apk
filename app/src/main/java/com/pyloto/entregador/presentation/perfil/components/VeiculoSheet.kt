package com.pyloto.entregador.presentation.perfil.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pyloto.entregador.domain.model.VeiculoTipo
import com.pyloto.entregador.presentation.theme.PylotoColors
import com.pyloto.entregador.presentation.theme.PylotoTheme

/**
 * Bottom Sheet de Veículo.
 *
 * Permite ao entregador visualizar e editar informações sobre seu veículo:
 * tipo (Moto, Bicicleta, Carro) e placa.
 *
 * @param tipoAtual tipo de veículo atual
 * @param placaAtual placa atual (pode ser null para bicicleta)
 * @param isSaving indica se está salvando
 * @param onSave callback com (tipo, placa) atualizados
 * @param onDismiss fecha o sheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VeiculoSheet(
    tipoAtual: VeiculoTipo?,
    placaAtual: String?,
    isSaving: Boolean,
    onSave: (tipo: VeiculoTipo, placa: String?) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var selectedTipo by remember(tipoAtual) {
        mutableStateOf(tipoAtual ?: VeiculoTipo.MOTO)
    }
    var editPlaca by remember(placaAtual) {
        mutableStateOf(placaAtual ?: "")
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Título
            SheetHeader(
                title = "Veículo",
                icon = Icons.Default.TwoWheeler,
                iconColor = PylotoColors.Sepia
            )

            // ── Seletor de Tipo ───────────────────────────────
            Text(
                text = "Tipo de veículo",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = PylotoColors.Black
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                VeiculoTipoCard(
                    tipo = VeiculoTipo.MOTO,
                    label = "Moto",
                    icon = Icons.Default.TwoWheeler,
                    isSelected = selectedTipo == VeiculoTipo.MOTO,
                    onClick = { selectedTipo = VeiculoTipo.MOTO },
                    modifier = Modifier.weight(1f)
                )
                VeiculoTipoCard(
                    tipo = VeiculoTipo.BICICLETA,
                    label = "Bicicleta",
                    icon = Icons.Default.DirectionsBike,
                    isSelected = selectedTipo == VeiculoTipo.BICICLETA,
                    onClick = { selectedTipo = VeiculoTipo.BICICLETA },
                    modifier = Modifier.weight(1f)
                )
                VeiculoTipoCard(
                    tipo = VeiculoTipo.CARRO,
                    label = "Carro",
                    icon = Icons.Default.DirectionsCar,
                    isSelected = selectedTipo == VeiculoTipo.CARRO,
                    onClick = { selectedTipo = VeiculoTipo.CARRO },
                    modifier = Modifier.weight(1f)
                )
            }

            // ── Placa ─────────────────────────────────────────
            if (selectedTipo != VeiculoTipo.BICICLETA) {
                PylotoTextField(
                    value = editPlaca,
                    onValueChange = { editPlaca = it.uppercase() },
                    label = "Placa do veículo",
                    leadingIcon = Icons.Default.Edit,
                    enabled = true
                )
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = PylotoColors.TechBlue.copy(alpha = 0.08f)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        text = "Bicicletas não necessitam de placa.",
                        style = MaterialTheme.typography.bodySmall,
                        color = PylotoColors.TechBlue,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // ── Botão Salvar ──────────────────────────────────
            Button(
                onClick = {
                    val placa = if (selectedTipo == VeiculoTipo.BICICLETA) null else editPlaca.ifBlank { null }
                    onSave(selectedTipo, placa)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = !isSaving && (selectedTipo == VeiculoTipo.BICICLETA || editPlaca.isNotBlank()),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PylotoColors.Sepia,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (isSaving) "Salvando..." else "Salvar Veículo",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

/**
 * Card de seleção de tipo de veículo com visual premium.
 */
@Composable
private fun VeiculoTipoCard(
    tipo: VeiculoTipo,
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) PylotoColors.Sepia else PylotoColors.OutlineVariant
    val backgroundColor = if (isSelected) PylotoColors.Sepia.copy(alpha = 0.08f) else Color.White
    val iconColor = if (isSelected) PylotoColors.Sepia else PylotoColors.TextSecondary
    val textColor = if (isSelected) PylotoColors.Sepia else PylotoColors.TextSecondary

    Card(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 2.dp else 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(contentAlignment = Alignment.TopEnd) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = iconColor,
                    modifier = Modifier.size(32.dp)
                )
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .background(
                                color = PylotoColors.Sepia,
                                shape = RoundedCornerShape(7.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(10.dp)
                        )
                    }
                }
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = textColor
            )
        }
    }
}

// ════════════════════════════════════════════════════════════════
// PREVIEW
// ════════════════════════════════════════════════════════════════

@Preview(showBackground = true)
@Composable
private fun VeiculoSheetContentPreview() {
    PylotoTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SheetHeader(
                title = "Veículo",
                icon = Icons.Default.TwoWheeler,
                iconColor = PylotoColors.Sepia
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                VeiculoTipoCard(
                    tipo = VeiculoTipo.MOTO,
                    label = "Moto",
                    icon = Icons.Default.TwoWheeler,
                    isSelected = true,
                    onClick = {},
                    modifier = Modifier.weight(1f)
                )
                VeiculoTipoCard(
                    tipo = VeiculoTipo.BICICLETA,
                    label = "Bicicleta",
                    icon = Icons.Default.DirectionsBike,
                    isSelected = false,
                    onClick = {},
                    modifier = Modifier.weight(1f)
                )
                VeiculoTipoCard(
                    tipo = VeiculoTipo.CARRO,
                    label = "Carro",
                    icon = Icons.Default.DirectionsCar,
                    isSelected = false,
                    onClick = {},
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
