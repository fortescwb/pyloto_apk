package com.pyloto.entregador.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "notificacoes",
    indices = [Index(value = ["lida"])]
)
data class NotificacaoEntity(
    @PrimaryKey val id: String,
    val titulo: String,
    val corpo: String,
    val tipo: String, // CORRIDA_NOVA, CORRIDA_CANCELADA, SISTEMA, PROMOCAO
    val dados: String?, // JSON extra
    val lida: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
