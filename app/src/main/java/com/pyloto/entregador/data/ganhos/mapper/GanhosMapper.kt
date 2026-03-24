package com.pyloto.entregador.data.ganhos.mapper

import com.pyloto.entregador.data.ganhos.remote.dto.GanhosResponse
import com.pyloto.entregador.domain.model.Ganhos
import com.pyloto.entregador.domain.model.GanhosDia
import com.pyloto.entregador.domain.model.GanhosExtratoItem
import com.pyloto.entregador.domain.model.GanhosFinanceiro
import com.pyloto.entregador.domain.model.GanhosPix
import com.pyloto.entregador.domain.model.GanhosSaldo
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
            },
            saldo = GanhosSaldo(
                pendente = BigDecimal.valueOf(response.saldo?.pendente ?: 0.0),
                disponivel = BigDecimal.valueOf(response.saldo?.disponivel ?: 0.0),
                transferido = BigDecimal.valueOf(response.saldo?.transferido ?: 0.0),
                falhou = BigDecimal.valueOf(response.saldo?.falhou ?: 0.0),
                mensalidadePendente = BigDecimal.valueOf(
                    response.saldo?.mensalidadePendente ?: 0.0
                )
            ),
            pix = GanhosPix(
                tipo = response.pix?.tipo.orEmpty(),
                chave = response.pix?.chave.orEmpty(),
                status = response.pix?.status.orEmpty(),
                recebimentoValido = response.pix?.recebimentoValido ?: false,
                pendencias = response.pix?.pendencias ?: emptyList()
            ),
            financeiro = GanhosFinanceiro(
                suspensaoAtiva = response.financeiro?.suspensaoAtiva ?: false,
                motivoSuspensao = response.financeiro?.motivoSuspensao.orEmpty(),
                inadimplenteDesde = response.financeiro?.inadimplenteDesde.orEmpty(),
                mensalidadesVencidas = response.financeiro?.mensalidadesVencidas ?: 0,
                proximaMensalidadeEm = response.financeiro?.proximaMensalidadeEm
            ),
            extrato = response.extrato.map { item ->
                GanhosExtratoItem(
                    id = item.id,
                    tipo = item.tipo,
                    grupo = item.grupo,
                    titulo = item.titulo,
                    descricao = item.descricao,
                    status = item.status,
                    valor = BigDecimal.valueOf(item.valor),
                    taxaPlataforma = BigDecimal.valueOf(item.taxaPlataforma),
                    ocorridoEm = item.ocorridoEm,
                    disponivelEm = item.disponivelEm,
                    pedidoId = item.pedidoId
                )
            }
        )
    }
}
