package com.pyloto.entregador.presentation.corridas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pyloto.entregador.domain.repository.CorridaRepository
import com.pyloto.entregador.domain.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.math.abs
import javax.inject.Inject

@HiltViewModel
class CorridasViewModel @Inject constructor(
    private val corridaRepository: CorridaRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CorridasUiState())
    val uiState: StateFlow<CorridasUiState> = _uiState.asStateFlow()
    private var hasLoadedFromLocation = false

    init {
        observeLocation()
        waitForLocationThenLoad()
    }

    private fun waitForLocationThenLoad() {
        viewModelScope.launch {
            val location = withTimeoutOrNull(5_000) {
                locationRepository.getLastLocation()
                    .filterNotNull()
                    .first()
            }
            if (location != null) {
                _uiState.update {
                    it.copy(
                        entregadorLat = location.latitude,
                        entregadorLng = location.longitude
                    )
                }
            }
            if (!hasLoadedFromLocation) {
                loadCorridas()
            }
        }
    }

    private fun observeLocation() {
        viewModelScope.launch {
            locationRepository.getLastLocation().collect { location ->
                location ?: return@collect
                val lat = location.latitude
                val lng = location.longitude
                if (!lat.isFinite() || !lng.isFinite()) return@collect

                _uiState.update { state ->
                    val latDiff = abs(state.entregadorLat - lat)
                    val lngDiff = abs(state.entregadorLng - lng)
                    if (latDiff < LOCATION_EPSILON && lngDiff < LOCATION_EPSILON) {
                        return@update state
                    }
                    state.copy(
                        entregadorLat = lat,
                        entregadorLng = lng
                    )
                }

                if (!hasLoadedFromLocation) {
                    hasLoadedFromLocation = true
                    loadCorridas()
                }
            }
        }
    }

    fun loadCorridas() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val location = locationRepository.getCurrentOrLastKnownLocation()
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
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        corridas = emptyList(),
                        erro = error.message ?: "Erro ao carregar corridas"
                    )
                }
            }
        }
    }

    fun toggleViewMode() {
        _uiState.update {
            it.copy(
                viewMode = if (it.viewMode == CorridasViewMode.LISTA) {
                    CorridasViewMode.MAPA
                } else {
                    CorridasViewMode.LISTA
                }
            )
        }
    }

    fun limparErro() {
        _uiState.update { it.copy(erro = null) }
    }
}

private const val LOCATION_EPSILON = 0.00001
