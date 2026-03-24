package com.pyloto.entregador.domain.model

data class CorridaOperationalEvent(
    val kind: String,
    val message: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val accuracy: Float? = null,
    val speed: Float? = null,
    val bearing: Float? = null,
    val source: String? = null,
    val metadata: Map<String, String> = emptyMap()
)
