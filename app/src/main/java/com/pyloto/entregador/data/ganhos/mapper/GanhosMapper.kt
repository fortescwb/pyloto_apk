package com.pyloto.entregador.data.ganhos.mapper

import com.pyloto.entregador.data.ganhos.remote.dto.GanhosResponse
import com.pyloto.entregador.domain.model.Ganhos
import com.pyloto.entregador.domain.model.GanhosDia
import java.math.BigDecimal
import javax.inject.Inject

class GanhosMapper @Inject constructor() {

    fun toDomain(response: GanhosResponse): Ganhos {
        return Ganhos(
            periodo = response.periodo,
            totalBruto = BigDecimal.valueOf(response.totalBruto),
            totalLiquido = BigDecimal.valueOf(response.totalLiquido),
            totalCorridas = response.totalCorridas,
            mediaValorCorrida = BigDecimal.valueOf(response.mediaValorCorrida),
            corridasPorDia = response.corridasPorDia.mapValues { (_, dia) ->
                GanhosDia(
                    data = dia.data,
                    valor = BigDecimal.valueOf(dia.valor),
                    quantidadeCorridas = dia.quantidadeCorridas
                )
            }
        )
    }
}
