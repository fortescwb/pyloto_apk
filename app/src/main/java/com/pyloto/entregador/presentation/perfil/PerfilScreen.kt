package com.pyloto.entregador.presentation.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pyloto.entregador.domain.model.Entregador
import com.pyloto.entregador.domain.model.Veiculo
import com.pyloto.entregador.domain.model.VeiculoTipo
import com.pyloto.entregador.presentation.perfil.components.DadosPessoaisSheet
import com.pyloto.entregador.presentation.perfil.components.FinanceiroSheet
import com.pyloto.entregador.presentation.perfil.components.MetaSemanalSheet
import com.pyloto.entregador.presentation.perfil.components.ProfileHeader
import com.pyloto.entregador.presentation.perfil.components.ProfileMenuSection
import com.pyloto.entregador.presentation.perfil.components.VeiculoSheet
import com.pyloto.entregador.presentation.theme.PylotoColors
import com.pyloto.entregador.presentation.theme.PylotoTheme
import kotlinx.coroutines.flow.collectLatest

// ═══════════════════════════════════════════════════════════════
// BOTTOM SHEET STATES
// ═══════════════════════════════════════════════════════════════

/**
 * Identifica qual bottom sheet está aberto.
 */
private enum class ActiveSheet {
    NONE,
    DADOS_PESSOAIS,
    FINANCEIRO,
    META_SEMANAL,
    VEICULO
}

// ═══════════════════════════════════════════════════════════════
// TELA PRINCIPAL
// ═══════════════════════════════════════════════════════════════

