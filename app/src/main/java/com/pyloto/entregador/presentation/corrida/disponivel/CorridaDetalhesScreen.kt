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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pyloto.entregador.domain.model.Corrida
import com.pyloto.entregador.domain.model.Endereco
import com.pyloto.entregador.presentation.theme.PylotoColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CorridaDetalhesScreen(
    corridaId: String,
    onNavigateBack: () -> Unit,
    onCorridaAceita: () -> Unit,
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
                    isAceitando = uiState.isAceitando,
                    onAceitar = { viewModel.aceitarCorrida(corridaId) },
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
    isAceitando: Boolean,
    onAceitar: () -> Unit,
    onVoltar: () -> Unit,
    modifier: Modifier = Modifier
) {
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

        // ── Cliente ────────────────────────────────────────
        InfoCard(title = "Cliente") {
            InfoRow(icon = Icons.Default.Person, text = corrida.cliente.nome)
            InfoRow(icon = Icons.Default.Phone, text = corrida.cliente.telefone)
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
            enabled = !isAceitando,
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

        TextButton(
            onClick = onVoltar,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Recusar", color = PylotoColors.TextSecondary)
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
