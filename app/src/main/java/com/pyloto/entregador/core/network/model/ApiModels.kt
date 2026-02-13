package com.pyloto.entregador.core.network.model

import com.google.gson.annotations.SerializedName

/**
 * Wrapper padrão para todas as respostas da API.
 * Permite tratar erros de forma uniforme em toda a aplicação.
 */
data class ApiResponse<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: T,
    @SerializedName("message") val message: String?,
    @SerializedName("errors") val errors: List<ApiError>?
)

data class ApiError(
    @SerializedName("field") val field: String?,
    @SerializedName("message") val message: String
)

/**
 * Resposta paginada para listas grandes (escala).
 */
data class PaginatedResponse<T>(
    @SerializedName("content") val content: List<T>,
    @SerializedName("page") val page: Int,
    @SerializedName("size") val size: Int,
    @SerializedName("totalElements") val totalElements: Long,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("last") val last: Boolean
)

/**
 * Resultado genérico para operações assíncronas.
 * Facilita tratamento de estados em ViewModels.
 */
sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(val message: String, val code: Int? = null) : NetworkResult<Nothing>()
    object Loading : NetworkResult<Nothing>()
}
