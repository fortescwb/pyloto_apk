package com.pyloto.entregador.presentation.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pyloto.entregador.domain.model.Entregador
import com.pyloto.entregador.domain.model.Veiculo
import com.pyloto.entregador.domain.model.VeiculoTipo
import com.pyloto.entregador.domain.repository.AuthRepository
import com.pyloto.entregador.domain.repository.EntregadorRepository
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
    private val entregadorRepository: EntregadorRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PerfilUiState())
    val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<PerfilEvent>()
    val events: SharedFlow<PerfilEvent> = _events.asSharedFlow()

    init {
        loadPerfil()
    }

    /**
     * Carrega as informações do perfil do entregador.
     * TODO: Substituir scaffold por chamada real ao repositório.
     */
    fun loadPerfil() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // TODO: Descomentar quando o repositório estiver pronto
                // val entregador = entregadorRepository.getPerfil()
                // _uiState.update { it.copy(isLoading = false, entregador = entregador) }

                // Scaffold/Placeholder — dados mock
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        entregador = Entregador(
                            id = "entregador-001",
                            nome = "João da Silva",
                            email = "joao.silva@email.com",
                            telefone = "(42) 99999-8888",
                            cpf = "123.456.789-00",
                            fotoUrl = null,
                            veiculo = Veiculo(
                                tipo = VeiculoTipo.MOTO,
                                placa = "ABC-1D23"
                            ),
                            rating = 4.8,
                            totalCorridas = 342,
                            statusOnline = false
                        ),
                        metaSemanal = 1500.0
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        erro = e.message ?: "Erro ao carregar perfil"
                    )
                }
            }
        }
    }

    /**
     * Atualiza campos editáveis do perfil.
     * Campos não editáveis: CPF, email, ID.
     * TODO: Sincronizar com o backend.
     */
    fun atualizarDadosPessoais(nome: String, telefone: String) {
        val current = _uiState.value.entregador ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                val atualizado = current.copy(nome = nome, telefone = telefone)
                // TODO: entregadorRepository.atualizarPerfil(atualizado)
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        entregador = atualizado
                    )
                }
                _events.emit(PerfilEvent.DadosSalvos)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        erro = e.message ?: "Erro ao salvar dados"
                    )
                }
            }
        }
    }

    /**
     * Atualiza a meta semanal do entregador.
     * TODO: Persistir em DataStore ou backend.
     */
    fun atualizarMetaSemanal(novaMetaSemanal: Double) {
        _uiState.update { it.copy(metaSemanal = novaMetaSemanal) }
        // TODO: preferencesRepository.setWeeklyGoal(novaMetaSemanal)
    }

    /**
     * Atualiza informações do veículo.
     * TODO: Sincronizar com o backend.
     */
    fun atualizarVeiculo(tipo: VeiculoTipo, placa: String?) {
        val current = _uiState.value.entregador ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            try {
                val novoVeiculo = Veiculo(tipo = tipo, placa = placa)
                val atualizado = current.copy(veiculo = novoVeiculo)
                // TODO: entregadorRepository.atualizarPerfil(atualizado)
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        entregador = atualizado
                    )
                }
                _events.emit(PerfilEvent.DadosSalvos)
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        erro = e.message ?: "Erro ao salvar veículo"
                    )
                }
            }
        }
    }

    /**
     * Faz logout do entregador.
     */
    fun logout() {
        viewModelScope.launch {
            try {
                authRepository.logout()
            } catch (_: Exception) {
                // Ignora erros de logout — navega mesmo assim
            }
            _events.emit(PerfilEvent.LogoutRealizado)
        }
    }

    fun limparErro() {
        _uiState.update { it.copy(erro = null) }
    }
}

// ═══════════════════════════════════════════════════════════════
// UI STATE
// ═══════════════════════════════════════════════════════════════

data class PerfilUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val entregador: Entregador? = null,
    val metaSemanal: Double = 1500.0,
    val erro: String? = null
)

// ═══════════════════════════════════════════════════════════════
// EVENTS
// ═══════════════════════════════════════════════════════════════

sealed class PerfilEvent {
    data object DadosSalvos : PerfilEvent()
    data object LogoutRealizado : PerfilEvent()
}
