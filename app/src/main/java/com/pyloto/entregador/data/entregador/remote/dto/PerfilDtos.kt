package com.pyloto.entregador.data.entregador.remote.dto

import com.google.gson.annotations.SerializedName

data class EntregadorPerfilResponse(
    @SerializedName("id") val id: String,
    @SerializedName("nome") val nome: String,
    @SerializedName("email") val email: String,
    @SerializedName("telefone") val telefone: String,
    @SerializedName("cpf") val cpf: String,
    @SerializedName(value = "foto_url", alternate = ["fotoUrl"]) val fotoUrl: String? = null,
    @SerializedName(value = "veiculo_tipo", alternate = ["veiculoTipo"]) val veiculoTipo: String? = null,
    @SerializedName(value = "veiculo_placa", alternate = ["veiculoPlaca", "placa"]) val veiculoPlaca: String? = null,
    @SerializedName(value = "nota_media", alternate = ["rating"]) val rating: Double = 5.0,
    @SerializedName(value = "total_corridas", alternate = ["totalCorridas"]) val totalCorridas: Int = 0,
    @SerializedName(value = "online", alternate = ["statusOnline"]) val statusOnline: Boolean = false
)

data class AtualizarPerfilRequest(
    @SerializedName("nome") val nome: String?,
    @SerializedName("telefone") val telefone: String?,
    @SerializedName("foto_url") val fotoUrl: String?,
    @SerializedName("tipo_veiculo") val veiculoTipo: String?,
    @SerializedName("placa") val veiculoPlaca: String?
)

data class StatusRequest(
    @SerializedName("disponivel") val disponivel: Boolean
)
