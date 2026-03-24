package com.pyloto.entregador.domain.model

data class ActiveRouteContext(
    val pedidoId: String,
    val phase: String,
    val startedAt: Long
)
