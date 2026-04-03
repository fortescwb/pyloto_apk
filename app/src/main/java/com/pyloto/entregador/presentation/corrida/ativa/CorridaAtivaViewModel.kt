package com.pyloto.entregador.presentation.corrida.ativa

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pyloto.entregador.domain.model.ActiveRouteContext
import com.pyloto.entregador.domain.model.Corrida
import com.pyloto.entregador.domain.model.CorridaOperationalEvent
import com.pyloto.entregador.domain.model.CorridaStatus
import com.pyloto.entregador.domain.repository.CorridaRepository
import com.pyloto.entregador.domain.repository.LocationRepository
import com.pyloto.entregador.domain.repository.PreferencesRepository
import com.pyloto.entregador.domain.usecase.corrida.ColetarCorridaUseCase
import com.pyloto.entregador.domain.usecase.corrida.FinalizarCorridaUseCase
import com.pyloto.entregador.domain.usecase.corrida.IniciarCorridaUseCase
import com.pyloto.entregador.domain.usecase.corrida.RegistrarEventoOperacionalCorridaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.asin
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

data class CorridaAtivaUiState(
    val isLoading: Boolean = true,
    val isSubmitting: Boolean = false,
    val corrida: Corrida? = null,
    val localizacaoLat: Double? = null,
    val localizacaoLng: Double? = null,
    val distanciaRestanteM: Int? = null,
    val etaRestanteMin: Int? = null,
    val erro: String? = null,
    val currentStep: Int = 0,
    val fotoComprovanteUrl: String = "",
    val comprovanteUploadId: String = ""
)

sealed interface CorridaAtivaEffect {
    data object StartTracking : CorridaAtivaEffect
    data object StopTracking : CorridaAtivaEffect
    data object Finished : CorridaAtivaEffect
}

