package com.pyloto.entregador.presentation.auth.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pyloto.entregador.domain.usecase.auth.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
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
        _uiState.update { it.copy(email = email, emailError = null) }
    }

    fun onSenhaChange(senha: String) {
        _uiState.update { it.copy(senha = senha, senhaError = null) }
    }

    fun login() {
        val state = _uiState.value
        if (state.email.isBlank()) {
            _uiState.update { it.copy(emailError = "Email é obrigatório") }
            return
        }
        if (state.senha.isBlank()) {
            _uiState.update { it.copy(senhaError = "Senha é obrigatória") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            loginUseCase(state.email, state.senha)
                .onSuccess {
                    _events.emit(LoginEvent.LoginSuccess)
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Erro ao fazer login"
                        )
                    }
                }
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
