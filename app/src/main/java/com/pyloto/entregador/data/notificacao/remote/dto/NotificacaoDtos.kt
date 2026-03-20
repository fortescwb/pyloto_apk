package com.pyloto.entregador.data.notificacao.remote.dto

import com.google.gson.annotations.SerializedName

data class FCMTokenRequest(
    @SerializedName("token") val token: String,
    @SerializedName("platform") val platform: String = "ANDROID"
)

data class NotificacaoResponse(
    @SerializedName("id") val id: String,
    @SerializedName(value = "titulo", alternate = ["title"]) val titulo: String,
    @SerializedName(value = "corpo", alternate = ["message", "mensagem"]) val corpo: String,
    @SerializedName("tipo") val tipo: String,
    @SerializedName("dados") val dados: Map<String, String>? = null,
    @SerializedName(value = "timestamp", alternate = ["created_at"]) val timestamp: Double
)
