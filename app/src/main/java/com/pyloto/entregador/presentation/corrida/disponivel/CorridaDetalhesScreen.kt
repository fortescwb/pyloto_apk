package com.pyloto.entregador.presentation.corrida.disponivel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pyloto.entregador.domain.model.CapacityCheck
import com.pyloto.entregador.domain.model.Corrida
import com.pyloto.entregador.domain.model.Endereco
import com.pyloto.entregador.presentation.theme.PylotoColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CorridaDetalhesScreen(
    corridaId: String,
    onNavigateBack: () -> Unit,
    onCorridaAceita: () -> Unit,
    onCorridaRecusada: () -> Unit,
    viewModel: CorridaDetalhesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(corridaId) {
        viewModel.loadCorrida(corridaId)
    }

    LaunchedEffect(uiState.aceitaComSucesso) {
        if (uiState.aceitaComSucesso) onCorridaAceita()
    }

    LaunchedEffect(uiState.recusaComSucesso) {
        if (uiState.recusaComSucesso) onCorridaRecusada()
    }

    LaunchedEffect(uiState.erro) {
        uiState.erro?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.limparErro()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalhes da Corrida", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PylotoColors.MilitaryGreen
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = PylotoColors.Parchment
    ) { innerPadding ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PylotoColors.MilitaryGreen)
                }
            }

            uiState.corrida == null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Corrida não encontrada",
                            style = MaterialTheme.typography.bodyLarge,
                            color = PylotoColors.TextSecondary
                        )
                        TextButton(onClick = onNavigateBack) {
                            Text("Voltar")
                        }
                    }
                }
            }

            else -> {
                CorridaDetalhesContent(
                    corrida = uiState.corrida!!,
                    capacityCheck = uiState.capacityCheck,
                    isAceitando = uiState.isAceitando,
                    isRecusando = uiState.isRecusando,
                    onAceitar = { viewModel.aceitarCorrida(corridaId) },
                    onRecusar = { categoria, motivo ->
                        viewModel.recusarCorrida(corridaId, categoria, motivo)
                    },
                    onVoltar = onNavigateBack,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}

@Composable
private fun CorridaDetalhesContent(
    corrida: Corrida,
    capacityCheck: CapacityCheck?,
    isAceitando: Boolean,
    isRecusando: Boolean,
    onAceitar: () -> Unit,
    onRecusar: (String, String) -> Unit,
    onVoltar: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showRecusalDialog by rememberSaveable { mutableStateOf(false) }
    var refusalCategory by rememberSaveable { mutableStateOf(REFUSAL_CATEGORY_OPTIONS.first().key) }
    var refusalReason by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // ── Resumo financeiro ──────────────────────────────
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = PylotoColors.MilitaryGreen),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "R$ %.2f".format(corrida.valor.toDouble()),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "${corrida.distanciaKm} km · ~${corrida.tempoEstimadoMin} min",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
                if (corrida.prioridade) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFFFD600)
                    ) {
                        Text(
                            text = "PRIORITÁRIO",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        capacityCheck?.let { check ->
            CapacityCheckCard(check = check)
        }

        // ── Cliente ────────────────────────────────────────
        SlaInfoCard(corrida = corrida)

        InfoCard(title = "Cliente") {
            InfoRow(icon = Icons.Default.Person, text = corrida.cliente.nome)
            if (corrida.cliente.telefone.isNotBlank()) {
                InfoRow(icon = Icons.Default.Phone, text = corrida.cliente.telefone)
            }
            Text(
                text = "Contato do solicitante minimizado. Use apenas os canais oficiais da Pyloto.",
                style = MaterialTheme.typography.bodySmall,
                color = PylotoColors.TextSecondary
            )
        }

        // ── Endereços ──────────────────────────────────────
        InfoCard(title = "Coleta") {
            EnderecoInfo(endereco = corrida.origem)
        }

        InfoCard(title = "Entrega") {
            EnderecoInfo(endereco = corrida.destino)
        }

        // ── Ações ──────────────────────────────────────────
        Spacer(modifier = Modifier.height(4.dp))

        Button(
            onClick = onAceitar,
            enabled = !isAceitando && !isRecusando && (capacityCheck?.fits != false),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = PylotoColors.MilitaryGreen
            )
        ) {
            if (isAceitando) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Aceitar Corrida",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        OutlinedButton(
            onClick = { showRecusalDialog = true },
            enabled = !isAceitando && !isRecusando,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = PylotoColors.MilitaryGreen
            )
        ) {
            if (isRecusando) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = PylotoColors.MilitaryGreen,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Recusar corrida",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        TextButton(
            onClick = onVoltar,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Voltar", color = PylotoColors.TextSecondary)
        }

        if (showRecusalDialog) {
            RefusalDialog(
                selectedCategory = refusalCategory,
                reason = refusalReason,
                isSubmitting = isRecusando,
                onCategoryChange = { refusalCategory = it },
                onReasonChange = { refusalReason = it },
                onDismiss = {
                    if (!isRecusando) {
                        showRecusalDialog = false
                    }
                },
                onConfirm = {
                    onRecusar(refusalCategory, refusalReason)
                }
            )
        }
    }
}

