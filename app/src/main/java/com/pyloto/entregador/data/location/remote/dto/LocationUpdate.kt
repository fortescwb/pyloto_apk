package com.pyloto.entregador.data.location.remote.dto

import com.google.gson.annotations.SerializedName

data class LocationUpdate(
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("accuracy") val accuracy: Float,
    @SerializedName("speed") val speed: Float,
    @SerializedName("bearing") val bearing: Float,
    @SerializedName("timestamp") val timestamp: Long,
    @SerializedName("source") val source: String? = null,
    @SerializedName("pedido_id") val pedidoId: String? = null,
    @SerializedName("route_session_id") val routeSessionId: String? = null
)
