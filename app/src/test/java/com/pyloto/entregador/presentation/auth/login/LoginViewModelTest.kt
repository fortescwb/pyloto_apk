package com.pyloto.entregador.presentation.auth.login

import com.pyloto.entregador.domain.model.AuthToken
import com.pyloto.entregador.domain.usecase.auth.LoginUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

/**
 * Testes unitários do LoginViewModel.
 *
 * Valida:
 *   - Validação local de campos (email, senha)
 *   - Fluxo de sucesso → LoginEvent.LoginSuccess emitido
 *   - Fluxo de erro → mensagem amigável no uiState
 *   - Estado de loading durante a chamada
 */
@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private lateinit var loginUseCase: LoginUseCase
    private lateinit var viewModel: LoginViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        loginUseCase = mock()
        viewModel = LoginViewModel(loginUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // ─── Validação de campos ──────────────────────────────────────

    @Test
    fun `login com email vazio mostra erro de email`() {
        viewModel.onEmailChange("")
        viewModel.onSenhaChange("123456")

        viewModel.login()

        val state = viewModel.uiState.value
        assertEquals("Email é obrigatório", state.emailError)
        assertNull(state.senhaError)
        assertFalse(state.isLoading)
    }

    @Test
    fun `login com email invalido mostra erro de formato`() {
        viewModel.onEmailChange("emailinvalido")
        viewModel.onSenhaChange("123456")

        viewModel.login()

        val state = viewModel.uiState.value
        assertEquals("Formato de email inválido", state.emailError)
    }

    @Test
    fun `login com senha vazia mostra erro de senha`() {
        viewModel.onEmailChange("teste@pyloto.com")
        viewModel.onSenhaChange("")

        viewModel.login()

        val state = viewModel.uiState.value
        assertNull(state.emailError)
        assertEquals("Senha é obrigatória", state.senhaError)
    }

    @Test
    fun `login com senha curta mostra erro de tamanho`() {
        viewModel.onEmailChange("teste@pyloto.com")
        viewModel.onSenhaChange("123")

        viewModel.login()

        val state = viewModel.uiState.value
        assertEquals("Senha deve ter pelo menos 6 caracteres", state.senhaError)
    }

    @Test
    fun `login com ambos campos vazios mostra ambos erros`() {
        viewModel.onEmailChange("")
        viewModel.onSenhaChange("")

        viewModel.login()

        val state = viewModel.uiState.value
        assertNotNull(state.emailError)
        assertNotNull(state.senhaError)
    }

    // ─── Fluxo de sucesso ─────────────────────────────────────────

    @Test
    fun `login com credenciais validas emite LoginSuccess`() = runTest {
        val token = AuthToken(
            accessToken = "jwt-token",
            refreshToken = "refresh-token",
            userId = "user-123",
            expiresIn = 3600
        )
        whenever(loginUseCase.invoke(any(), any())).thenReturn(Result.success(token))

        viewModel.onEmailChange("teste@pyloto.com")
        viewModel.onSenhaChange("senha123")

        val events = mutableListOf<LoginEvent>()
        val job = launch {
            viewModel.events.collect { events.add(it) }
        }

        viewModel.login()

        // Aguarda propagação
        advanceUntilIdle()

        assertTrue(events.any { it is LoginEvent.LoginSuccess })
        assertFalse(viewModel.uiState.value.isLoading)
        job.cancel()
    }

    // ─── Fluxo de erro ────────────────────────────────────────────

    @Test
    fun `login com erro de rede mostra mensagem amigavel`() = runTest {
        whenever(loginUseCase.invoke(any(), any()))
            .thenReturn(Result.failure(java.net.UnknownHostException("No host")))

        viewModel.onEmailChange("teste@pyloto.com")
        viewModel.onSenhaChange("senha123")

        viewModel.login()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.error)
        assertTrue(state.error!!.contains("conexão"))
    }

    @Test
    fun `login com credenciais incorretas mostra mensagem de erro`() = runTest {
        whenever(loginUseCase.invoke(any(), any()))
            .thenReturn(Result.failure(Exception("401 Unauthorized - Invalid credentials")))

        viewModel.onEmailChange("teste@pyloto.com")
        viewModel.onSenhaChange("senhaerrada")

        viewModel.login()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.error)
    }

    // ─── Estado de loading ────────────────────────────────────────

    @Test
    fun `digitar email limpa erro anterior`() {
        viewModel.onEmailChange("")
        viewModel.login() // Gera erro

        viewModel.onEmailChange("t") // Digita algo

        assertNull(viewModel.uiState.value.emailError)
        assertNull(viewModel.uiState.value.error) // Erro geral também limpo
    }

    @Test
    fun `digitar senha limpa erro anterior`() {
        viewModel.onSenhaChange("")
        viewModel.onEmailChange("a@b.com")
        viewModel.login() // Gera erro

        viewModel.onSenhaChange("x") // Digita algo

        assertNull(viewModel.uiState.value.senhaError)
    }
}
