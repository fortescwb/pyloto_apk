package com.pyloto.entregador.data.auth.remote.dto

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("senha") val senha: String
)

data class RefreshTokenRequest(
    @SerializedName("refresh_token") val refreshToken: String
)

/**
 * DTO do token retornado pelo backend.
 * O backend retorna snake_case e inclui o objeto parceiro.
 */
data class AuthToken(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("refresh_token") val refreshToken: String? = null,
    @SerializedName("token_type") val tokenType: String = "bearer",
    @SerializedName("parceiro") val parceiro: ParceiroLoginDto? = null,
    @SerializedName("requires_digital_contract_signature")
    val requiresDigitalContractSignature: Boolean = false
)

data class ParceiroLoginDto(
    @SerializedName("id") val id: String,
    @SerializedName("nome") val nome: String? = null,
    @SerializedName("email") val email: String? = null
)
