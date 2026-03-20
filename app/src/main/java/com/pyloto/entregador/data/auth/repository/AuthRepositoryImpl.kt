package com.pyloto.entregador.data.auth.repository

import com.pyloto.entregador.core.network.ApiService
import com.pyloto.entregador.core.util.TokenManager
import com.pyloto.entregador.data.auth.remote.dto.LoginRequest
import com.pyloto.entregador.data.auth.remote.dto.RefreshTokenRequest
import com.pyloto.entregador.data.auth.remote.dto.RegisterRequest
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
        val response = apiService.login(
            LoginRequest(email = credentials.email, senha = credentials.senha)
        )
        val refreshToken = response.data.refreshToken?.ifBlank { response.data.accessToken }
            ?: response.data.accessToken

        val token = AuthToken(
            accessToken = response.data.accessToken,
            refreshToken = refreshToken,
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
        val refreshToken = response.data.refreshToken?.ifBlank { response.data.accessToken }
            ?: response.data.accessToken

        val token = AuthToken(
            accessToken = response.data.accessToken,
            refreshToken = refreshToken,
            userId = response.data.parceiro?.id ?: "",
            expiresIn = 3600L
        )
        tokenManager.saveTokens(token.accessToken, token.refreshToken)
        tokenManager.saveUserId(token.userId)
        return token
    }

    override suspend fun refreshToken(): AuthToken {
        val currentRefreshToken = tokenManager.getRefreshToken()
            ?.ifBlank { tokenManager.getAccessToken() }
            ?: throw IllegalStateException("No refresh token available")

        val response = apiService.refreshToken(
            RefreshTokenRequest(refreshToken = currentRefreshToken)
        )

        val newAccessToken = response.data.accessToken.ifBlank {
            throw IllegalStateException("Refresh endpoint retornou access_token vazio")
        }
        val newRefreshToken = response.data.refreshToken?.ifBlank { currentRefreshToken }
            ?: currentRefreshToken
        val userId = response.data.parceiro?.id ?: tokenManager.getUserId() ?: ""

        val token = AuthToken(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken,
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
}
