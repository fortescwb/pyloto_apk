package com.pyloto.entregador.presentation.corrida.ativa

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CorridaAtivaScreen(
    onCorridaFinalizada: () -> Unit,
    onChatClick: (String) -> Unit
) {
    // TODO: Implementar tela de corrida ativa com mapa em tempo real
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Corrida em Andamento",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Tela em construção - mapa, navegação, status steps")
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onCorridaFinalizada) {
            Text("Finalizar Corrida")
        }
    }
}
