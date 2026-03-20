package com.pyloto.entregador.data.chat.remote.dto

import com.google.gson.annotations.SerializedName

data class MensagemResponse(
    @SerializedName("id") val id: String? = null,
    @SerializedName(value = "message_id", alternate = ["messageId"]) val messageId: String? = null,
    @SerializedName(value = "corrida_id", alternate = ["corridaId"]) val corridaId: String? = null,
    @SerializedName(value = "remetente_id", alternate = ["remetenteId"]) val remetenteId: String? = null,
    @SerializedName(value = "remetente_tipo", alternate = ["remetenteTipo"]) val remetenteTipo: String? = null,
    @SerializedName("conteudo") val conteudo: String? = null,
    @SerializedName(value = "tipo_mensagem", alternate = ["tipoMensagem", "tipo"]) val tipoMensagem: String? = null,
    @SerializedName("timestamp") val timestamp: Double? = null
)

data class EnviarMensagemRequest(
    @SerializedName("conteudo") val conteudo: String,
    @SerializedName("tipo") val tipo: String = "texto"
)
