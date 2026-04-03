package com.pyloto.entregador.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pyloto.entregador.domain.model.Mensagem
import com.pyloto.entregador.domain.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChatUiState(
    val corridaId: String = "",
    val mensagens: List<Mensagem> = emptyList(),
    val draft: String = "",
    val isLoading: Boolean = false,
    val isSending: Boolean = false,
    val unreadCount: Int = 0,
    val erro: String? = null
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun initialize(corridaId: String) {
        if (corridaId.isBlank()) {
            _uiState.update { it.copy(erro = "Corrida invalida para o chat") }
            return
        }

        if (_uiState.value.corridaId == corridaId && _uiState.value.mensagens.isNotEmpty()) {
            return
        }

        _uiState.update { it.copy(corridaId = corridaId, isLoading = true, erro = null) }

        observeMensagens(corridaId)
        refreshMensagens()
        markAsRead()
    }

    private fun observeMensagens(corridaId: String) {
        viewModelScope.launch {
            chatRepository.observarMensagens(corridaId).collect { mensagens ->
                _uiState.update { state ->
                    state.copy(
                        mensagens = mensagens,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun refreshMensagens() {
        val corridaId = _uiState.value.corridaId
        if (corridaId.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, erro = null) }
            runCatching {
                chatRepository.getMensagensPaginadas(corridaId, page = 0)
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        erro = error.message ?: "Erro ao atualizar mensagens"
                    )
                }
            }
        }
    }

    fun updateDraft(value: String) {
        _uiState.update { it.copy(draft = value) }
    }

    fun enviarMensagem() {
        val corridaId = _uiState.value.corridaId
        val draft = _uiState.value.draft.trim()
        if (corridaId.isBlank() || draft.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSending = true, erro = null) }
            runCatching {
                chatRepository.enviarMensagem(corridaId, draft, tipo = "texto")
            }.onSuccess {
                _uiState.update { it.copy(isSending = false, draft = "") }
                refreshMensagens()
                markAsRead()
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isSending = false,
                        erro = error.message ?: "Erro ao enviar mensagem"
                    )
                }
            }
        }
    }

    fun markAsRead() {
        val corridaId = _uiState.value.corridaId
        if (corridaId.isBlank()) return

        viewModelScope.launch {
            runCatching {
                chatRepository.marcarComoLidas(corridaId)
                chatRepository.obterNaoLidasServidor(corridaId)
            }.onSuccess { unreadCount ->
                _uiState.update { it.copy(unreadCount = unreadCount) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(erro = null) }
    }
}
