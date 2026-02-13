package com.pyloto.entregador.domain.model

import java.math.BigDecimal

data class Corrida(
    val id: String,
    val cliente: Cliente,
    val origem: Endereco,
    val destino: Endereco,
    val valor: BigDecimal,
    val distanciaKm: Double,
    val tempoEstimadoMin: Int,
    val status: CorridaStatus,
    val timestamps: CorridaTimestamps,
    val fotoComprovanteUrl: String? = null,
    val motivoCancelamento: String? = null
)

data class Cliente(
    val nome: String,
    val telefone: String,
    val foto: String? = null
)

data class Endereco(
    val logradouro: String,
    val numero: String,
    val complemento: String? = null,
    val bairro: String,
    val cidade: String,
    val cep: String,
    val latitude: Double,
    val longitude: Double
) {
    val enderecoFormatado: String
        get() = "$logradouro, $numero${complemento?.let { " - $it" } ?: ""} - $bairro"
}

enum class CorridaStatus {
    DISPONIVEL,
    ACEITA,
    A_CAMINHO_COLETA,
    COLETADA,
    A_CAMINHO_ENTREGA,
    FINALIZADA,
    CANCELADA;

    val isAtiva: Boolean
        get() = this in listOf(ACEITA, A_CAMINHO_COLETA, COLETADA, A_CAMINHO_ENTREGA)

    val isFinalizada: Boolean
        get() = this == FINALIZADA || this == CANCELADA
}

data class CorridaTimestamps(
    val criadaEm: Long,
    val aceitaEm: Long? = null,
    val iniciadaEm: Long? = null,
    val coletadaEm: Long? = null,
    val finalizadaEm: Long? = null,
    val canceladaEm: Long? = null
)
