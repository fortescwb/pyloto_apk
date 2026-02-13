package com.pyloto.entregador.presentation.perfil

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PerfilScreen(
    onNavigateBack: () -> Unit,
    onGanhosClick: () -> Unit,
    onLogout: () -> Unit
) {
    // TODO: Implementar tela de perfil completa
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Meu Perfil",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Tela em construção - foto, dados, veículo, rating")
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onGanhosClick) {
            Text("Ver Ganhos")
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedButton(onClick = onLogout) {
            Text("Sair")
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = onNavigateBack) {
            Text("Voltar")
        }
    }
}