@HiltViewModel
class CorridaAtivaViewModel @Inject constructor(
    private val corridaRepository: CorridaRepository,
    private val locationRepository: LocationRepository,
    private val preferencesRepository: PreferencesRepository,
    private val iniciarCorridaUseCase: IniciarCorridaUseCase,
    private val coletarCorridaUseCase: ColetarCorridaUseCase,
    private val finalizarCorridaUseCase: FinalizarCorridaUseCase,
    private val registrarEventoOperacionalUseCase: RegistrarEventoOperacionalCorridaUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CorridaAtivaUiState())
    val uiState: StateFlow<CorridaAtivaUiState> = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<CorridaAtivaEffect>()
    val effects: SharedFlow<CorridaAtivaEffect> = _effects.asSharedFlow()

    init {
        observeLocationUpdates()
    }

    fun loadCorrida(corridaId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, erro = null) }
            runCatching {
                corridaRepository.getCorridaDetalhes(corridaId)
            }.onSuccess { corrida ->
                _uiState.update { state ->
                    val nextStep = inferStep(corrida, state.currentStep)
                    state.copy(
                        isLoading = false,
                        corrida = corrida,
                        erro = null,
                        currentStep = nextStep
                    ).refreshRealtimeMetrics()
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        erro = error.message ?: "Erro ao carregar corrida"
                    )
                }
            }
        }
    }

    fun updateFotoComprovanteUrl(value: String) {
        _uiState.update { it.copy(fotoComprovanteUrl = value) }
    }

    fun updateComprovanteUploadId(value: String) {
        _uiState.update { it.copy(comprovanteUploadId = value) }
    }

    fun iniciarRotaColeta(corridaId: String) {
        runAction(corridaId, nextStep = 1) {
            iniciarCorridaUseCase(corridaId).getOrThrow()
            persistRouteContext(corridaId, "coleta")
            _effects.emit(CorridaAtivaEffect.StartTracking)
        }
    }

    fun registrarChegadaColeta(corridaId: String) {
        runAction(corridaId, nextStep = 2) {
            registrarEventoOperacionalUseCase(
                corridaId,
                CorridaOperationalEvent(
                    kind = "pickup_arrived",
                    message = "Parceiro esta aguardando para coletar.",
                    source = "active_screen"
                )
            ).getOrThrow()
        }
    }

    fun confirmarColeta(corridaId: String) {
        runAction(corridaId, nextStep = 3) {
            coletarCorridaUseCase(corridaId).getOrThrow()
            persistRouteContext(corridaId, "coleta_concluida")
        }
    }

    fun iniciarRotaEntrega(corridaId: String) {
        runAction(corridaId, nextStep = 4) {
            iniciarCorridaUseCase(corridaId).getOrThrow()
            persistRouteContext(corridaId, "entrega")
            _effects.emit(CorridaAtivaEffect.StartTracking)
        }
    }

    fun registrarChegadaDestino(corridaId: String) {
        runAction(corridaId, nextStep = 5) {
            registrarEventoOperacionalUseCase(
                corridaId,
                CorridaOperationalEvent(
                    kind = "dropoff_arrived",
                    message = "Parceiro esta aguardando o destinatario.",
                    source = "active_screen"
                )
            ).getOrThrow()
        }
    }

    fun finalizarEntrega(corridaId: String) {
        runAction(corridaId, nextStep = 5) {
            finalizarCorridaUseCase(
                corridaId,
                _uiState.value.fotoComprovanteUrl.trim().ifEmpty { null },
                _uiState.value.comprovanteUploadId.trim().ifEmpty { null }
            ).getOrThrow()
            preferencesRepository.clearActiveRouteContext()
            _effects.emit(CorridaAtivaEffect.StopTracking)
            _effects.emit(CorridaAtivaEffect.Finished)
        }
    }

    private fun runAction(
        corridaId: String,
        nextStep: Int,
        action: suspend () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true, erro = null) }
            runCatching {
                action()
            }.onSuccess {
                _uiState.update { state ->
                    state.copy(isSubmitting = false, currentStep = nextStep).refreshRealtimeMetrics()
                }
                loadCorrida(corridaId)
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isSubmitting = false,
                        erro = error.message ?: "Erro ao atualizar corrida"
                    )
                }
            }
        }
    }

    private suspend fun persistRouteContext(corridaId: String, phase: String) {
        preferencesRepository.saveActiveRouteContext(
            ActiveRouteContext(
                pedidoId = corridaId,
                phase = phase,
                startedAt = System.currentTimeMillis()
            )
        )
    }

    private fun inferStep(corrida: Corrida, currentStep: Int): Int {
        if (currentStep > 0) {
            return currentStep
        }

        return when (corrida.status) {
            CorridaStatus.ACEITA -> 0
            CorridaStatus.A_CAMINHO_COLETA -> 1
            CorridaStatus.COLETADA -> 3
            CorridaStatus.A_CAMINHO_ENTREGA -> 4
            CorridaStatus.FINALIZADA -> 5
            CorridaStatus.CANCELADA -> 5
            else -> 0
        }
    }

    private fun observeLocationUpdates() {
        viewModelScope.launch {
            locationRepository.getLastLocation().collect { location ->
                location ?: return@collect
                _uiState.update { state ->
                    state.copy(
                        localizacaoLat = location.latitude,
                        localizacaoLng = location.longitude
                    ).refreshRealtimeMetrics()
                }
            }
        }
    }

    private fun CorridaAtivaUiState.refreshRealtimeMetrics(): CorridaAtivaUiState {
        val corridaAtual = corrida ?: return copy(distanciaRestanteM = null, etaRestanteMin = null)
        val latAtual = localizacaoLat ?: return copy(distanciaRestanteM = null, etaRestanteMin = null)
        val lngAtual = localizacaoLng ?: return copy(distanciaRestanteM = null, etaRestanteMin = null)

        val targetLat: Double
        val targetLng: Double
        if (currentStep <= 2) {
            targetLat = corridaAtual.origem.latitude
            targetLng = corridaAtual.origem.longitude
        } else {
            targetLat = corridaAtual.destino.latitude
            targetLng = corridaAtual.destino.longitude
        }

        val remainingMeters = haversineDistanceMeters(
            originLat = latAtual,
            originLng = lngAtual,
            destinationLat = targetLat,
            destinationLng = targetLng,
        )
        val etaMinutes = maxOf(1, ceil(remainingMeters / ETA_CITY_SPEED_M_PER_MIN).toInt())

        return copy(
            distanciaRestanteM = remainingMeters.toInt(),
            etaRestanteMin = etaMinutes
        )
    }

    private fun haversineDistanceMeters(
        originLat: Double,
        originLng: Double,
        destinationLat: Double,
        destinationLng: Double
    ): Double {
        val deltaLat = Math.toRadians(destinationLat - originLat)
        val deltaLng = Math.toRadians(destinationLng - originLng)
        val startLat = Math.toRadians(originLat)
        val endLat = Math.toRadians(destinationLat)
        val arc = sin(deltaLat / 2) * sin(deltaLat / 2) +
            cos(startLat) * cos(endLat) * sin(deltaLng / 2) * sin(deltaLng / 2)
        return 2 * EARTH_RADIUS_M * asin(sqrt(arc))
    }

    private companion object {
        const val EARTH_RADIUS_M = 6_371_000.0
        const val ETA_CITY_SPEED_M_PER_MIN = 450.0
    }
}
