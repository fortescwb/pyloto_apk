package com.pyloto.entregador.presentation.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pyloto.entregador.domain.model.RemetenteTipo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    corridaId: String,
    onNavigateBack: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(corridaId) {
        viewModel.initialize(corridaId)
    }

    LaunchedEffect(uiState.mensagens.size) {
        if (uiState.mensagens.isNotEmpty()) {
            viewModel.markAsRead()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Chat da corrida")
                        Text(corridaId, style = MaterialTheme.typography.labelMedium)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Nao lidas: ${uiState.unreadCount}",
                style = MaterialTheme.typography.labelLarge
            )

            if (uiState.isLoading && uiState.mensagens.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.mensagens, key = { it.id }) { mensagem ->
                        val sender = when (mensagem.remetenteTipo) {
                            RemetenteTipo.ENTREGADOR -> "Voce"
                            RemetenteTipo.CLIENTE -> "Cliente"
                            RemetenteTipo.SISTEMA -> "Sistema"
                        }
                        Column {
                            Text(
                                text = sender,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = mensagem.conteudo,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }

            if (!uiState.erro.isNullOrBlank()) {
                Text(
                    text = uiState.erro ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = uiState.draft,
                    onValueChange = viewModel::updateDraft,
                    modifier = Modifier.weight(1f),
                    label = { Text("Mensagem") },
                    placeholder = { Text("Digite sua mensagem") }
                )
                Button(
                    onClick = viewModel::enviarMensagem,
                    enabled = !uiState.isSending && uiState.draft.isNotBlank()
                ) {
                    Text(if (uiState.isSending) "..." else "Enviar")
                }
            }

            Spacer(modifier = Modifier.height(2.dp))
        }
    }
}
