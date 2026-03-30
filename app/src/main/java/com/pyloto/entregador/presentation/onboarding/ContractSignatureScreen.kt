package com.pyloto.entregador.presentation.onboarding

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pyloto.entregador.presentation.theme.PylotoColors
import com.pyloto.entregador.presentation.theme.PylotoTheme

@Composable
fun ContractSignatureScreen(
    onOnboardingCompleted: () -> Unit,
    onSkip: () -> Unit,
    onLogout: () -> Unit,
    viewModel: ContractSignatureViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                ContractSignatureEvent.OnboardingCompleted -> onOnboardingCompleted()
                ContractSignatureEvent.SkipRequested -> onSkip()
                ContractSignatureEvent.LogoutCompleted -> onLogout()
            }
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let { snackbarHostState.showSnackbar(it) }
    }

    val status = uiState.status
    val contractDownloadRef = status?.contratoDownloadRef.orEmpty()
    val contractUrl = contractDownloadRef.takeIf {
        it.startsWith("http://", ignoreCase = true) ||
            it.startsWith("https://", ignoreCase = true)
    }
    val requiresDigitalSignature = status?.requiresDigitalContractSignature == true
    val vehicleAuditRequired = status?.vehicleAuditRequired == true
    val selfServiceAvailable = requiresDigitalSignature || vehicleAuditRequired

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = PylotoColors.Parchment
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(PylotoColors.Parchment)
                .padding(innerPadding)
        ) {
            if (uiState.isLoading && status == null) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = PylotoColors.MilitaryGreen
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp)
                        .imePadding(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Spacer(modifier = Modifier.height(40.dp))

                    Text(
                        text = "Regularizacao do cadastro",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = PylotoColors.Black
                        )
                    )

                    Text(
                        text = "Conclua as pendencias operacionais abaixo para liberar o acesso ao app parceiro.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = PylotoColors.TextSecondary
                    )

                    status?.let {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.large,
                            color = PylotoColors.White
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Text(
                                    text = "Status do cadastro",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = PylotoColors.Black
                                )
                                StatusLine("Status cadastral", it.statusCadastral.ifBlank { "pendente" })
                                StatusLine("Status operacional", it.statusOperacional.ifBlank { "em_analise" })
                                StatusLine("Versao do contrato", it.contratoVersao.ifBlank { "nao informada" })
                                if (it.pendingReason != null) {
                                    Text(
                                        text = it.pendingReason,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = PylotoTheme.extendedColors.techBlue
                                    )
                                }
                            }
                        }
                    }

                    if (!status?.documentAlerts.isNullOrEmpty()) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium,
                            color = PylotoColors.GoldLight.copy(alpha = 0.35f)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "Alertas documentais",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = PylotoColors.Black
                                )
                                status?.documentAlerts?.forEach { alert ->
                                    Text(
                                        text = alert,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = PylotoColors.TextSecondary
                                    )
                                }
                            }
                        }
                    }

                    if (!status?.documentBlockers.isNullOrEmpty()) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium,
                            color = PylotoColors.StatusRejected.copy(alpha = 0.08f)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Pendencias documentais",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = PylotoColors.Black
                                )
                                status?.documentBlockers?.forEach { blocker ->
                                    Text(
                                        text = blocker,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = PylotoColors.StatusRejected
                                    )
                                }
                            }
                        }
                    }

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large,
                        color = PylotoColors.White
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Contrato e termos de uso",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.SemiBold
                                ),
                                color = PylotoColors.Black
                            )

                            Text(
                                text = if (requiresDigitalSignature) {
                                    "Baixe a via disponibilizada pela equipe Pyloto, assine no Gov.br e envie a referencia da via assinada."
                                } else {
                                    "A assinatura digital do contrato ja foi registrada. Se necessario, voce ainda pode baixar a via atual."
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = PylotoColors.TextSecondary
                            )

                            Button(
                                onClick = {
                                    contractUrl?.let { url ->
                                        try {
                                            context.startActivity(
                                                Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                            )
                                        } catch (_: ActivityNotFoundException) {
                                            viewModel.loadStatus()
                                        }
                                    }
                                },
                                enabled = contractUrl != null,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PylotoColors.TechBlue,
                                    contentColor = PylotoColors.White
                                )
                            ) {
                                Text("Baixar contrato para assinatura")
                            }

                            if (contractDownloadRef.isBlank()) {
                                Text(
                                    text = "A via do contrato ainda nao foi anexada pela equipe Pyloto.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = PylotoColors.StatusRejected
                                )
                            } else if (contractUrl == null) {
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = MaterialTheme.shapes.medium,
                                    color = PylotoColors.Parchment
                                ) {
                                    Column(
                                        modifier = Modifier.padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = "Referencia atual do contrato",
                                            style = MaterialTheme.typography.labelLarge,
                                            color = PylotoColors.Black
                                        )
                                        SelectionContainer {
                                            Text(
                                                text = contractDownloadRef,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = PylotoColors.TextSecondary
                                            )
                                        }
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = uiState.assinaturaDigitalRef,
                                onValueChange = viewModel::onAssinaturaDigitalRefChange,
                                label = { Text("Referencia ou URL da via assinada") },
                                placeholder = { Text("Cole aqui a referencia do Gov.br") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 3,
                                maxLines = 5,
                                shape = MaterialTheme.shapes.medium
                            )

                            Button(
                                onClick = viewModel::submitDigitalSignature,
                                enabled = !uiState.isLoading && !uiState.isSubmitting,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PylotoTheme.extendedColors.gold,
                                    contentColor = PylotoColors.White
                                )
                            ) {
                                if (uiState.isSubmitting && uiState.submittingAction == "contract") {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp,
                                        color = PylotoColors.White
                                    )
                                } else {
                                    Text(
                                        text = if (status?.contratoAssinaturaDigitalConcluida == true) {
                                            "Atualizar assinatura digital"
                                        } else {
                                            "Enviar assinatura digital"
                                        }
                                    )
                                }
                            }
                        }
                    }

                    if (vehicleAuditRequired) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.large,
                            color = PylotoColors.White
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "Auditoria do veiculo em uso",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = PylotoColors.Black
                                )
                                Text(
                                    text = "Envie uma foto atual do veiculo com a placa visivel para a equipe Pyloto validar o cadastro em operacao.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = PylotoColors.TextSecondary
                                )

                                OutlinedTextField(
                                    value = uiState.fotoVeiculoRef,
                                    onValueChange = viewModel::onFotoVeiculoRefChange,
                                    label = { Text("Referencia ou URL da foto do veiculo") },
                                    placeholder = { Text("Cole aqui a referencia do arquivo enviado") },
                                    modifier = Modifier.fillMaxWidth(),
                                    minLines = 3,
                                    maxLines = 5,
                                    shape = MaterialTheme.shapes.medium
                                )

                                OutlinedTextField(
                                    value = uiState.placaInformada,
                                    onValueChange = viewModel::onPlacaInformadaChange,
                                    label = { Text("Placa informada no envio") },
                                    placeholder = { Text("Ex.: ABC1D23") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    shape = MaterialTheme.shapes.medium
                                )

                                Button(
                                    onClick = viewModel::submitVehicleAudit,
                                    enabled = !uiState.isLoading && !uiState.isSubmitting,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = PylotoColors.Black,
                                        contentColor = PylotoColors.White
                                    )
                                ) {
                                    if (uiState.isSubmitting && uiState.submittingAction == "vehicle_audit") {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            strokeWidth = 2.dp,
                                            color = PylotoColors.White
                                        )
                                    } else {
                                        Text("Enviar foto do veiculo")
                                    }
                                }
                            }
                        }
                    }

                    if (!selfServiceAvailable && status?.prontoParaOperacao == false) {
                        Text(
                            text = "Sua operacao segue bloqueada. Aguarde a revisao da equipe Pyloto para liberar novas corridas.",
                            style = MaterialTheme.typography.bodySmall,
                            color = PylotoColors.TextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Text(
                            text = "Depois do envio, o app atualiza o status do cadastro e libera o acesso quando nao houver mais bloqueios.",
                            style = MaterialTheme.typography.bodySmall,
                            color = PylotoColors.TextSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Button(
                        onClick = viewModel::skip,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PylotoColors.MilitaryGreen,
                            contentColor = PylotoColors.White
                        )
                    ) {
                        Text("Mais tarde")
                    }

                    TextButton(
                        onClick = viewModel::loadStatus,
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Atualizar status do cadastro")
                    }

                    TextButton(
                        onClick = viewModel::logout,
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Sair",
                            color = PylotoColors.StatusRejected
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun StatusLine(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = PylotoColors.TextSecondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = PylotoColors.Black
        )
    }
}
