package com.pyloto.entregador.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "mensagens",
    indices = [
        Index(value = ["corridaId"]),
        Index(value = ["timestamp"])
    ]
)
data class MensagemEntity(
    @PrimaryKey val id: String,
    val corridaId: String,
    val remetenteId: String,
    val remetenteTipo: String, // ENTREGADOR, CLIENTE, SISTEMA
    val conteudo: String,
    val tipoMensagem: String, // TEXTO, IMAGEM, LOCALIZACAO
    val timestamp: Long,
    val lida: Boolean = false,
    val sincronizada: Boolean = false
)
