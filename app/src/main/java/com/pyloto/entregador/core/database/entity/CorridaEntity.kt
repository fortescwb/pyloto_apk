package com.pyloto.entregador.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "corridas",
    indices = [
        Index(value = ["status"]),
        Index(value = ["sincronizado"])
    ]
)
data class CorridaEntity(
    @PrimaryKey val id: String,
    val clienteNome: String,
    val clienteTelefone: String,
    val clienteFoto: String?,
    val enderecoOrigem: String,
    val enderecoDestino: String,
    val latOrigem: Double,
    val lngOrigem: Double,
    val latDestino: Double,
    val lngDestino: Double,
    val valorEntrega: Double,
    val distanciaKm: Double,
    val tempoEstimadoMin: Int,
    val status: String, // DISPONIVEL, ACEITA, A_CAMINHO_COLETA, COLETADA, A_CAMINHO_ENTREGA, FINALIZADA, CANCELADA
    val aceitaEm: Long?,
    val iniciadaEm: Long?,
    val coletadaEm: Long?,
    val finalizadaEm: Long?,
    val canceladaEm: Long?,
    val motivoCancelamento: String?,
    val fotoComprovanteUrl: String?,
    val sincronizado: Boolean = false,
    val criadoEm: Long = System.currentTimeMillis(),
    val atualizadoEm: Long = System.currentTimeMillis()
)
