package com.pyloto.entregador.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun HomeScreen(
    onCorridaClick: (String) -> Unit,
    onPerfilClick: () -> Unit,
    onHistoricoClick: () -> Unit,
    onNotificacoesClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is HomeEvent.CorridaAceita -> {
                    // Navegar para corrida ativa
                }
            }
        }
    }

    Scaffold(
        topBar = {
            // TODO: Implementar TopBar com status online/offline
        },
        bottomBar = {
            // TODO: Implementar Bottom Navigation
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                is HomeUiState.Loading -> {
                    CircularProgressIndicator()
                }
                is HomeUiState.Empty -> {
                    Text(
                        text = "Nenhuma corrida disponível no momento",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                is HomeUiState.Success -> {
                    // TODO: Implementar lista de corridas com LazyColumn
                    Text("${state.corridas.size} corridas disponíveis")
                }
                is HomeUiState.Error -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = state.message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(onClick = { viewModel.loadCorridas() }) {
                            Text("Tentar novamente")
                        }
                    }
                }
            }
        }
    }
}
