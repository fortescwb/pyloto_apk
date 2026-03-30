package com.pyloto.entregador.core.network.model

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    @SerializedName("success") val success: Boolean = true,
    @SerializedName("data") val data: T? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("errors") val errors: List<ApiError>? = null,
    @SerializedName("meta") val meta: JsonObject? = null
) {
    fun requireData(): T {
        if (data != null) return data
        val errorDetail = errors?.joinToString("; ") { it.message }
            ?: message
            ?: "Resposta sem dados do servidor"
        throw ApiException(errorDetail)
    }
}

class ApiException(message: String) : RuntimeException(message)

data class ApiError(
    @SerializedName("field") val field: String?,
    @SerializedName("message") val message: String
)

data class PaginatedResponse<T>(
    @SerializedName(value = "items", alternate = ["content"]) val items: List<T> = emptyList(),
    @SerializedName("page") val page: Int = 0,
    @SerializedName(value = "size", alternate = ["page_size"]) val size: Int = 0,
    @SerializedName(value = "total", alternate = ["totalElements"]) val total: Long = 0,
    @SerializedName(value = "has_next", alternate = ["hasNext"]) val hasNext: Boolean = false
)
