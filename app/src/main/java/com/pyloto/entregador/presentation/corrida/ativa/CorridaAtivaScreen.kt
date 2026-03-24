package com.pyloto.entregador.presentation.corrida.ativa

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.pyloto.entregador.core.location.LocationService
import com.pyloto.entregador.domain.model.Corrida

private data class RouteStep(
    val title: String,
    val description: String,
    val actionLabel: String
)

private val steps = listOf(
    RouteStep(
        title = "Iniciar rota",
        description = "Ativa a rota de coleta e inicia o rastreamento operacional.",
        actionLabel = "Iniciar rota de coleta"
    ),
    RouteStep(
        title = "Chegada na coleta",
        description = "Registra a chegada do parceiro no ponto de coleta.",
        actionLabel = "Registrar chegada na coleta"
    ),
    RouteStep(
        title = "Confirmar coleta",
        description = "Confirma que o item foi coletado e fica pronto para entrega.",
        actionLabel = "Confirmar coleta"
    ),
    RouteStep(
        title = "Rota de entrega",
        description = "Inicia o deslocamento ao destino mantendo o tracking ativo.",
        actionLabel = "Iniciar rota de entrega"
    ),
    RouteStep(
        title = "Chegada no destino",
        description = "Registra a chegada do parceiro no ponto de entrega.",
        actionLabel = "Registrar chegada no destino"
    ),
    RouteStep(
        title = "Finalizacao",
        description = "Envia a confirmacao final e encerra a rota ativa.",
        actionLabel = "Finalizar entrega"
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CorridaAtivaScreen(
    corridaId: String,
    onCorridaFinalizada: () -> Unit,
    onChatClick: (String) -> Unit,
    viewModel: CorridaAtivaViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(corridaId) {
        viewModel.loadCorrida(corridaId)
    }

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                CorridaAtivaEffect.StartTracking -> startTrackingService(context)
                CorridaAtivaEffect.StopTracking -> stopTrackingService(context)
                CorridaAtivaEffect.Finished -> onCorridaFinalizada()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Corrida em andamento")
                        Text(corridaId, style = MaterialTheme.typography.labelMedium)
                    }
                },
                actions = {
                    IconButton(onClick = { onChatClick(corridaId) }) {
                        Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = "Chat")
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.padding(horizontal = 24.dp))
                }
            }

            uiState.corrida == null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = uiState.erro ?: "Nao foi possivel carregar a corrida.",
                        color = MaterialTheme.colorScheme.error
                    )
                    Button(onClick = { viewModel.loadCorrida(corridaId) }) {
                        Text("Tentar novamente")
                    }
                }
            }

            else -> {
                val corrida = uiState.corrida ?: return@Scaffold
                CorridaAtivaContent(
                    corrida = corrida,
                    currentStep = uiState.currentStep,
                    isSubmitting = uiState.isSubmitting,
                    erro = uiState.erro,
                    fotoComprovanteUrl = uiState.fotoComprovanteUrl,
                    onChatClick = { onChatClick(corridaId) },
                    onFotoComprovanteChange = viewModel::updateFotoComprovanteUrl,
                    onPrimaryAction = {
                        when (uiState.currentStep) {
                            0 -> viewModel.iniciarRotaColeta(corridaId)
                            1 -> viewModel.registrarChegadaColeta(corridaId)
                            2 -> viewModel.confirmarColeta(corridaId)
                            3 -> viewModel.iniciarRotaEntrega(corridaId)
                            4 -> viewModel.registrarChegadaDestino(corridaId)
                            else -> viewModel.finalizarEntrega(corridaId)
                        }
                    },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun CorridaAtivaContent(
    corrida: Corrida,
    currentStep: Int,
    isSubmitting: Boolean,
    erro: String?,
    fotoComprovanteUrl: String,
    onChatClick: () -> Unit,
    onFotoComprovanteChange: (String) -> Unit,
    onPrimaryAction: () -> Unit,
    modifier: Modifier = Modifier
) {
    val step = steps[currentStep.coerceIn(0, steps.lastIndex)]

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SummaryCard(corrida = corrida)
        RouteChecklist(currentStep = currentStep)
        ActionCard(
            step = step,
            currentStep = currentStep,
            isSubmitting = isSubmitting,
            fotoComprovanteUrl = fotoComprovanteUrl,
            onFotoComprovanteChange = onFotoComprovanteChange,
            onPrimaryAction = onPrimaryAction,
            onChatClick = onChatClick
        )

        if (!erro.isNullOrBlank()) {
            Text(
                text = erro,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun SummaryCard(corrida: Corrida) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(corrida.cliente.nome, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text("Origem: ${corrida.origem.enderecoFormatado}")
            Text("Destino: ${corrida.destino.enderecoFormatado}")
            Text("Status atual: ${corrida.status.name}")
            Text("SLA: ${corrida.slaResumo ?: "Rastreamento operacional ativo"}")
            Text("Distancia estimada: ${corrida.distanciaKm} km")
            Text("Tempo estimado: ${corrida.tempoEstimadoMin} min")
        }
    }
}

@Composable
private fun RouteChecklist(currentStep: Int) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("Roteiro operacional", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            steps.forEachIndexed { index, step ->
                val prefix = when {
                    index < currentStep -> "Concluido"
                    index == currentStep -> "Atual"
                    else -> "Pendente"
                }
                Text("$prefix - ${step.title}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun ActionCard(
    step: RouteStep,
    currentStep: Int,
    isSubmitting: Boolean,
    fotoComprovanteUrl: String,
    onFotoComprovanteChange: (String) -> Unit,
    onPrimaryAction: () -> Unit,
    onChatClick: () -> Unit
) {
    Card {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(step.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(step.description, style = MaterialTheme.typography.bodyMedium)
                }
                Icon(Icons.Default.Navigation, contentDescription = null)
            }

            if (currentStep >= steps.lastIndex) {
                OutlinedTextField(
                    value = fotoComprovanteUrl,
                    onValueChange = onFotoComprovanteChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("URL da foto de comprovante") },
                    placeholder = { Text("https://...") }
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = onPrimaryAction,
                    enabled = !isSubmitting
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.height(18.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(step.actionLabel)
                    }
                }

                Button(onClick = onChatClick, enabled = !isSubmitting) {
                    Text("Abrir chat")
                }
            }
        }
    }
}

private fun startTrackingService(context: Context) {
    val intent = Intent(context, LocationService::class.java).apply {
        action = LocationService.ACTION_START
    }
    ContextCompat.startForegroundService(context, intent)
}

private fun stopTrackingService(context: Context) {
    val intent = Intent(context, LocationService::class.java).apply {
        action = LocationService.ACTION_STOP
    }
    context.startService(intent)
}
