package com.pyloto.entregador.data.corrida.remote.dto

import com.google.gson.annotations.SerializedName

data class CorridaResponse(
    @SerializedName("id") val id: String,
    @SerializedName(value = "cliente_nome", alternate = ["clienteNome", "solicitante_nome"]) val clienteNome: String? = null,
    @SerializedName(value = "cliente_telefone", alternate = ["clienteTelefone", "solicitante_telefone"]) val clienteTelefone: String? = null,
    @SerializedName(value = "cliente_foto", alternate = ["clienteFoto"]) val clienteFoto: String? = null,
    @SerializedName(value = "endereco_origem", alternate = ["enderecoOrigem"]) val enderecoOrigem: EnderecoResponse? = null,
    @SerializedName(value = "endereco_destino", alternate = ["enderecoDestino"]) val enderecoDestino: EnderecoResponse? = null,
    @SerializedName(value = "valor_parceiro", alternate = ["valorEntrega"]) val valorEntrega: Double? = null,
    @SerializedName(value = "distancia_km", alternate = ["distanciaKm"]) val distanciaKm: Double? = null,
    @SerializedName(value = "tempo_estimado_min", alternate = ["tempoEstimadoMin"]) val tempoEstimadoMin: Int? = null,
    @SerializedName("status") val status: String,
    @SerializedName(value = "created_at", alternate = ["criadoEm"]) val criadoEm: Double? = null,
    @SerializedName(value = "aceito_at", alternate = ["aceitaEm"]) val aceitaEm: Double? = null,
    @SerializedName(value = "coletando_at", alternate = ["iniciadaEm"]) val iniciadaEm: Double? = null,
    @SerializedName(value = "coletado_at", alternate = ["coletadaEm"]) val coletadaEm: Double? = null,
    @SerializedName(value = "finalizado_at", alternate = ["finalizadaEm", "entregue_at"]) val finalizadaEm: Double? = null,
    @SerializedName(value = "cancelado_at", alternate = ["canceladaEm"]) val canceladaEm: Double? = null,
    @SerializedName(value = "foto_comprovante_url", alternate = ["fotoComprovanteUrl"]) val fotoComprovanteUrl: String? = null,
    @SerializedName(value = "motivo", alternate = ["motivoCancelamento", "motivo_cancelamento"]) val motivoCancelamento: String? = null,
    @SerializedName("modalidade_corrida") val modalidadeCorrida: String? = null,
    @SerializedName("processamento_dia_seguinte") val processamentoDiaSeguinte: Boolean? = null,
    @SerializedName("coleta_deadline_at") val coletaDeadlineAt: Double? = null,
    @SerializedName("entrega_deadline_at") val entregaDeadlineAt: Double? = null,
    @SerializedName("sla_status") val slaStatus: String? = null,
    @SerializedName("sla_rule_summary") val slaResumo: String? = null,
    @SerializedName("sla_alerts") val slaAlerts: List<String>? = null,
    @SerializedName("distancia_ate_coleta_m") val distanciaAteColetaM: Int? = null,
    @SerializedName("eta_ate_coleta_min") val etaAteColetaMin: Int? = null,
    @SerializedName("distancia_total_m") val distanciaTotalM: Int? = null,
    @SerializedName("tempo_total_min") val tempoTotalMin: Int? = null,
    @SerializedName("ganho_por_km") val ganhoPorKm: Double? = null,
    @SerializedName("geo_source") val geoSource: String? = null,
    @SerializedName("rank_dispatch") val rankDispatch: Int? = null,
    @SerializedName("dados") val dados: Map<String, Any?>? = null
)

