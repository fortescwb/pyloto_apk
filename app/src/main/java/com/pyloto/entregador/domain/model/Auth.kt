package com.pyloto.entregador.domain.model

data class AuthToken(
    val accessToken: String,
    val refreshToken: String,
    val userId: String,
    val expiresIn: Long,
    val requiresDigitalContractSignature: Boolean = false
)

data class LoginCredentials(
    val email: String,
    val senha: String
)