@Composable
private fun RefusalDialog(
    selectedCategory: String,
    reason: String,
    isSubmitting: Boolean,
    onCategoryChange: (String) -> Unit,
    onReasonChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Recusar corrida",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Selecione a categoria da recusa. Recusas por capacidade, indisponibilidade, problema tecnico, seguranca/area de risco e SLA incompativel sao tratadas como justificaveis.",
                    style = MaterialTheme.typography.bodySmall,
                    color = PylotoColors.TextSecondary
                )
                REFUSAL_CATEGORY_OPTIONS.forEach { option ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RadioButton(
                            selected = selectedCategory == option.key,
                            onClick = { onCategoryChange(option.key) }
                        )
                        Column {
                            Text(
                                text = option.label,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                            if (option.supportingText != null) {
                                Text(
                                    text = option.supportingText,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = PylotoColors.TextSecondary
                                )
                            }
                        }
                    }
                }
                OutlinedTextField(
                    value = reason,
                    onValueChange = onReasonChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Motivo detalhado") },
                    minLines = 3,
                    enabled = !isSubmitting
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !isSubmitting && reason.trim().length >= 3
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                } else {
                    Text("Confirmar recusa")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isSubmitting) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun CapacityCheckCard(check: CapacityCheck) {
    val isBlocked = !check.fits
    val containerColor = if (isBlocked) Color(0xFFFFF1F0) else Color(0xFFFFF7E5)
    val contentColor = if (isBlocked) Color(0xFFB42318) else Color(0xFFB54708)

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = if (isBlocked) "Aceite bloqueado" else "Impacto na capacidade",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
            Text(
                text = check.reason.ifBlank {
                    "Volume ${check.orderDemand.volumeLitros.toInt()}L • Peso ${check.orderDemand.pesoKg.toInt()}kg • Valor R$ %.2f".format(check.orderDemand.valorReais)
                },
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor
            )
            Text(
                text = "Remanescente após aceite: ${check.projectedRemaining.volumeLitros.toInt()}L • ${check.projectedRemaining.pesoKg.toInt()}kg • R$ %.2f".format(check.projectedRemaining.valorReais),
                style = MaterialTheme.typography.bodySmall,
                color = PylotoColors.TextSecondary
            )
        }
    }
}

