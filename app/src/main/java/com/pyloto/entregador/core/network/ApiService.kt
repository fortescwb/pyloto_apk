package com.pyloto.entregador.core.network

import com.pyloto.entregador.core.network.model.ApiResponse
import com.pyloto.entregador.core.network.model.PaginatedResponse
import com.pyloto.entregador.data.auth.remote.dto.AuthToken
import com.pyloto.entregador.data.auth.remote.dto.LoginRequest
import com.pyloto.entregador.data.auth.remote.dto.RefreshTokenRequest
import com.pyloto.entregador.data.chat.remote.dto.EnviarMensagemRequest
import com.pyloto.entregador.data.chat.remote.dto.ChatReadResponse
import com.pyloto.entregador.data.chat.remote.dto.ChatUnreadCountResponse
import com.pyloto.entregador.data.chat.remote.dto.MensagemResponse
import com.pyloto.entregador.data.corrida.remote.dto.CancelamentoRequest
import com.pyloto.entregador.data.corrida.remote.dto.CorridaDetalhesResponse
import com.pyloto.entregador.data.corrida.remote.dto.CorridaResponse
import com.pyloto.entregador.data.corrida.remote.dto.FinalizacaoRequest
import com.pyloto.entregador.data.corrida.remote.dto.OperationalEventRequest
import com.pyloto.entregador.data.corrida.remote.dto.RecusaCorridaRequest
import com.pyloto.entregador.data.corrida.remote.dto.RecusaCorridaResponse
import com.pyloto.entregador.data.entregador.remote.dto.AtualizarPerfilRequest
import com.pyloto.entregador.data.entregador.remote.dto.AgendaTrabalhoResponse
import com.pyloto.entregador.data.entregador.remote.dto.CancelarAgendaRequest
import com.pyloto.entregador.data.entregador.remote.dto.CapacityCheckResponse
import com.pyloto.entregador.data.entregador.remote.dto.CapacitySnapshotResponse
import com.pyloto.entregador.data.entregador.remote.dto.CriarAgendaRequest
import com.pyloto.entregador.data.entregador.remote.dto.EntregadorPerfilResponse
import com.pyloto.entregador.data.entregador.remote.dto.OnboardingStatusResponse
import com.pyloto.entregador.data.entregador.remote.dto.SubmitDigitalContractSignatureRequest
import com.pyloto.entregador.data.entregador.remote.dto.SubmitVehicleAuditRequest
import com.pyloto.entregador.data.entregador.remote.dto.StatusRequest
import com.pyloto.entregador.data.ganhos.remote.dto.GanhosResponse
import com.pyloto.entregador.data.location.remote.dto.LocationUpdate
import com.pyloto.entregador.data.notificacao.remote.dto.FCMTokenRequest
import com.pyloto.entregador.data.notificacao.remote.dto.NotificacaoReadAllResponse
import com.pyloto.entregador.data.notificacao.remote.dto.NotificacaoReadResponse
import com.pyloto.entregador.data.notificacao.remote.dto.NotificacaoResponse
import com.pyloto.entregador.data.notificacao.remote.dto.NotificacaoUnreadCountResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Contrato HTTP do app parceiro com o backend pyloto_atende.
 *
 * Mantem somente definicoes de endpoint, sem regra de negocio.
 */
interface ApiService {

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<AuthToken>

