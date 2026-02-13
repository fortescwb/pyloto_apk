package com.pyloto.entregador.domain.model

data class AuthToken(
    val accessToken: String,
    val refreshToken: String,
    val userId: String,
    val expiresIn: Long
)

data class LoginCredentials(
    val email: String,
    val senha: String
)

data class RegisterData(
    val nome: String,
    val email: String,
    val senha: String,
    val telefone: String,
    val cpf: String,
    val veiculoTipo: VeiculoTipo
)
