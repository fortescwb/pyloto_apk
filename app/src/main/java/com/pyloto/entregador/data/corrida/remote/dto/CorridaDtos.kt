package com.pyloto.entregador.data.corrida.remote.dto

import com.google.gson.annotations.SerializedName

data class CorridaResponse(
    @SerializedName("id") val id: String,
    @SerializedName(value = "cliente_nome", alternate = ["clienteNome", "solicitante_nome"]) val clienteNome: String? = null,
    @SerializedName(value = "cliente_telefone", alternate = ["clienteTelefone", "solicitante_telefone"]) val clienteTelefone: String? = null,
    @SerializedName(value = "cliente_foto", alternate = ["clienteFoto"]) val clienteFoto: String? = null,
    @SerializedName(value = "endereco_origem", alternate = ["enderecoOrigem"]) val enderecoOrigem: EnderecoResponse? = null,
    @SerializedName(value = "endereco_destino", alternate = ["enderecoDestino"]) val enderecoDestino: EnderecoResponse? = null,
    @SerializedName(value = "valor_parceiro", alternate = ["valorEntrega", "valor_entrega", "valor_estimado", "valor_final"]) val valorEntrega: Double? = null,
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
    @SerializedName("dados") val dados: Map<String, Any?>? = null
)

data class CorridaDetalhesResponse(
    @SerializedName("id") val id: String,
    @SerializedName(value = "cliente_nome", alternate = ["clienteNome", "solicitante_nome"]) val clienteNome: String? = null,
    @SerializedName(value = "cliente_telefone", alternate = ["clienteTelefone", "solicitante_telefone"]) val clienteTelefone: String? = null,
    @SerializedName(value = "cliente_foto", alternate = ["clienteFoto"]) val clienteFoto: String? = null,
    @SerializedName(value = "endereco_origem", alternate = ["enderecoOrigem"]) val enderecoOrigem: EnderecoResponse? = null,
    @SerializedName(value = "endereco_destino", alternate = ["enderecoDestino"]) val enderecoDestino: EnderecoResponse? = null,
    @SerializedName(value = "valor_parceiro", alternate = ["valorEntrega", "valor_entrega", "valor_estimado", "valor_final"]) val valorEntrega: Double? = null,
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
    @SerializedName("dados") val dados: Map<String, Any?>? = null
)

data class EnderecoResponse(
    @SerializedName(value = "logradouro", alternate = ["rua"]) val logradouro: String? = null,
    @SerializedName("numero") val numero: String? = null,
    @SerializedName("complemento") val complemento: String? = null,
    @SerializedName("bairro") val bairro: String? = null,
    @SerializedName("cidade") val cidade: String? = null,
    @SerializedName("cep") val cep: String? = null,
    @SerializedName(value = "latitude", alternate = ["lat"]) val latitude: Double? = null,
    @SerializedName(value = "longitude", alternate = ["lng"]) val longitude: Double? = null
)

data class FinalizacaoRequest(
    @SerializedName("foto_comprovante_url") val fotoComprovanteUrl: String?
)

data class CancelamentoRequest(
    @SerializedName("motivo") val motivo: String
)
