package com.pyloto.entregador.presentation.corridas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pyloto.entregador.domain.model.Corrida
import com.pyloto.entregador.domain.repository.CorridaRepository
import com.pyloto.entregador.domain.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

// ═══════════════════════════════════════════════════════════════
// VIEW MODE
// ═══════════════════════════════════════════════════════════════

enum class CorridasViewMode { LISTA, MAPA }

// ═══════════════════════════════════════════════════════════════
// UI STATE
// ═══════════════════════════════════════════════════════════════

data class CorridasUiState(
    val isLoading: Boolean = true,
    val corridas: List<Corrida> = emptyList(),
    val viewMode: CorridasViewMode = CorridasViewMode.LISTA,
    val entregadorLat: Double = -25.4284,   // Scaffold — Ponta Grossa, PR
    val entregadorLng: Double = -49.2733,
    val erro: String? = null
) {
    /**
     * Corridas ordenadas por distância até o ponto de coleta
     * (distância euclidiana simples como scaffold; substituir por road distance).
     */
    val corridasOrdenadas: List<CorridaComDistancia>
        get() = corridas.map { corrida ->
            val distanciaAteColeta = haversineKm(
                entregadorLat, entregadorLng,
                corrida.origem.latitude, corrida.origem.longitude
            )
            CorridaComDistancia(corrida, distanciaAteColeta)
        }.sortedBy { it.distanciaAteColetaKm }
}

/**
 * Wrapper que agrega a distância do entregador até o ponto de coleta.
 */
data class CorridaComDistancia(
    val corrida: Corrida,
    val distanciaAteColetaKm: Double
) {
    val distanciaAteColetaFormatada: String
        get() = if (distanciaAteColetaKm < 1.0) {
            "${(distanciaAteColetaKm * 1000).toInt()}m"
        } else {
            "%.1fkm".format(distanciaAteColetaKm)
        }
}

/**
 * Fórmula Haversine para distância entre dois pontos geográficos.
 */
private fun haversineKm(
    lat1: Double, lon1: Double,
    lat2: Double, lon2: Double
): Double {
    val r = 6371.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
            Math.sin(dLon / 2) * Math.sin(dLon / 2)
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    return r * c
}

// ═══════════════════════════════════════════════════════════════
// VIEWMODEL
// ═══════════════════════════════════════════════════════════════

@HiltViewModel
class CorridasViewModel @Inject constructor(
    private val corridaRepository: CorridaRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CorridasUiState())
    val uiState: StateFlow<CorridasUiState> = _uiState.asStateFlow()

    init {
        loadCorridas()
    }

    /**
     * Carrega corridas disponíveis.
     * TODO: Substituir scaffold por chamada real ao repositório.
     */
    fun loadCorridas() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val location = locationRepository.getLastKnownLocation()
                val lat = location?.latitude ?: _uiState.value.entregadorLat
                val lng = location?.longitude ?: _uiState.value.entregadorLng
                val corridas = corridaRepository.getCorridasDisponiveis(lat, lng)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        corridas = corridas,
                        entregadorLat = lat,
                        entregadorLng = lng,
                        erro = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        erro = e.message ?: "Erro ao carregar corridas"
                    )
                }
            }
        }
    }

    /**
     * Alterna entre modo Lista e Mapa.
     */
    fun toggleViewMode() {
        _uiState.update {
            it.copy(
                viewMode = if (it.viewMode == CorridasViewMode.LISTA)
                    CorridasViewMode.MAPA
                else
                    CorridasViewMode.LISTA
            )
        }
    }

    fun limparErro() {
        _uiState.update { it.copy(erro = null) }
    }

}