data class CorridaDetalhesResponse(
    @SerializedName("id") val id: String,
    @SerializedName(value = "cliente_nome", alternate = ["clienteNome", "solicitante_nome"]) val clienteNome: String? = null,
    @SerializedName(value = "cliente_telefone", alternate = ["clienteTelefone", "solicitante_telefone"]) val clienteTelefone: String? = null,
    @SerializedName(value = "cliente_foto", alternate = ["clienteFoto"]) val clienteFoto: String? = null,
    @SerializedName(value = "endereco_origem", alternate = ["enderecoOrigem"]) val enderecoOrigem: EnderecoResponse? = null,
    @SerializedName(value = "endereco_destino", alternate = ["enderecoDestino"]) val enderecoDestino: EnderecoResponse? = null,
    @SerializedName(value = "valor_parceiro", alternate = ["valorEntrega"]) val valorEntrega: Double? = null,
    @SerializedName(value = "distancia_km", alternate = ["distanciaKm"]) val distanciaKm: Double? = null,
    @SerializedName(value = "tempo_estimado_min", alternate = ["tempoEstimadoMin"]) val tempoEstimadoMin: Int? = null,
    @SerializedName("status") val status: String,
    @SerializedName(value = "created_at", alternate = ["criadoEm"]) val criadoEm: Double? = null,
    @SerializedName(value = "aceito_at", alternate = ["aceitaEm"]) val aceitaEm: Double? = null,
    @SerializedName(value = "coletando_at", alternate = ["iniciadaEm"]) val iniciadaEm: Double? = null,
    @SerializedName(value = "coletado_at", alternate = ["coletadaEm"]) val coletadaEm: Double? = null,
    @SerializedName(value = "finalizado_at", alternate = ["finalizadaEm", "entregue_at"]) val finalizadaEm: Double? = null,
    @SerializedName(value = "cancelado_at", alternate = ["canceladaEm"]) val canceladaEm: Double? = null,
    @SerializedName(value = "foto_comprovante_url", alternate = ["fotoComprovanteUrl"]) val fotoComprovanteUrl: String? = null,
    @SerializedName(value = "motivo", alternate = ["motivoCancelamento", "motivo_cancelamento"]) val motivoCancelamento: String? = null,
    @SerializedName("modalidade_corrida") val modalidadeCorrida: String? = null,
    @SerializedName("processamento_dia_seguinte") val processamentoDiaSeguinte: Boolean? = null,
    @SerializedName("coleta_deadline_at") val coletaDeadlineAt: Double? = null,
    @SerializedName("entrega_deadline_at") val entregaDeadlineAt: Double? = null,
    @SerializedName("sla_status") val slaStatus: String? = null,
    @SerializedName("sla_rule_summary") val slaResumo: String? = null,
    @SerializedName("sla_alerts") val slaAlerts: List<String>? = null,
    @SerializedName("distancia_ate_coleta_m") val distanciaAteColetaM: Int? = null,
    @SerializedName("eta_ate_coleta_min") val etaAteColetaMin: Int? = null,
    @SerializedName("distancia_total_m") val distanciaTotalM: Int? = null,
    @SerializedName("tempo_total_min") val tempoTotalMin: Int? = null,
    @SerializedName("ganho_por_km") val ganhoPorKm: Double? = null,
    @SerializedName("geo_source") val geoSource: String? = null,
    @SerializedName("rank_dispatch") val rankDispatch: Int? = null,
    @SerializedName("dados") val dados: Map<String, Any?>? = null
)

data class EnderecoResponse(
    @SerializedName(value = "logradouro", alternate = ["rua"]) val logradouro: String? = null,
    @SerializedName("numero") val numero: String? = null,
    @SerializedName("complemento") val complemento: String? = null,
    @SerializedName("bairro") val bairro: String? = null,
    @SerializedName(value = "cidade", alternate = ["cidade_estado"]) val cidade: String? = null,
    @SerializedName("cep") val cep: String? = null,
    @SerializedName(value = "latitude", alternate = ["lat"]) val latitude: Double? = null,
    @SerializedName(value = "longitude", alternate = ["lng"]) val longitude: Double? = null
)

data class FinalizacaoRequest(
    @SerializedName("foto_comprovante_url") val fotoComprovanteUrl: String?,
    @SerializedName("upload_id") val uploadId: String? = null
)

data class CancelamentoRequest(
    @SerializedName("motivo") val motivo: String
)

data class RecusaCorridaRequest(
    @SerializedName("categoria") val categoria: String,
    @SerializedName("motivo") val motivo: String
)

data class RecusaCorridaReputationResponse(
    @SerializedName("nivel_distribuicao") val nivelDistribuicao: String? = null,
    @SerializedName("recusas_injustificadas_janela") val recusasInjustificadasJanela: Int? = null
)

data class RecusaCorridaResponse(
    @SerializedName("pedido_id") val pedidoId: String,
    @SerializedName("categoria") val categoria: String,
    @SerializedName("justificavel") val justificavel: Boolean,
    @SerializedName("reputacao") val reputacao: RecusaCorridaReputationResponse? = null
)

data class OperationalEventRequest(
    @SerializedName("kind") val kind: String,
    @SerializedName("message") val message: String? = null,
    @SerializedName("latitude") val latitude: Double? = null,
    @SerializedName("longitude") val longitude: Double? = null,
    @SerializedName("accuracy") val accuracy: Float? = null,
    @SerializedName("speed") val speed: Float? = null,
    @SerializedName("bearing") val bearing: Float? = null,
    @SerializedName("source") val source: String? = null,
    @SerializedName("metadata") val metadata: Map<String, String>? = null
)
