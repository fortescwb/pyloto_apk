package com.pyloto.entregador.presentation.perfil.components

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
 * Bottom Sheet de Dados Pessoais.
 *
 * Campos editáveis: Nome, Telefone.
 * Campos somente leitura (bloqueados): CPF, Email.
 *
 * @param nome nome atual
 * @param email email (não editável)
 * @param telefone telefone atual
 * @param cpf CPF (não editável)
 * @param isSaving indica se está salvando
 * @param onSave callback com (nome, telefone) atualizados
 * @param onDismiss fecha o sheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DadosPessoaisSheet(
    nome: String,
    email: String,
    telefone: String,
    cpf: String,
    isSaving: Boolean,
    onSave: (nome: String, telefone: String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var editNome by remember(nome) { mutableStateOf(nome) }
    var editTelefone by remember(telefone) { mutableStateOf(telefone) }

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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Título
            SheetHeader(
                title = "Dados Pessoais",
                icon = Icons.Default.Person,
                iconColor = PylotoColors.MilitaryGreen
            )

            Spacer(modifier = Modifier.height(4.dp))

            // ── Campos editáveis ─────────────────────────────
            PylotoTextField(
                value = editNome,
                onValueChange = { editNome = it },
                label = "Nome completo",
                leadingIcon = Icons.Default.Edit,
                enabled = true
            )

            PylotoTextField(
                value = editTelefone,
                onValueChange = { editTelefone = it },
                label = "Telefone",
                leadingIcon = Icons.Default.Phone,
                enabled = true
            )

            HorizontalDivider(color = PylotoColors.OutlineVariant)

            // ── Campos somente leitura ───────────────────────
            Text(
                text = "Informações não editáveis",
                style = MaterialTheme.typography.labelMedium,
                color = PylotoColors.TextSecondary,
                fontWeight = FontWeight.Medium
            )

            PylotoTextField(
                value = email,
                onValueChange = {},
                label = "Email",
                leadingIcon = Icons.Default.Email,
                enabled = false,
                trailingIcon = Icons.Default.Lock
            )

            PylotoTextField(
                value = cpf,
                onValueChange = {},
                label = "CPF",
                leadingIcon = Icons.Default.Badge,
                enabled = false,
                trailingIcon = Icons.Default.Lock
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ── Botão Salvar ─────────────────────────────────
            Button(
                onClick = { onSave(editNome, editTelefone) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = !isSaving && editNome.isNotBlank() && editTelefone.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PylotoColors.MilitaryGreen,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (isSaving) "Salvando..." else "Salvar Alterações",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

/**
 * Header reutilizável para os bottom sheets do perfil.
 */
@Composable
internal fun SheetHeader(
    title: String,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(28.dp)
        )
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = PylotoColors.Black
        )
    }
}

/**
 * Campo de texto estilizado do Pyloto, reutilizado em todos os sheets.
 */
@Composable
internal fun PylotoTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    trailingIcon: ImageVector? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = if (enabled) PylotoColors.MilitaryGreen else PylotoColors.TextDisabled,
                modifier = Modifier.size(20.dp)
            )
        },
        trailingIcon = if (trailingIcon != null) {
            {
                Icon(
                    imageVector = trailingIcon,
                    contentDescription = "Não editável",
                    tint = PylotoColors.TextDisabled,
                    modifier = Modifier.size(18.dp)
                )
            }
        } else null,
        enabled = enabled,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PylotoColors.MilitaryGreen,
            unfocusedBorderColor = PylotoColors.OutlineVariant,
            disabledBorderColor = PylotoColors.OutlineVariant,
            disabledTextColor = PylotoColors.TextSecondary,
            disabledLabelColor = PylotoColors.TextDisabled,
            cursorColor = PylotoColors.MilitaryGreen,
            focusedLabelColor = PylotoColors.MilitaryGreen
        ),
        singleLine = true
    )
}

// ════════════════════════════════════════════════════════════════
// PREVIEW
// ════════════════════════════════════════════════════════════════

@Preview(showBackground = true)
@Composable
private fun DadosPessoaisSheetContentPreview() {
    PylotoTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SheetHeader(
                title = "Dados Pessoais",
                icon = Icons.Default.Person,
                iconColor = PylotoColors.MilitaryGreen
            )
            PylotoTextField(
                value = "João da Silva",
                onValueChange = {},
                label = "Nome completo",
                leadingIcon = Icons.Default.Edit,
                enabled = true
            )
            PylotoTextField(
                value = "joao@email.com",
                onValueChange = {},
                label = "Email",
                leadingIcon = Icons.Default.Email,
                enabled = false,
                trailingIcon = Icons.Default.Lock
            )
        }
    }
}
