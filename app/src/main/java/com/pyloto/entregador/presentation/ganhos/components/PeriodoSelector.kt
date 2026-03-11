package com.pyloto.entregador.presentation.ganhos.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pyloto.entregador.presentation.ganhos.PeriodoGanhos
import com.pyloto.entregador.presentation.theme.PylotoColors
import com.pyloto.entregador.presentation.theme.PylotoTheme

/**
 * Chips de seleção do período (Hoje / Semana / Mês / Total).
 */
@Composable
fun PeriodoSelector(
    selecionado: PeriodoGanhos,
    onSelecionar: (PeriodoGanhos) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        PeriodoGanhos.entries.forEachIndexed { index, periodo ->
            if (index > 0) Spacer(modifier = Modifier.width(8.dp))

            val isSelected = selecionado == periodo

            FilterChip(
                selected = isSelected,
                onClick = { onSelecionar(periodo) },
                label = {
                    Text(
                        text = periodo.label,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = PylotoColors.MilitaryGreen,
                    selectedLabelColor = Color.White,
                    containerColor = PylotoColors.Parchment,
                    labelColor = PylotoColors.TextSecondary
                ),
                border = FilterChipDefaults.filterChipBorder(
                    borderColor = PylotoColors.OutlineVariant,
                    selectedBorderColor = PylotoColors.MilitaryGreen,
                    enabled = true,
                    selected = isSelected
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PeriodoSelectorPreview() {
    PylotoTheme {
        PeriodoSelector(
            selecionado = PeriodoGanhos.SEMANA,
            onSelecionar = {}
        )
    }
}
