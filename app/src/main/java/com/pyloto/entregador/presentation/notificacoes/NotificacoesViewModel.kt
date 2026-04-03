package com.pyloto.entregador.presentation.notificacoes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pyloto.entregador.domain.model.Notificacao
import com.pyloto.entregador.domain.repository.NotificacaoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NotificacoesUiState(
    val isLoading: Boolean = true,
    val notificacoes: List<Notificacao> = emptyList(),
    val unreadCount: Int = 0,
    val erro: String? = null
)

@HiltViewModel
class NotificacoesViewModel @Inject constructor(
    private val notificacaoRepository: NotificacaoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificacoesUiState())
    val uiState: StateFlow<NotificacoesUiState> = _uiState.asStateFlow()

    init {
        observeNotificacoes()
        observeUnreadCount()
        refresh()
    }

    private fun observeNotificacoes() {
        viewModelScope.launch {
            notificacaoRepository.observarNotificacoes().collect { list ->
                _uiState.update { state ->
                    state.copy(
                        notificacoes = list,
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun observeUnreadCount() {
        viewModelScope.launch {
            notificacaoRepository.observarNaoLidas().collect { count ->
                _uiState.update { it.copy(unreadCount = count) }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, erro = null) }
            runCatching {
                notificacaoRepository.sincronizar(page = 0, size = 30)
                val serverCount = notificacaoRepository.obterNaoLidasServidor()
                _uiState.update { it.copy(unreadCount = serverCount) }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        erro = error.message ?: "Erro ao carregar notificacoes"
                    )
                }
            }
        }
    }

    fun markAsRead(notificacaoId: String) {
        viewModelScope.launch {
            runCatching {
                notificacaoRepository.marcarComoLida(notificacaoId)
            }.onFailure { error ->
                _uiState.update { it.copy(erro = error.message ?: "Erro ao marcar leitura") }
            }
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            runCatching {
                notificacaoRepository.marcarTodasComoLidas()
            }.onFailure { error ->
                _uiState.update { it.copy(erro = error.message ?: "Erro ao marcar todas como lidas") }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(erro = null) }
    }
}
