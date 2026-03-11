package com.pyloto.entregador.data.repository

import com.pyloto.entregador.BuildConfig
import com.pyloto.entregador.core.network.ApiService
import com.pyloto.entregador.core.util.TokenManager
import com.pyloto.entregador.data.remote.model.LoginRequest
import com.pyloto.entregador.data.remote.model.RefreshTokenRequest
import com.pyloto.entregador.data.remote.model.RegisterRequest
import com.pyloto.entregador.domain.model.AuthToken
import com.pyloto.entregador.domain.model.LoginCredentials
import com.pyloto.entregador.domain.model.RegisterData
import com.pyloto.entregador.domain.repository.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun login(credentials: LoginCredentials): AuthToken {
        if (BuildConfig.DEBUG && isHardcodedDebugCredentials(credentials)) {
            return provideHardcodedDebugToken()
        }

        val response = apiService.login(
            LoginRequest(email = credentials.email, senha = credentials.senha)
        )
        val token = AuthToken(
            accessToken = response.data.accessToken,
            refreshToken = "",
            userId = response.data.parceiro?.id ?: "",
            expiresIn = 3600L
        )
        tokenManager.saveTokens(token.accessToken, token.refreshToken)
        tokenManager.saveUserId(token.userId)
        return token
    }

    override suspend fun register(data: RegisterData): AuthToken {
        val response = apiService.register(
            RegisterRequest(
                nome = data.nome,
                email = data.email,
                senha = data.senha,
                telefone = data.telefone,
                cpf = data.cpf,
                veiculoTipo = data.veiculoTipo.name
            )
        )
        val token = AuthToken(
            accessToken = response.data.accessToken,
            refreshToken = "",
            userId = response.data.parceiro?.id ?: "",
            expiresIn = 3600L
        )
        tokenManager.saveTokens(token.accessToken, token.refreshToken)
        tokenManager.saveUserId(token.userId)
        return token
    }

    override suspend fun refreshToken(): AuthToken {
        val currentRefreshToken = tokenManager.getRefreshToken()
            ?: throw IllegalStateException("No refresh token available")

        val response = apiService.refreshToken(
            RefreshTokenRequest(refreshToken = currentRefreshToken)
        )
        // Backend stub: retorna access_token vazio; preservar token atual se resposta inválida
        val newAccessToken = response.data.accessToken.ifBlank {
            tokenManager.getAccessToken() ?: throw IllegalStateException("Token inválido")
        }
        val userId = response.data.parceiro?.id ?: tokenManager.getUserId() ?: ""
        val token = AuthToken(
            accessToken = newAccessToken,
            refreshToken = currentRefreshToken,
            userId = userId,
            expiresIn = 3600L
        )
        tokenManager.saveTokens(token.accessToken, token.refreshToken)
        return token
    }

    override suspend fun logout() {
        try {
            apiService.logout()
        } catch (_: Exception) {
            // Ignora erro de rede no logout
        } finally {
            tokenManager.clearTokens()
        }
    }

    override suspend fun isLoggedIn(): Boolean {
        return tokenManager.isLoggedIn()
    }

    private suspend fun provideHardcodedDebugToken(): AuthToken {
        val token = AuthToken(
            accessToken = "debug-access-token",
            refreshToken = "debug-refresh-token",
            userId = "debug-entregador",
            expiresIn = 31_536_000L
        )
        tokenManager.saveTokens(token.accessToken, token.refreshToken)
        tokenManager.saveUserId(token.userId)
        return token
    }

    private fun isHardcodedDebugCredentials(credentials: LoginCredentials): Boolean {
        val email = credentials.email.trim()
        val senha = credentials.senha.trim()
        return email.equals(DEBUG_TEST_EMAIL, ignoreCase = true) && senha == DEBUG_TEST_PASSWORD
    }

    companion object {
        const val DEBUG_TEST_EMAIL = "teste@pyloto.com"
        const val DEBUG_TEST_PASSWORD = "senha123"
    }
}
