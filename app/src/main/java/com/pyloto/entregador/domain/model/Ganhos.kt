package com.pyloto.entregador.domain.model

import java.math.BigDecimal

data class Ganhos(
    val periodo: String,
    val totalBruto: BigDecimal,
    val totalLiquido: BigDecimal,
    val totalCorridas: Int,
    val mediaValorCorrida: BigDecimal,
    val corridasPorDia: Map<String, GanhosDia>,
    val saldo: GanhosSaldo = GanhosSaldo(),
    val pix: GanhosPix = GanhosPix(),
    val financeiro: GanhosFinanceiro = GanhosFinanceiro(),
    val extrato: List<GanhosExtratoItem> = emptyList()
)

data class GanhosDia(
    val data: String,
    val valor: BigDecimal,
    val quantidadeCorridas: Int
)

data class GanhosSaldo(
    val pendente: BigDecimal = BigDecimal.ZERO,
    val disponivel: BigDecimal = BigDecimal.ZERO,
    val transferido: BigDecimal = BigDecimal.ZERO,
    val falhou: BigDecimal = BigDecimal.ZERO,
    val mensalidadePendente: BigDecimal = BigDecimal.ZERO
)

data class GanhosPix(
    val tipo: String = "",
    val chave: String = "",
    val status: String = "",
    val recebimentoValido: Boolean = false,
    val pendencias: List<String> = emptyList()
)

data class GanhosFinanceiro(
    val suspensaoAtiva: Boolean = false,
    val motivoSuspensao: String = "",
    val inadimplenteDesde: String = "",
    val mensalidadesVencidas: Int = 0,
    val proximaMensalidadeEm: String? = null
)

data class GanhosExtratoItem(
    val id: String,
    val tipo: String,
    val grupo: String,
    val titulo: String,
    val descricao: String,
    val status: String,
    val valor: BigDecimal,
    val taxaPlataforma: BigDecimal = BigDecimal.ZERO,
    val ocorridoEm: String = "",
    val disponivelEm: String? = null,
    val pedidoId: String? = null
)
