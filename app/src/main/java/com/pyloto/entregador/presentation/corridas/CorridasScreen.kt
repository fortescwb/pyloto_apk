package com.pyloto.entregador.presentation.corridas

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pyloto.entregador.presentation.corridas.components.CorridasListView
import com.pyloto.entregador.presentation.corridas.components.CorridasMapView
import com.pyloto.entregador.presentation.theme.PylotoColors
import com.pyloto.entregador.presentation.theme.PylotoTheme

/**
 * Tela de Corridas Disponíveis.
 *
 * Exibe corridas em dois modos alternáveis:
 * - **Lista**: Cards ricos ordenados por distância até a coleta.
 * - **Mapa**: Mapa centralizado no entregador com pins de coleta (raio ~200m).
 *
 * @param onCorridaClick abre detalhes de uma corrida
 * @param onHomeClick navega para Home
 * @param onGanhosClick navega para Ganhos
 * @param onPerfilClick navega para Perfil
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CorridasScreen(
    onCorridaClick: (String) -> Unit,
    onHomeClick: () -> Unit,
    onGanhosClick: () -> Unit,
    onPerfilClick: () -> Unit,
    viewModel: CorridasViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // ── Snackbar de erro ────────────────────────────────────
    LaunchedEffect(uiState.erro) {
        uiState.erro?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.limparErro()
        }
    }

    Scaffold(
        topBar = {
            CorridasTopBar(
                viewMode = uiState.viewMode,
                onToggleViewMode = viewModel::toggleViewMode,
                onRefresh = viewModel::loadCorridas
            )
        },
        bottomBar = {
            CorridasBottomNavigation(
                onHomeClick = onHomeClick,
                onCorridasClick = { /* já está na tela */ },
                onGanhosClick = onGanhosClick,
                onPerfilClick = onPerfilClick
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = PylotoColors.Parchment
    ) { innerPadding ->

        if (uiState.isLoading) {
            LoadingState(modifier = Modifier.padding(innerPadding))
            return@Scaffold
        }

        // ── Animação de transição entre modos ───────────────
        AnimatedContent(
            targetState = uiState.viewMode,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "ViewModeTransition",
            modifier = Modifier.padding(innerPadding)
        ) { viewMode ->
            when (viewMode) {
                CorridasViewMode.LISTA -> {
                    CorridasListView(
                        corridasOrdenadas = uiState.corridasOrdenadas,
                        onCorridaClick = onCorridaClick
                    )
                }
                CorridasViewMode.MAPA -> {
                    CorridasMapView(
                        corridasOrdenadas = uiState.corridasOrdenadas,
                        entregadorLat = uiState.entregadorLat,
                        entregadorLng = uiState.entregadorLng,
                        onCorridaClick = onCorridaClick
                    )
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// TOP BAR COM TOGGLE
// ═══════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CorridasTopBar(
    viewMode: CorridasViewMode,
    onToggleViewMode: () -> Unit,
    onRefresh: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "Corridas",
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
        },
        actions = {
            // ── Botão Refresh ──
            IconButton(onClick = onRefresh) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Atualizar",
                    tint = Color.White
                )
            }

            // ── Toggle Lista/Mapa ──
            ViewModeToggle(
                viewMode = viewMode,
                onToggle = onToggleViewMode,
                modifier = Modifier.padding(end = 8.dp)
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = PylotoColors.MilitaryGreen
        )
    )
}

/**
 * Toggle chip para alternar entre Lista e Mapa.
 */
@Composable
private fun ViewModeToggle(
    viewMode: CorridasViewMode,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(
                color = Color.White.copy(alpha = 0.15f),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(2.dp),
        horizontalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        ViewModeChip(
            label = "Lista",
            icon = Icons.AutoMirrored.Filled.List,
            isSelected = viewMode == CorridasViewMode.LISTA,
            onClick = { if (viewMode != CorridasViewMode.LISTA) onToggle() }
        )
        ViewModeChip(
            label = "Mapa",
            icon = Icons.Default.Map,
            isSelected = viewMode == CorridasViewMode.MAPA,
            onClick = { if (viewMode != CorridasViewMode.MAPA) onToggle() }
        )
    }
}

@Composable
private fun ViewModeChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Color.White,
            selectedLabelColor = PylotoColors.MilitaryGreen,
            selectedLeadingIconColor = PylotoColors.MilitaryGreen,
            containerColor = Color.Transparent,
            labelColor = Color.White.copy(alpha = 0.8f),
            iconColor = Color.White.copy(alpha = 0.8f)
        ),
        border = FilterChipDefaults.filterChipBorder(
            borderColor = Color.Transparent,
            selectedBorderColor = Color.Transparent,
            enabled = true,
            selected = isSelected
        ),
        shape = RoundedCornerShape(16.dp)
    )
}

// ═══════════════════════════════════════════════════════════════
// BOTTOM NAVIGATION
// ═══════════════════════════════════════════════════════════════

@Composable
private fun CorridasBottomNavigation(
    onHomeClick: () -> Unit,
    onCorridasClick: () -> Unit,
    onGanhosClick: () -> Unit,
    onPerfilClick: () -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = false,
            onClick = onHomeClick,
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text("Início") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PylotoColors.MilitaryGreen,
                selectedTextColor = PylotoColors.MilitaryGreen,
                indicatorColor = PylotoColors.MilitaryGreen.copy(alpha = 0.1f)
            )
        )
        NavigationBarItem(
            selected = true,
            onClick = onCorridasClick,
            icon = {
                Icon(Icons.AutoMirrored.Filled.List, contentDescription = null)
            },
            label = { Text("Corridas") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PylotoColors.MilitaryGreen,
                selectedTextColor = PylotoColors.MilitaryGreen,
                indicatorColor = PylotoColors.MilitaryGreen.copy(alpha = 0.1f)
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = onGanhosClick,
            icon = { Icon(Icons.Default.AccountBalance, contentDescription = null) },
            label = { Text("Ganhos") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PylotoColors.MilitaryGreen,
                selectedTextColor = PylotoColors.MilitaryGreen,
                indicatorColor = PylotoColors.MilitaryGreen.copy(alpha = 0.1f)
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = onPerfilClick,
            icon = { Icon(Icons.Default.Person, contentDescription = null) },
            label = { Text("Perfil") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = PylotoColors.MilitaryGreen,
                selectedTextColor = PylotoColors.MilitaryGreen,
                indicatorColor = PylotoColors.MilitaryGreen.copy(alpha = 0.1f)
            )
        )
    }
}

// ═══════════════════════════════════════════════════════════════
// ESTADOS DA TELA
// ═══════════════════════════════════════════════════════════════

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(color = PylotoColors.MilitaryGreen)
            Text(
                text = "Buscando corridas...",
                style = MaterialTheme.typography.bodyLarge,
                color = PylotoColors.TextSecondary
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// PREVIEW
// ═══════════════════════════════════════════════════════════════

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun CorridasScreenPreview() {
    PylotoTheme {
        // Preview estático sem Hilt
        Scaffold(
            topBar = {
                @OptIn(ExperimentalMaterial3Api::class)
                TopAppBar(
                    title = { Text("Corridas", color = Color.White) },
                    actions = {
                        ViewModeToggle(
                            viewMode = CorridasViewMode.LISTA,
                            onToggle = {},
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = PylotoColors.MilitaryGreen
                    )
                )
            },
            containerColor = PylotoColors.Parchment
        ) { _ ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Preview — Use emulador para ver com dados mock",
                    style = MaterialTheme.typography.bodyMedium,
                    color = PylotoColors.TextSecondary,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
