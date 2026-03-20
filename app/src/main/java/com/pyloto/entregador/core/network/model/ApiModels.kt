package com.pyloto.entregador.core.network.model

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: T,
    @SerializedName("message") val message: String?,
    @SerializedName("errors") val errors: List<ApiError>?,
    @SerializedName("meta") val meta: JsonObject? = null
)

data class ApiError(
    @SerializedName("field") val field: String?,
    @SerializedName("message") val message: String
)

data class PaginatedResponse<T>(
    @SerializedName(value = "items", alternate = ["content"]) val items: List<T>,
    @SerializedName("page") val page: Int,
    @SerializedName(value = "size", alternate = ["page_size"]) val size: Int,
    @SerializedName(value = "total", alternate = ["totalElements"]) val total: Long,
    @SerializedName(value = "has_next", alternate = ["hasNext"]) val hasNext: Boolean = false
)