@Composable
private fun SlaInfoCard(corrida: Corrida) {
    val isPriority = corrida.modalidade == "prioridade"
    val containerColor = when (corrida.slaStatus) {
        "breached" -> Color(0xFFFFF1F0)
        "attention" -> Color(0xFFFFF7E5)
        else -> Color(0xFFF7F6F2)
    }
    val contentColor = when (corrida.slaStatus) {
        "breached" -> Color(0xFFB42318)
        "attention" -> Color(0xFFB54708)
        else -> PylotoColors.TextPrimary
    }

    InfoCard(title = "Modalidade e SLA") {
        Text(
            text = if (isPriority) "Prioridade" else "Comum",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = contentColor,
            modifier = Modifier
                .fillMaxWidth()
                .background(containerColor, RoundedCornerShape(10.dp))
                .padding(horizontal = 12.dp, vertical = 10.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = corrida.slaResumo ?: "SLA operacional indisponivel no momento.",
            style = MaterialTheme.typography.bodyMedium,
            color = PylotoColors.TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Coleta limite: ${formatDeadline(corrida.coletaDeadlineEm)}",
            style = MaterialTheme.typography.bodySmall,
            color = PylotoColors.TextSecondary
        )
        Text(
            text = "Entrega limite: ${formatDeadline(corrida.entregaDeadlineEm)}",
            style = MaterialTheme.typography.bodySmall,
            color = PylotoColors.TextSecondary
        )
        if (corrida.processamentoDiaSeguinte) {
            Text(
                text = "Pedido comum remanejado para a janela do proximo dia por ter sido criado apos 18h.",
                style = MaterialTheme.typography.bodySmall,
                color = PylotoColors.TextSecondary,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        if (corrida.slaAlertas.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            corrida.slaAlertas.forEach { alert ->
                Text(
                    text = "- $alert",
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor
                )
            }
        }
    }
}

@Composable
private fun InfoCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = PylotoColors.TextSecondary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            content()
        }
    }
}

@Composable
private fun InfoRow(icon: ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PylotoColors.MilitaryGreen,
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = PylotoColors.TextPrimary
        )
    }
}

@Composable
private fun EnderecoInfo(endereco: Endereco) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = null,
            tint = PylotoColors.MilitaryGreen,
            modifier = Modifier.size(18.dp).padding(top = 2.dp)
        )
        Column {
            Text(
                text = endereco.enderecoFormatado,
                style = MaterialTheme.typography.bodyMedium,
                color = PylotoColors.TextPrimary
            )
            Text(
                text = "${endereco.cidade} · ${endereco.cep}",
                style = MaterialTheme.typography.bodySmall,
                color = PylotoColors.TextSecondary
            )
        }
    }
}

private fun formatDeadline(timestamp: Long?): String {
    if (timestamp == null) {
        return "sera definido no proximo marco operacional"
    }
    val formatter = SimpleDateFormat("dd/MM HH:mm", Locale("pt", "BR"))
    return formatter.format(Date(timestamp))
}

private data class RefusalCategoryOption(
    val key: String,
    val label: String,
    val supportingText: String? = null
)

private val REFUSAL_CATEGORY_OPTIONS = listOf(
    RefusalCategoryOption(
        key = "capacidade",
        label = "Capacidade",
        supportingText = "Quando volume, peso ou valor nao cabem na sua operacao."
    ),
    RefusalCategoryOption(
        key = "indisponibilidade",
        label = "Indisponibilidade",
        supportingText = "Quando voce nao consegue assumir a corrida naquele momento."
    ),
    RefusalCategoryOption(
        key = "problema_tecnico",
        label = "Problema tecnico",
        supportingText = "Falha no app, aparelho ou conectividade."
    ),
    RefusalCategoryOption(
        key = "seguranca_area_risco",
        label = "Seguranca / area de risco",
        supportingText = "Situações com risco operacional ou pessoal."
    ),
    RefusalCategoryOption(
        key = "sla_incompativel",
        label = "SLA incompativel",
        supportingText = "Prazo inviavel para atendimento seguro."
    ),
    RefusalCategoryOption(
        key = "valor_baixo",
        label = "Valor baixo",
        supportingText = "Pode impactar sua prioridade futura se ocorrer de forma reiterada."
    ),
    RefusalCategoryOption(
        key = "sem_interesse",
        label = "Sem interesse",
        supportingText = "Pode impactar sua prioridade futura se ocorrer de forma reiterada."
    ),
    RefusalCategoryOption(
        key = "sem_motivo",
        label = "Sem motivo",
        supportingText = "Pode impactar sua prioridade futura se ocorrer de forma reiterada."
    ),
    RefusalCategoryOption(
        key = "outro",
        label = "Outro",
        supportingText = "Explique o motivo com detalhes."
    )
)
