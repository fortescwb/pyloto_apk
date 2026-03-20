package com.pyloto.entregador.presentation.home

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pyloto.entregador.domain.repository.LocationRepository
import com.pyloto.entregador.domain.repository.PreferencesRepository
import com.pyloto.entregador.domain.usecase.corrida.AceitarCorridaUseCase
import com.pyloto.entregador.domain.usecase.corrida.ObterCorridasDisponiveisUseCase
import com.pyloto.entregador.domain.usecase.entregador.AtualizarStatusOnlineUseCase
import com.pyloto.entregador.domain.usecase.home.ObterDailyStatsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val obterCorridasUseCase: ObterCorridasDisponiveisUseCase,
    private val aceitarCorridaUseCase: AceitarCorridaUseCase,
    private val atualizarStatusOnlineUseCase: AtualizarStatusOnlineUseCase,
    private val obterDailyStatsUseCase: ObterDailyStatsUseCase,
    private val locationRepository: LocationRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<HomeEvent>()
    val events: SharedFlow<HomeEvent> = _events.asSharedFlow()

    private var loadCorridasJob: Job? = null

    init {
        observeGoals()
        observeLocation()
        loadCorridas()
        loadDailyStats()
    }

    private fun observeGoals() {
        viewModelScope.launch {
            preferencesRepository.observeDailyGoal().collect { goal ->
                _uiState.update { state -> state.copy(dailyGoal = goal) }
            }
        }
    }

    private fun observeLocation() {
        viewModelScope.launch {
            locationRepository.getLastLocation().collect { location ->
                _uiState.update { state ->
                    state.copy(localizacaoAtual = location?.toHomeLocation())
                }
            }
        }
    }

    fun alterarModoVisualizacao(modo: HomeModoVisualizacao) {
        if (_uiState.value.modoVisualizacao == modo) return
        _uiState.update { it.copy(modoVisualizacao = modo) }
        loadCorridas()
    }

    fun loadCorridas() {
        loadCorridasJob?.cancel()

        val raio = when (_uiState.value.modoVisualizacao) {
            HomeModoVisualizacao.PADRAO -> RAIO_PADRAO_METROS
            HomeModoVisualizacao.MAPA -> RAIO_MAPA_METROS
        }

        loadCorridasJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, erro = null) }
            obterCorridasUseCase(raio)
                .catch { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            erro = error.message ?: "Erro ao carregar corridas"
                        )
                    }
                }
                .collect { corridas ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            erro = null,
                            corridas = corridas
                        )
                    }
                }
        }
    }

    fun aceitarCorrida(corridaId: String) {
        viewModelScope.launch {
            aceitarCorridaUseCase(corridaId)
                .onSuccess {
                    _events.emit(HomeEvent.CorridaAceita(corridaId))
                    loadCorridas()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(erro = error.message ?: "Erro ao aceitar corrida")
                    }
                }
        }
    }

    fun toggleOnlineStatus() {
        viewModelScope.launch {
            val newStatus = !_uiState.value.isOnline
            runCatching {
                atualizarStatusOnlineUseCase(newStatus)
            }.onSuccess {
                _uiState.update { state -> state.copy(isOnline = newStatus, erro = null) }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(erro = error.message ?: "Erro ao atualizar status online")
                }
            }
        }
    }

    fun loadDailyStats() {
        viewModelScope.launch {
            runCatching {
                obterDailyStatsUseCase()
            }.onSuccess { stats ->
                _uiState.update { state -> state.copy(dailyStats = stats, erro = null) }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(erro = error.message ?: "Erro ao carregar estatisticas")
                }
            }
        }
    }

    fun updateDailyGoal(newGoal: Double) {
        viewModelScope.launch {
            preferencesRepository.saveDailyGoal(newGoal)
        }
    }

    private fun Location.toHomeLocation(): HomeLocation {
        return HomeLocation(latitude = latitude, longitude = longitude)
    }

    private companion object {
        const val RAIO_PADRAO_METROS = 5000
        const val RAIO_MAPA_METROS = 200
    }
}

