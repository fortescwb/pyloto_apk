package com.pyloto.entregador.core.network

import com.pyloto.entregador.core.network.model.*
import com.pyloto.entregador.data.remote.model.*
import retrofit2.http.*

/**
 * Interface principal do serviço de API.
 * Todos os endpoints do backend Pyloto estão centralizados aqui.
 * Para escala, endpoints podem ser divididos em interfaces menores
 * (AuthApiService, CorridaApiService, etc.) e registrados via Hilt.
 */
interface ApiService {

    // ==================== AUTH ====================

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<AuthToken>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): ApiResponse<AuthToken>

    @POST("auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): ApiResponse<AuthToken>

    @POST("auth/logout")
    suspend fun logout(): ApiResponse<Unit>

    // ==================== CORRIDAS ====================

    @GET("corridas/disponiveis")
    suspend fun getCorridasDisponiveis(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double,
        @Query("raio") raio: Int = 5000
    ): ApiResponse<List<CorridaResponse>>

    @GET("corridas/{id}")
    suspend fun getCorridaDetalhes(@Path("id") corridaId: String): ApiResponse<CorridaDetalhesResponse>

    @POST("corridas/{id}/aceitar")
    suspend fun aceitarCorrida(@Path("id") corridaId: String): ApiResponse<CorridaDetalhesResponse>

    @POST("corridas/{id}/iniciar")
    suspend fun iniciarCorrida(@Path("id") corridaId: String): ApiResponse<Unit>

    @POST("corridas/{id}/coletar")
    suspend fun coletarCorrida(@Path("id") corridaId: String): ApiResponse<Unit>

    @POST("corridas/{id}/finalizar")
    suspend fun finalizarCorrida(
        @Path("id") corridaId: String,
        @Body request: FinalizacaoRequest
    ): ApiResponse<Unit>

    @POST("corridas/{id}/cancelar")
    suspend fun cancelarCorrida(
        @Path("id") corridaId: String,
        @Body request: CancelamentoRequest
    ): ApiResponse<Unit>

    @GET("corridas/historico")
    suspend fun getHistoricoCorridas(
        @Query("page") page: Int,
        @Query("size") size: Int = 20
    ): ApiResponse<PaginatedResponse<CorridaResponse>>

    // ==================== ENTREGADOR ====================

    @POST("entregador/localizacao")
    suspend fun atualizarLocalizacao(@Body location: LocationUpdate): ApiResponse<Unit>

    @POST("entregador/localizacao/batch")
    suspend fun atualizarLocalizacaoBatch(@Body locations: List<LocationUpdate>): ApiResponse<Unit>

    @GET("entregador/perfil")
    suspend fun getPerfil(): ApiResponse<EntregadorPerfilResponse>

    @PUT("entregador/perfil")
    suspend fun atualizarPerfil(@Body perfil: AtualizarPerfilRequest): ApiResponse<EntregadorPerfilResponse>

    @POST("entregador/status")
    suspend fun atualizarStatus(@Body status: StatusRequest): ApiResponse<Unit>

    @GET("entregador/ganhos")
    suspend fun getGanhos(
        @Query("periodo") periodo: String, // DIARIO, SEMANAL, MENSAL
        @Query("dataInicio") dataInicio: String?,
        @Query("dataFim") dataFim: String?
    ): ApiResponse<GanhosResponse>

    // ==================== CHAT ====================

    @GET("chat/{corridaId}/mensagens")
    suspend fun getMensagens(
        @Path("corridaId") corridaId: String,
        @Query("page") page: Int = 0
    ): ApiResponse<PaginatedResponse<MensagemResponse>>

    @POST("chat/{corridaId}/mensagem")
    suspend fun enviarMensagem(
        @Path("corridaId") corridaId: String,
        @Body mensagem: EnviarMensagemRequest
    ): ApiResponse<MensagemResponse>

    // ==================== NOTIFICAÇÕES ====================

    @POST("notificacoes/token")
    suspend fun registrarTokenFCM(@Body token: FCMTokenRequest): ApiResponse<Unit>

    @GET("notificacoes")
    suspend fun getNotificacoes(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): ApiResponse<PaginatedResponse<NotificacaoResponse>>
}
