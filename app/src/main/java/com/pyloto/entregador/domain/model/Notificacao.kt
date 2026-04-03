package com.pyloto.entregador.domain.model

data class Notificacao(
    val id: String,
    val titulo: String,
    val corpo: String,
    val tipo: String,
    val lida: Boolean,
    val lidaEm: Long?,
    val timestamp: Long
)
