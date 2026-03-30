package com.pyloto.entregador.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pyloto.entregador.domain.model.OnboardingStatus
import com.pyloto.entregador.domain.usecase.auth.LogoutUseCase
import com.pyloto.entregador.domain.usecase.entregador.EnviarAuditoriaVeiculoUseCase
import com.pyloto.entregador.domain.usecase.entregador.EnviarAssinaturaDigitalContratoUseCase
import com.pyloto.entregador.domain.usecase.entregador.ObterOnboardingStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContractSignatureViewModel @Inject constructor(
    private val obterOnboardingStatusUseCase: ObterOnboardingStatusUseCase,
    private val enviarAssinaturaDigitalContratoUseCase: EnviarAssinaturaDigitalContratoUseCase,
    private val enviarAuditoriaVeiculoUseCase: EnviarAuditoriaVeiculoUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ContractSignatureUiState())
    val uiState: StateFlow<ContractSignatureUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ContractSignatureEvent>()
    val events: SharedFlow<ContractSignatureEvent> = _events.asSharedFlow()

    init {
        loadStatus()
    }

    fun loadStatus() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching {
                obterOnboardingStatusUseCase()
            }.onSuccess { status ->
                applyStatus(status, isLoading = false, isSubmitting = false)
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = mapErrorMessage(error)
                    )
                }
            }
        }
    }

    fun onAssinaturaDigitalRefChange(value: String) {
        _uiState.update { it.copy(assinaturaDigitalRef = value, error = null) }
    }

    fun onFotoVeiculoRefChange(value: String) {
        _uiState.update { it.copy(fotoVeiculoRef = value, error = null) }
    }

    fun onPlacaInformadaChange(value: String) {
        _uiState.update { it.copy(placaInformada = value.uppercase(), error = null) }
    }

    fun submitDigitalSignature() {
        val assinaturaDigitalRef = uiState.value.assinaturaDigitalRef.trim()
        if (assinaturaDigitalRef.length < 3) {
            _uiState.update {
                it.copy(error = "Informe a referencia ou URL da via assinada digitalmente.")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(isSubmitting = true, submittingAction = "contract", error = null)
            }
            runCatching {
                enviarAssinaturaDigitalContratoUseCase(assinaturaDigitalRef)
            }.onSuccess { status ->
                applyStatus(status, isLoading = false, isSubmitting = false)
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        submittingAction = null,
                        error = mapErrorMessage(error)
                    )
                }
            }
        }
    }

    fun submitVehicleAudit() {
        val status = uiState.value.status
        val incidentId = status?.vehicleAuditIncidentId.orEmpty()
        val fotoVeiculoRef = uiState.value.fotoVeiculoRef.trim()
        val placaInformada = uiState.value.placaInformada.trim().ifBlank { null }

        if (incidentId.isBlank()) {
            _uiState.update {
                it.copy(error = "A auditoria do veiculo ainda nao esta pronta para envio.")
            }
            return
        }

        if (fotoVeiculoRef.length < 3) {
            _uiState.update {
                it.copy(error = "Informe a referencia ou URL da foto atual do veiculo.")
            }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(isSubmitting = true, submittingAction = "vehicle_audit", error = null)
            }
            runCatching {
                enviarAuditoriaVeiculoUseCase(
                    incidentId = incidentId,
                    fotoVeiculoRef = fotoVeiculoRef,
                    placaInformada = placaInformada
                )
            }.onSuccess { updatedStatus ->
                applyStatus(updatedStatus, isLoading = false, isSubmitting = false)
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        submittingAction = null,
                        error = mapErrorMessage(error)
                    )
                }
            }
        }
    }

    fun skip() {
        viewModelScope.launch {
            _events.emit(ContractSignatureEvent.SkipRequested)
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _events.emit(ContractSignatureEvent.LogoutCompleted)
        }
    }

    private suspend fun applyStatus(
        status: OnboardingStatus,
        isLoading: Boolean,
        isSubmitting: Boolean
    ) {
        _uiState.update { current ->
            current.copy(
                isLoading = isLoading,
                isSubmitting = isSubmitting,
                status = status,
                assinaturaDigitalRef = if (
                    current.assinaturaDigitalRef.isBlank() &&
                    status.contratoAssinaturaDigitalRef.isNotBlank()
                ) {
                    status.contratoAssinaturaDigitalRef
                } else {
                    current.assinaturaDigitalRef
                },
                fotoVeiculoRef = if (status.vehicleAuditRequired) current.fotoVeiculoRef else "",
                placaInformada = if (status.vehicleAuditRequired) current.placaInformada else "",
                submittingAction = null,
                error = null
            )
        }

        if (status.prontoParaOperacao) {
            _events.emit(ContractSignatureEvent.OnboardingCompleted)
        }
    }

    private fun mapErrorMessage(error: Throwable): String {
        return when {
            error is java.net.UnknownHostException ||
                error is java.net.ConnectException ->
                "Sem conexao com a internet. Verifique sua rede e tente novamente."

            error is java.net.SocketTimeoutException ->
                "Servidor demorou para responder. Tente novamente."

            error.message?.contains("403") == true ->
                "A operacao do app ainda nao foi liberada para este cadastro."

            error.message?.contains("404") == true ->
                "Contrato nao encontrado para este cadastro."

            else -> error.message ?: "Nao foi possivel atualizar a assinatura digital."
        }
    }
}

data class ContractSignatureUiState(
    val isLoading: Boolean = true,
    val isSubmitting: Boolean = false,
    val submittingAction: String? = null,
    val status: OnboardingStatus? = null,
    val assinaturaDigitalRef: String = "",
    val fotoVeiculoRef: String = "",
    val placaInformada: String = "",
    val error: String? = null
)

sealed class ContractSignatureEvent {
    object OnboardingCompleted : ContractSignatureEvent()
    object SkipRequested : ContractSignatureEvent()
    object LogoutCompleted : ContractSignatureEvent()
}
