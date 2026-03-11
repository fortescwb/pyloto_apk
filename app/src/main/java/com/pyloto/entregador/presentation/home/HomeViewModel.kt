package com.pyloto.entregador.presentation.home

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pyloto.entregador.domain.model.Corrida
import com.pyloto.entregador.domain.model.DailyStats
import com.pyloto.entregador.domain.usecase.corrida.AceitarCorridaUseCase
import com.pyloto.entregador.domain.usecase.corrida.ObterCorridasDisponiveisUseCase
import com.pyloto.entregador.domain.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val obterCorridasUseCase: ObterCorridasDisponiveisUseCase,
    private val aceitarCorridaUseCase: AceitarCorridaUseCase,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<HomeEvent>()
    val events: SharedFlow<HomeEvent> = _events.asSharedFlow()

    private var loadCorridasJob: Job? = null

    init {
        observarLocalizacao()
        loadCorridas()
        loadDailyStats()
    }

    private fun observarLocalizacao() {
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
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            erro = e.message ?: "Erro ao carregar corridas"
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
                .onFailure { e ->
                    _uiState.update {
                        it.copy(erro = e.message ?: "Erro ao aceitar corrida")
                    }
                }
        }
    }

    // ═══════════════════════════════════════════════════════════
    // NOVOS MÉTODOS — Redesign Home Screen
    // ═══════════════════════════════════════════════════════════

    /**
     * Alterna o status online/offline do entregador.
     * TODO: Sincronizar com o backend quando o endpoint estiver pronto.
     */
    fun toggleOnlineStatus() {
        _uiState.update { it.copy(isOnline = !it.isOnline) }
        // TODO: Implementar lógica de sincronização com backend
        // ex: entregadorRepository.setOnlineStatus(uiState.value.isOnline)
    }

    /**
     * Carrega as estatísticas do dia do entregador.
     * TODO: Buscar dados reais do repositório / CORE API.
     * Por ora, utiliza dados scaffold/placeholder.
     */
    fun loadDailyStats() {
        viewModelScope.launch {
            // TODO: Substituir por chamada real ao repositório
            // val stats = dailyStatsRepository.getToday()
            _uiState.update {
                it.copy(
                    dailyStats = DailyStats(
                        earnings = 245.50,
                        deliveries = 12,
                        timeOnlineMinutes = 332,
                        maxTimeMinutes = 600,
                        totalFeeSavings = 24.55,
                        averagePerHour = 44.51
                    )
                )
            }
        }
    }

    /**
     * Atualiza a meta diária configurada pelo entregador.
     * TODO: Persistir em SharedPreferences ou no backend.
     */
    fun updateDailyGoal(newGoal: Double) {
        _uiState.update { it.copy(dailyGoal = newGoal) }
        // TODO: Persistir no DataStore / SharedPreferences
        // ex: preferencesRepository.setDailyGoal(newGoal)
    }

    private fun Location.toHomeLocation(): HomeLocation {
        return HomeLocation(latitude = latitude, longitude = longitude)
    }

    companion object {
        private const val RAIO_PADRAO_METROS = 5000
        private const val RAIO_MAPA_METROS = 200
    }
}

data class HomeUiState(
    // ── Estado original ──────────────────────────────────────
    val isLoading: Boolean = true,
    val modoVisualizacao: HomeModoVisualizacao = HomeModoVisualizacao.PADRAO,
    val corridas: List<Corrida> = emptyList(),
    val localizacaoAtual: HomeLocation? = null,
    val erro: String? = null,

    // ── Novos campos — Redesign ──────────────────────────────
    /** Status online/offline do entregador */
    val isOnline: Boolean = false,
    /** Cidade atual do entregador */
    val cidadeAtual: String = "Ponta Grossa, PR",
    /** Região/bairro atual do entregador */
    val regiaoAtual: String = "Centro",
    /** Estatísticas do dia (scaffold/placeholder por ora) */
    val dailyStats: DailyStats = DailyStats(),
    /** Meta diária configurável em R$ */
    val dailyGoal: Double = 300.0
)

enum class HomeModoVisualizacao {
    PADRAO,
    MAPA
}

data class HomeLocation(
    val latitude: Double,
    val longitude: Double
)

sealed class HomeEvent {
    data class CorridaAceita(val corridaId: String) : HomeEvent()
}