    @POST("auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): ApiResponse<AuthToken>

    @POST("auth/logout")
    suspend fun logout(): ApiResponse<Unit>

    @GET("corridas/disponiveis")
    suspend fun getCorridasDisponiveis(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double,
        @Query("raio") raio: Int = 5000
    ): ApiResponse<List<CorridaResponse>>

    @GET("corridas/{id}")
    suspend fun getCorridaDetalhes(@Path("id") corridaId: String): ApiResponse<CorridaDetalhesResponse>

    @GET("corridas/{id}/capacidade-check")
    suspend fun getCorridaCapacityCheck(
        @Path("id") corridaId: String
    ): ApiResponse<CapacityCheckResponse>

    @POST("corridas/{id}/aceitar")
    suspend fun aceitarCorrida(@Path("id") corridaId: String): ApiResponse<CorridaDetalhesResponse>

    @POST("corridas/{id}/recusar")
    suspend fun recusarCorrida(
        @Path("id") corridaId: String,
        @Body request: RecusaCorridaRequest
    ): ApiResponse<RecusaCorridaResponse>

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

    @POST("corridas/{id}/eventos")
    suspend fun registrarEventoOperacional(
        @Path("id") corridaId: String,
        @Body request: OperationalEventRequest
    ): ApiResponse<CorridaDetalhesResponse>

    @GET("corridas/historico")
    suspend fun getHistoricoCorridas(
        @Query("page") page: Int,
        @Query("size") size: Int = 20
    ): ApiResponse<PaginatedResponse<CorridaResponse>>

    @POST("entregador/localizacao")
    suspend fun atualizarLocalizacao(@Body location: LocationUpdate): ApiResponse<Unit>

    @POST("entregador/localizacao/batch")
    suspend fun atualizarLocalizacaoBatch(@Body locations: List<LocationUpdate>): ApiResponse<Unit>

    @GET("entregador/perfil")
    suspend fun getPerfil(): ApiResponse<EntregadorPerfilResponse>

    @GET("entregador/onboarding-status")
    suspend fun getOnboardingStatus(): ApiResponse<OnboardingStatusResponse>

    @GET("entregador/capacidade")
    suspend fun getOperationalCapacity(): ApiResponse<CapacitySnapshotResponse>

    @GET("entregador/agenda")
    suspend fun getWorkSchedule(): ApiResponse<AgendaTrabalhoResponse>

    @POST("entregador/agenda")
    suspend fun createWorkSchedule(
        @Body request: CriarAgendaRequest
    ): ApiResponse<AgendaTrabalhoResponse>

    @POST("entregador/agenda/{agendaId}/cancelar")
    suspend fun cancelWorkSchedule(
        @Path("agendaId") agendaId: String,
        @Body request: CancelarAgendaRequest
    ): ApiResponse<AgendaTrabalhoResponse>

    @PUT("entregador/perfil")
    suspend fun atualizarPerfil(@Body perfil: AtualizarPerfilRequest): ApiResponse<EntregadorPerfilResponse>

    @POST("entregador/status")
    suspend fun atualizarStatus(@Body status: StatusRequest): ApiResponse<Unit>

    @POST("entregador/contrato/assinatura-digital")
    suspend fun submitDigitalContractSignature(
        @Body request: SubmitDigitalContractSignatureRequest
    ): ApiResponse<OnboardingStatusResponse>

    @POST("entregador/auditoria-veiculo")
    suspend fun submitVehicleAuditEvidence(
        @Body request: SubmitVehicleAuditRequest
    ): ApiResponse<OnboardingStatusResponse>

    @GET("entregador/ganhos")
    suspend fun getGanhos(
        @Query("periodo") periodo: String,
        @Query("data_inicio") dataInicio: String?,
        @Query("data_fim") dataFim: String?
    ): ApiResponse<GanhosResponse>

    @GET("chat/{corridaId}/mensagens")
    suspend fun getMensagens(
        @Path("corridaId") corridaId: String,
        @Query("page") page: Int = 0
    ): ApiResponse<PaginatedResponse<MensagemResponse>>

    @POST("chat/{corridaId}/mensagens")
    suspend fun enviarMensagem(
        @Path("corridaId") corridaId: String,
        @Body mensagem: EnviarMensagemRequest
    ): ApiResponse<MensagemResponse>

    @POST("chat/{corridaId}/read")
    suspend fun marcarChatComoLido(
        @Path("corridaId") corridaId: String
    ): ApiResponse<ChatReadResponse>

    @GET("chat/{corridaId}/unread-count")
    suspend fun getChatUnreadCount(
        @Path("corridaId") corridaId: String
    ): ApiResponse<ChatUnreadCountResponse>

    @POST("notificacoes/token")
    suspend fun registrarTokenFCM(@Body token: FCMTokenRequest): ApiResponse<Unit>

    @GET("notificacoes")
    suspend fun getNotificacoes(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): ApiResponse<PaginatedResponse<NotificacaoResponse>>

    @GET("notificacoes/unread-count")
    suspend fun getNotificacoesUnreadCount(): ApiResponse<NotificacaoUnreadCountResponse>

    @POST("notificacoes/{id}/read")
    suspend fun marcarNotificacaoComoLida(
        @Path("id") notificacaoId: String
    ): ApiResponse<NotificacaoReadResponse>

    @POST("notificacoes/read-all")
    suspend fun marcarNotificacoesComoLidas(): ApiResponse<NotificacaoReadAllResponse>
}
