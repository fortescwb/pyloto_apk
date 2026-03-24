package com.pyloto.entregador.data.ganhos.remote.dto

import com.google.gson.annotations.SerializedName

data class GanhosResponse(
    @SerializedName("periodo") val periodo: String,
    @SerializedName(value = "totalBruto", alternate = ["total_bruto"]) val totalBruto: Double,
    @SerializedName(value = "totalLiquido", alternate = ["total_liquido"]) val totalLiquido: Double,
    @SerializedName(value = "totalCorridas", alternate = ["total_corridas"]) val totalCorridas: Int,
    @SerializedName(value = "mediaValorCorrida", alternate = ["media_valor_corrida"]) val mediaValorCorrida: Double,
    @SerializedName(value = "corridasPorDia", alternate = ["corridas_por_dia"]) val corridasPorDia: Map<String, GanhosDiaResponse>,
    @SerializedName("saldo") val saldo: GanhosSaldoResponse? = null,
    @SerializedName("pix") val pix: GanhosPixResponse? = null,
    @SerializedName("financeiro") val financeiro: GanhosFinanceiroResponse? = null,
    @SerializedName("extrato") val extrato: List<GanhosExtratoItemResponse> = emptyList()
)

data class GanhosDiaResponse(
    @SerializedName("data") val data: String,
    @SerializedName("valor") val valor: Double,
    @SerializedName(value = "quantidadeCorridas", alternate = ["quantidade_corridas"]) val quantidadeCorridas: Int
)

data class GanhosSaldoResponse(
    @SerializedName("pendente") val pendente: Double = 0.0,
    @SerializedName("disponivel") val disponivel: Double = 0.0,
    @SerializedName("transferido") val transferido: Double = 0.0,
    @SerializedName("falhou") val falhou: Double = 0.0,
    @SerializedName(value = "mensalidade_pendente", alternate = ["mensalidadePendente"]) val mensalidadePendente: Double = 0.0
)

data class GanhosPixResponse(
    @SerializedName("tipo") val tipo: String = "",
    @SerializedName("chave") val chave: String = "",
    @SerializedName("status") val status: String = "",
    @SerializedName(value = "recebimento_valido", alternate = ["recebimentoValido"]) val recebimentoValido: Boolean = false,
    @SerializedName("pendencias") val pendencias: List<String> = emptyList()
)

data class GanhosFinanceiroResponse(
    @SerializedName(value = "suspensao_ativa", alternate = ["suspensaoAtiva"]) val suspensaoAtiva: Boolean = false,
    @SerializedName(value = "motivo_suspensao", alternate = ["motivoSuspensao"]) val motivoSuspensao: String = "",
    @SerializedName(value = "inadimplente_desde", alternate = ["inadimplenteDesde"]) val inadimplenteDesde: String = "",
    @SerializedName(value = "mensalidades_vencidas", alternate = ["mensalidadesVencidas"]) val mensalidadesVencidas: Int = 0,
    @SerializedName(value = "proxima_mensalidade_em", alternate = ["proximaMensalidadeEm"]) val proximaMensalidadeEm: String? = null
)

data class GanhosExtratoItemResponse(
    @SerializedName("id") val id: String,
    @SerializedName("tipo") val tipo: String,
    @SerializedName("grupo") val grupo: String,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("descricao") val descricao: String,
    @SerializedName("status") val status: String,
    @SerializedName("valor") val valor: Double,
    @SerializedName(value = "taxa_plataforma", alternate = ["taxaPlataforma"]) val taxaPlataforma: Double = 0.0,
    @SerializedName(value = "ocorrido_em", alternate = ["ocorridoEm"]) val ocorridoEm: String = "",
    @SerializedName(value = "disponivel_em", alternate = ["disponivelEm"]) val disponivelEm: String? = null,
    @SerializedName(value = "pedido_id", alternate = ["pedidoId"]) val pedidoId: String? = null
)
