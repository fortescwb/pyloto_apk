package com.pyloto.entregador.domain.usecase.auth

import com.pyloto.entregador.domain.model.AuthToken
import com.pyloto.entregador.domain.model.LoginCredentials
import com.pyloto.entregador.domain.repository.AuthRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

/**
 * Testes unitários do LoginUseCase.
 *
 * Valida:
 *   - Campos obrigatórios (email + senha)
 *   - Chamada correta ao repository
 *   - Propagação de sucesso e exceções
 */
class LoginUseCaseTest {

    private lateinit var repository: AuthRepository
    private lateinit var useCase: LoginUseCase

    @Before
    fun setup() {
        repository = mock()
        useCase = LoginUseCase(repository)
    }

    @Test
    fun `email vazio retorna failure`() = runTest {
        val result = useCase("", "senha123")

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        verify(repository, never()).login(any())
    }

    @Test
    fun `senha vazia retorna failure`() = runTest {
        val result = useCase("teste@pyloto.com", "")

        assertTrue(result.isFailure)
        verify(repository, never()).login(any())
    }

    @Test
    fun `credenciais validas chama repository e retorna token`() = runTest {
        val expectedToken = AuthToken(
            accessToken = "jwt",
            refreshToken = "refresh",
            userId = "id-1",
            expiresIn = 3600
        )
        whenever(repository.login(any())).thenReturn(expectedToken)

        val result = useCase("teste@pyloto.com", "senha123")

        assertTrue(result.isSuccess)
        assertEquals(expectedToken, result.getOrNull())
        verify(repository).login(LoginCredentials("teste@pyloto.com", "senha123"))
    }

    @Test
    fun `erro do repository e propagado como failure`() = runTest {
        whenever(repository.login(any()))
            .thenThrow(RuntimeException("Network error"))

        val result = useCase("teste@pyloto.com", "senha123")

        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }
}
