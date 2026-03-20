package com.pyloto.entregador.presentation.corridas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pyloto.entregador.domain.repository.CorridaRepository
import com.pyloto.entregador.domain.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

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
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
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

