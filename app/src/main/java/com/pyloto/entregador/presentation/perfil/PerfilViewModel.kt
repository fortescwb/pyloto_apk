package com.pyloto.entregador.presentation.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pyloto.entregador.domain.model.Veiculo
import com.pyloto.entregador.domain.model.VeiculoTipo
import com.pyloto.entregador.domain.repository.PreferencesRepository
import com.pyloto.entregador.domain.usecase.auth.LogoutUseCase
import com.pyloto.entregador.domain.usecase.entregador.AtualizarPerfilUseCase
import com.pyloto.entregador.domain.usecase.entregador.ObterPerfilUseCase
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
class PerfilViewModel @Inject constructor(
    private val obterPerfilUseCase: ObterPerfilUseCase,
    private val atualizarPerfilUseCase: AtualizarPerfilUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PerfilUiState())
    val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<PerfilEvent>()
    val events: SharedFlow<PerfilEvent> = _events.asSharedFlow()

    init {
        observeWeeklyGoal()
        loadPerfil()
    }

    private fun observeWeeklyGoal() {
        viewModelScope.launch {
            preferencesRepository.observeWeeklyGoal().collect { goal ->
                _uiState.update { state -> state.copy(metaSemanal = goal) }
            }
        }
    }

    fun loadPerfil() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, erro = null) }
            runCatching {
                obterPerfilUseCase()
            }.onSuccess { entregador ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        entregador = entregador,
                        erro = null
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        erro = error.message ?: "Erro ao carregar perfil"
                    )
                }
            }
        }
    }

    fun atualizarDadosPessoais(nome: String, telefone: String) {
        val current = _uiState.value.entregador ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, erro = null) }
            runCatching {
                atualizarPerfilUseCase(current.copy(nome = nome, telefone = telefone))
            }.onSuccess { atualizado ->
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        entregador = atualizado,
                        erro = null
                    )
                }
                _events.emit(PerfilEvent.DadosSalvos)
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        erro = error.message ?: "Erro ao salvar dados"
                    )
                }
            }
        }
    }

    fun atualizarMetaSemanal(novaMetaSemanal: Double) {
        viewModelScope.launch {
            preferencesRepository.saveWeeklyGoal(novaMetaSemanal)
        }
    }

    fun atualizarVeiculo(tipo: VeiculoTipo, placa: String?) {
        val current = _uiState.value.entregador ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, erro = null) }
            runCatching {
                val novoVeiculo = Veiculo(tipo = tipo, placa = placa)
                atualizarPerfilUseCase(current.copy(veiculo = novoVeiculo))
            }.onSuccess { atualizado ->
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        entregador = atualizado,
                        erro = null
                    )
                }
                _events.emit(PerfilEvent.DadosSalvos)
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        erro = error.message ?: "Erro ao salvar veiculo"
                    )
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            logoutUseCase()
            _events.emit(PerfilEvent.LogoutRealizado)
        }
    }

    fun limparErro() {
        _uiState.update { it.copy(erro = null) }
    }
}
