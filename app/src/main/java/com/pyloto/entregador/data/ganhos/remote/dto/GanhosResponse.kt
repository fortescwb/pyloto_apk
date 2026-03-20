package com.pyloto.entregador.data.ganhos.remote.dto

import com.google.gson.annotations.SerializedName

data class GanhosResponse(
    @SerializedName("periodo") val periodo: String,
    @SerializedName(value = "totalBruto", alternate = ["total_bruto"]) val totalBruto: Double,
    @SerializedName(value = "totalLiquido", alternate = ["total_liquido"]) val totalLiquido: Double,
    @SerializedName(value = "totalCorridas", alternate = ["total_corridas"]) val totalCorridas: Int,
    @SerializedName(value = "mediaValorCorrida", alternate = ["media_valor_corrida"]) val mediaValorCorrida: Double,
    @SerializedName(value = "corridasPorDia", alternate = ["corridas_por_dia"]) val corridasPorDia: Map<String, GanhosDiaResponse>
)

data class GanhosDiaResponse(
    @SerializedName("data") val data: String,
    @SerializedName("valor") val valor: Double,
    @SerializedName(value = "quantidadeCorridas", alternate = ["quantidade_corridas"]) val quantidadeCorridas: Int
)
