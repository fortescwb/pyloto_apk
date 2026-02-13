package com.pyloto.entregador.domain.model

import java.math.BigDecimal

data class Ganhos(
    val periodo: String,
    val totalBruto: BigDecimal,
    val totalLiquido: BigDecimal,
    val totalCorridas: Int,
    val mediaValorCorrida: BigDecimal,
    val corridasPorDia: Map<String, GanhosDia>
)

data class GanhosDia(
    val data: String,
    val valor: BigDecimal,
    val quantidadeCorridas: Int
)
