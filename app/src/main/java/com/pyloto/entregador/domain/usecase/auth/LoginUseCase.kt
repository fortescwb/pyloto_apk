package com.pyloto.entregador.domain.usecase.auth

import com.pyloto.entregador.domain.model.AuthToken
import com.pyloto.entregador.domain.model.LoginCredentials
import com.pyloto.entregador.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, senha: String): Result<AuthToken> {
        return try {
            if (email.isBlank() || senha.isBlank()) {
                return Result.failure(IllegalArgumentException("Email e senha são obrigatórios"))
            }
            val token = repository.login(LoginCredentials(email, senha))
            Result.success(token)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
