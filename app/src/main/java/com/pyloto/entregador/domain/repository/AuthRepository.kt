package com.pyloto.entregador.domain.repository

import com.pyloto.entregador.domain.model.AuthToken
import com.pyloto.entregador.domain.model.LoginCredentials
import com.pyloto.entregador.domain.model.RegisterData

interface AuthRepository {
    suspend fun login(credentials: LoginCredentials): AuthToken
    suspend fun register(data: RegisterData): AuthToken
    suspend fun refreshToken(): AuthToken
    suspend fun logout()
    suspend fun isLoggedIn(): Boolean
}
