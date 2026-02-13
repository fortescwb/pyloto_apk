package com.pyloto.entregador.presentation.corrida.disponivel

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CorridaDetalhesScreen(
    onNavigateBack: () -> Unit,
    onCorridaAceita: () -> Unit
) {
    // TODO: Implementar tela de detalhes da corrida com mapa
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Detalhes da Corrida",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Tela em construção - mapa, rota, valor, cliente")
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onCorridaAceita) {
            Text("Aceitar Corrida")
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = onNavigateBack) {
            Text("Voltar")
        }
    }
}
