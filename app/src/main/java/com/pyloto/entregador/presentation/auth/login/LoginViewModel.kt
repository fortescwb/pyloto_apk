package com.pyloto.entregador.presentation.auth.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pyloto.entregador.domain.usecase.auth.LoginUseCase
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

        var hasError = false

        if (state.email.isBlank()) {
            _uiState.update { it.copy(emailError = "Email e obrigatorio") }
            hasError = true
        } else if (!Patterns.EMAIL_ADDRESS.matcher(state.email.trim()).matches()) {
            _uiState.update { it.copy(emailError = "Formato de email invalido") }
            hasError = true
        }

        if (state.senha.isBlank()) {
            _uiState.update { it.copy(senhaError = "Senha e obrigatoria") }
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
                    _events.emit(
                        LoginEvent.LoginSuccess(requiresOnboarding = false)
                    )
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = mapErrorMessage(error)
                        )
                    }
                }
        }
    }

    private fun mapErrorMessage(error: Throwable): String {
        return when {
            error is java.net.UnknownHostException ||
                error is java.net.ConnectException ->
                "Sem conexao com a internet. Verifique sua rede e tente novamente."

            error is java.net.SocketTimeoutException ->
                "Servidor demorou para responder. Tente novamente."

            error.message?.contains("401") == true ||
                error.message?.contains("credentials") == true ->
                "Email ou senha incorretos."

            error.message?.contains("403") == true ->
                "Conta bloqueada ou sem liberacao operacional."

            error.message?.contains("429") == true ->
                "Muitas tentativas. Aguarde um momento e tente novamente."

            else ->
                error.message ?: "Erro ao fazer login. Tente novamente."
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
    data class LoginSuccess(
        val requiresOnboarding: Boolean
    ) : LoginEvent()
}
