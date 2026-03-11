package com.pyloto.entregador.presentation.corrida.disponivel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pyloto.entregador.domain.model.Corrida
import com.pyloto.entregador.domain.repository.CorridaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CorridaDetalhesUiState(
    val isLoading: Boolean = true,
    val isAceitando: Boolean = false,
    val corrida: Corrida? = null,
    val erro: String? = null,
    val aceitaComSucesso: Boolean = false
)

@HiltViewModel
class CorridaDetalhesViewModel @Inject constructor(
    private val corridaRepository: CorridaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CorridaDetalhesUiState())
    val uiState: StateFlow<CorridaDetalhesUiState> = _uiState.asStateFlow()

    fun loadCorrida(corridaId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, erro = null) }
            try {
                val corrida = corridaRepository.getCorridaDetalhes(corridaId)
                _uiState.update { it.copy(isLoading = false, corrida = corrida) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        erro = e.message ?: "Erro ao carregar detalhes da corrida"
                    )
                }
            }
        }
    }

    fun aceitarCorrida(corridaId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isAceitando = true, erro = null) }
            try {
                corridaRepository.aceitarCorrida(corridaId)
                _uiState.update { it.copy(isAceitando = false, aceitaComSucesso = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isAceitando = false,
                        erro = e.message ?: "Erro ao aceitar corrida"
                    )
                }
            }
        }
    }

    fun limparErro() {
        _uiState.update { it.copy(erro = null) }
    }
}
