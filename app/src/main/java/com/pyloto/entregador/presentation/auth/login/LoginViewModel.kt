package com.pyloto.entregador.presentation.auth.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pyloto.entregador.domain.usecase.auth.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel da tela de Login.
 *
 * Responsabilidades:
 *   - Validação local de campos (email formato, senha mínima)
 *   - Chamada ao [LoginUseCase] e tratamento de resultado
 *   - Emissão de evento one-shot [LoginEvent.LoginSuccess] para navegação
 *
 * Erro de rede é mapeado para mensagem amigável em pt-BR.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<LoginEvent>()
    val events: SharedFlow<LoginEvent> = _events.asSharedFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, emailError = null, error = null) }
    }

    fun onSenhaChange(senha: String) {
        _uiState.update { it.copy(senha = senha, senhaError = null, error = null) }
    }

    fun login() {
        val state = _uiState.value

        // Validação local antes de chamar a API
        var hasError = false

        if (state.email.isBlank()) {
            _uiState.update { it.copy(emailError = "Email é obrigatório") }
            hasError = true
        } else if (!Patterns.EMAIL_ADDRESS.matcher(state.email.trim()).matches()) {
            _uiState.update { it.copy(emailError = "Formato de email inválido") }
            hasError = true
        }

        if (state.senha.isBlank()) {
            _uiState.update { it.copy(senhaError = "Senha é obrigatória") }
            hasError = true
        } else if (state.senha.length < 6) {
            _uiState.update { it.copy(senhaError = "Senha deve ter pelo menos 6 caracteres") }
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            loginUseCase(state.email.trim(), state.senha)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.emit(LoginEvent.LoginSuccess)
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = mapErrorMessage(e)
                        )
                    }
                }
        }
    }

    /**
     * Mapeia exceções para mensagens amigáveis ao usuário.
     */
    private fun mapErrorMessage(e: Throwable): String {
        return when {
            e is java.net.UnknownHostException ||
                e is java.net.ConnectException ->
                "Sem conexão com a internet. Verifique sua rede e tente novamente."

            e is java.net.SocketTimeoutException ->
                "Servidor demorou para responder. Tente novamente."

            e.message?.contains("401") == true ||
                e.message?.contains("credentials") == true ->
                "Email ou senha incorretos."

            e.message?.contains("403") == true ->
                "Conta bloqueada. Entre em contato com o suporte."

            e.message?.contains("429") == true ->
                "Muitas tentativas. Aguarde um momento e tente novamente."

            else ->
                e.message ?: "Erro ao fazer login. Tente novamente."
        }
    }
}

data class LoginUiState(
    val email: String = "",
    val senha: String = "",
    val emailError: String? = null,
    val senhaError: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class LoginEvent {
    object LoginSuccess : LoginEvent()
}
