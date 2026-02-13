package com.pyloto.entregador.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pyloto.entregador.domain.model.Corrida
import com.pyloto.entregador.domain.usecase.corrida.AceitarCorridaUseCase
import com.pyloto.entregador.domain.usecase.corrida.ObterCorridasDisponiveisUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val obterCorridasUseCase: ObterCorridasDisponiveisUseCase,
    private val aceitarCorridaUseCase: AceitarCorridaUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<HomeEvent>()
    val events: SharedFlow<HomeEvent> = _events.asSharedFlow()

    init {
        loadCorridas()
    }

    fun loadCorridas() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            obterCorridasUseCase()
                .catch { e ->
                    _uiState.value = HomeUiState.Error(e.message ?: "Erro desconhecido")
                }
                .collect { corridas ->
                    _uiState.value = if (corridas.isEmpty()) {
                        HomeUiState.Empty
                    } else {
                        HomeUiState.Success(corridas)
                    }
                }
        }
    }

    fun aceitarCorrida(corridaId: String) {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            aceitarCorridaUseCase(corridaId)
                .onSuccess {
                    _events.emit(HomeEvent.CorridaAceita(corridaId))
                }
                .onFailure { e ->
                    _uiState.value = HomeUiState.Error(e.message ?: "Erro ao aceitar corrida")
                }
        }
    }
}

sealed class HomeUiState {
    object Loading : HomeUiState()
    object Empty : HomeUiState()
    data class Success(val corridas: List<Corrida>) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

sealed class HomeEvent {
    data class CorridaAceita(val corridaId: String) : HomeEvent()
}