/**
 * Tela de Perfil do entregador Pyloto.
 *
 * Exibe foto, nome, rating/corridas e o menu de seções:
 * Dados Pessoais, Financeiro, Meta Semanal, Veículo. Cada item
 * abre um ModalBottomSheet. O botão "Sair" realiza logout.
 *
 * @param onNavigateBack volta à tela anterior
 * @param onGanhosClick navega para tela de Ganhos
 * @param onLogout callback chamado quando logout por evento do VM
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    onNavigateBack: () -> Unit,
    onGanhosClick: () -> Unit,
    onLogout: () -> Unit,
    viewModel: PerfilViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var activeSheet by remember { mutableStateOf(ActiveSheet.NONE) }

    // ── Observar eventos (DadosSalvos, Logout) ───────────────
    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is PerfilEvent.DadosSalvos -> {
                    activeSheet = ActiveSheet.NONE
                    snackbarHostState.showSnackbar("Dados salvos com sucesso!")
                }
                is PerfilEvent.LogoutRealizado -> onLogout()
            }
        }
    }

    // ── Exibir erro como snackbar ────────────────────────────
    LaunchedEffect(uiState.erro) {
        uiState.erro?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.limparErro()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Meu Perfil",
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                },
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

        if (uiState.isLoading) {
            LoadingState(modifier = Modifier.padding(innerPadding))
            return@Scaffold
        }

        val entregador = uiState.entregador
        if (entregador == null) {
            ErrorState(
                message = "Não foi possível carregar o perfil.",
                onRetry = { viewModel.loadPerfil() },
                modifier = Modifier.padding(innerPadding)
            )
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // ── Header (foto, nome, rating) ──────────────────
            ProfileHeader(
                nome = entregador.nome,
                fotoUrl = entregador.fotoUrl,
                rating = entregador.rating,
                totalCorridas = entregador.totalCorridas,
                onEditPhoto = {
                    // TODO: Abrir camera/galeria para editar foto
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ── Menu de seções + Sair ────────────────────────
            ProfileMenuSection(
                onDadosPessoaisClick = { activeSheet = ActiveSheet.DADOS_PESSOAIS },
                onFinanceiroClick = { activeSheet = ActiveSheet.FINANCEIRO },
                onMetaSemanalClick = { activeSheet = ActiveSheet.META_SEMANAL },
                onVeiculoClick = { activeSheet = ActiveSheet.VEICULO },
                onLogout = { viewModel.logout() }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        // ═══════════════════════════════════════════════════
        // BOTTOM SHEETS
        // ═══════════════════════════════════════════════════

        when (activeSheet) {
            ActiveSheet.DADOS_PESSOAIS -> {
                DadosPessoaisSheet(
                    nome = entregador.nome,
                    email = entregador.email,
                    telefone = entregador.telefone,
                    cpf = entregador.cpf,
                    isSaving = uiState.isSaving,
                    onSave = { nome, telefone ->
                        viewModel.atualizarDadosPessoais(nome, telefone)
                    },
                    onDismiss = { activeSheet = ActiveSheet.NONE }
                )
            }

            ActiveSheet.FINANCEIRO -> {
                // TODO: Buscar dados reais de ganhos via repositório
                FinanceiroSheet(
                    totalBruto = 3250.00,
                    totalLiquido = 2847.50,
                    totalCorridas = entregador.totalCorridas,
                    mediaValorCorrida = 9.50,
                    onDismiss = { activeSheet = ActiveSheet.NONE }
                )
            }

            ActiveSheet.META_SEMANAL -> {
                MetaSemanalSheet(
                    metaAtual = uiState.metaSemanal,
                    onSave = { novaMeta ->
                        viewModel.atualizarMetaSemanal(novaMeta)
                        activeSheet = ActiveSheet.NONE
                    },
                    onDismiss = { activeSheet = ActiveSheet.NONE }
                )
            }

            ActiveSheet.VEICULO -> {
                VeiculoSheet(
                    tipoAtual = entregador.veiculo?.tipo,
                    placaAtual = entregador.veiculo?.placa,
                    isSaving = uiState.isSaving,
                    onSave = { tipo, placa ->
                        viewModel.atualizarVeiculo(tipo, placa)
                    },
                    onDismiss = { activeSheet = ActiveSheet.NONE }
                )
            }

            ActiveSheet.NONE -> { /* Nenhum sheet aberto */ }
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// ESTADOS DE TELA
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
                text = "Carregando perfil...",
                style = MaterialTheme.typography.bodyLarge,
                color = PylotoColors.TextSecondary
            )
        }
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color = PylotoColors.StatusRejected
            )
            androidx.compose.material3.TextButton(onClick = onRetry) {
                Text("Tentar novamente", color = PylotoColors.MilitaryGreen)
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════
// PREVIEW
// ═══════════════════════════════════════════════════════════════

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PerfilScreenPreview() {
    PylotoTheme {
        // Preview estático sem Hilt — usa um Column diretamente
        val mockEntregador = Entregador(
            id = "001",
            nome = "João da Silva",
            email = "joao@email.com",
            telefone = "(42) 99999-8888",
            cpf = "123.456.789-00",
            fotoUrl = null,
            veiculo = Veiculo(VeiculoTipo.MOTO, "ABC-1D23"),
            rating = 4.8,
            totalCorridas = 342,
            statusOnline = false
        )

        Scaffold(
            topBar = {
                @OptIn(ExperimentalMaterial3Api::class)
                TopAppBar(
                    title = { Text("Meu Perfil", color = Color.White) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = PylotoColors.MilitaryGreen
                    )
                )
            },
            containerColor = PylotoColors.Parchment
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                ProfileHeader(
                    nome = mockEntregador.nome,
                    fotoUrl = mockEntregador.fotoUrl,
                    rating = mockEntregador.rating,
                    totalCorridas = mockEntregador.totalCorridas,
                    onEditPhoto = {}
                )
                Spacer(modifier = Modifier.height(8.dp))
                ProfileMenuSection(
                    onDadosPessoaisClick = {},
                    onFinanceiroClick = {},
                    onMetaSemanalClick = {},
                    onVeiculoClick = {},
                    onLogout = {}
                )
            }
        }
    }
}
