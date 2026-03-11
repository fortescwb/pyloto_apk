package com.pyloto.entregador.data.remote.model

import com.google.gson.annotations.SerializedName

// ==================== Auth ====================

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("senha") val senha: String
)

data class RegisterRequest(
    @SerializedName("nome") val nome: String,
    @SerializedName("email") val email: String,
    @SerializedName("senha") val senha: String,
    @SerializedName("telefone") val telefone: String,
    @SerializedName("cpf") val cpf: String,
    @SerializedName("tipo_veiculo") val veiculoTipo: String
)

data class RefreshTokenRequest(
    @SerializedName("refresh_token") val refreshToken: String
)

/**
 * DTO do token retornado pelo backend.
 * O backend retorna snake_case e inclui o objeto parceiro (não refreshToken/expiresIn).
 * Exemplo: {"access_token": "...", "token_type": "bearer", "parceiro": {"id": "...", ...}}
 */
data class AuthToken(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String = "bearer",
    @SerializedName("parceiro") val parceiro: ParceiroLoginDto? = null
)

data class ParceiroLoginDto(
    @SerializedName("id") val id: String,
    @SerializedName("nome") val nome: String? = null,
    @SerializedName("email") val email: String? = null
)

// ==================== Corrida ====================

data class CorridaResponse(
    @SerializedName("id") val id: String,
    @SerializedName("clienteNome") val clienteNome: String,
    @SerializedName("clienteTelefone") val clienteTelefone: String,
    @SerializedName("clienteFoto") val clienteFoto: String?,
    @SerializedName("enderecoOrigem") val enderecoOrigem: EnderecoResponse,
    @SerializedName("enderecoDestino") val enderecoDestino: EnderecoResponse,
    @SerializedName("valorEntrega") val valorEntrega: Double,
    @SerializedName("distanciaKm") val distanciaKm: Double,
    @SerializedName("tempoEstimadoMin") val tempoEstimadoMin: Int,
    @SerializedName("status") val status: String,
    @SerializedName("criadoEm") val criadoEm: Long,
    @SerializedName("aceitaEm") val aceitaEm: Long?,
    @SerializedName("iniciadaEm") val iniciadaEm: Long?,
    @SerializedName("coletadaEm") val coletadaEm: Long?,
    @SerializedName("finalizadaEm") val finalizadaEm: Long?,
    @SerializedName("canceladaEm") val canceladaEm: Long?,
    @SerializedName("fotoComprovanteUrl") val fotoComprovanteUrl: String?,
    @SerializedName("motivoCancelamento") val motivoCancelamento: String?
)

data class CorridaDetalhesResponse(
    @SerializedName("id") val id: String,
    @SerializedName("clienteNome") val clienteNome: String,
    @SerializedName("clienteTelefone") val clienteTelefone: String,
    @SerializedName("clienteFoto") val clienteFoto: String?,
    @SerializedName("enderecoOrigem") val enderecoOrigem: EnderecoResponse,
    @SerializedName("enderecoDestino") val enderecoDestino: EnderecoResponse,
    @SerializedName("valorEntrega") val valorEntrega: Double,
    @SerializedName("distanciaKm") val distanciaKm: Double,
    @SerializedName("tempoEstimadoMin") val tempoEstimadoMin: Int,
    @SerializedName("status") val status: String,
    @SerializedName("criadoEm") val criadoEm: Long,
    @SerializedName("aceitaEm") val aceitaEm: Long?,
    @SerializedName("iniciadaEm") val iniciadaEm: Long?,
    @SerializedName("coletadaEm") val coletadaEm: Long?,
    @SerializedName("finalizadaEm") val finalizadaEm: Long?,
    @SerializedName("canceladaEm") val canceladaEm: Long?,
    @SerializedName("fotoComprovanteUrl") val fotoComprovanteUrl: String?,
    @SerializedName("motivoCancelamento") val motivoCancelamento: String?
)

data class EnderecoResponse(
    @SerializedName("logradouro") val logradouro: String,
    @SerializedName("numero") val numero: String,
    @SerializedName("complemento") val complemento: String?,
    @SerializedName("bairro") val bairro: String,
    @SerializedName("cidade") val cidade: String,
    @SerializedName("cep") val cep: String,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double
)

data class FinalizacaoRequest(
    @SerializedName("foto_comprovante_url") val fotoComprovanteUrl: String?
)

data class CancelamentoRequest(
    @SerializedName("motivo") val motivo: String
)

// ==================== Entregador ====================

data class EntregadorPerfilResponse(
    @SerializedName("id") val id: String,
    @SerializedName("nome") val nome: String,
    @SerializedName("email") val email: String,
    @SerializedName("telefone") val telefone: String,
    @SerializedName("cpf") val cpf: String,
    @SerializedName("fotoUrl") val fotoUrl: String?,
    @SerializedName("veiculoTipo") val veiculoTipo: String?,
    @SerializedName("veiculoPlaca") val veiculoPlaca: String?,
    @SerializedName("rating") val rating: Double,
    @SerializedName("totalCorridas") val totalCorridas: Int,
    @SerializedName("statusOnline") val statusOnline: Boolean
)

data class AtualizarPerfilRequest(
    @SerializedName("nome") val nome: String?,
    @SerializedName("telefone") val telefone: String?,
    @SerializedName("foto_url") val fotoUrl: String?,
    @SerializedName("tipo_veiculo") val veiculoTipo: String?,
    @SerializedName("placa") val veiculoPlaca: String?
)

data class StatusRequest(
    @SerializedName("disponivel") val disponivel: Boolean
)

// ==================== Location ====================

data class LocationUpdate(
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
    @SerializedName("accuracy") val accuracy: Float,
    @SerializedName("speed") val speed: Float,
    @SerializedName("bearing") val bearing: Float,
    @SerializedName("timestamp") val timestamp: Long
)

// ==================== Ganhos ====================

data class GanhosResponse(
    @SerializedName("periodo") val periodo: String,
    @SerializedName("totalBruto") val totalBruto: Double,
    @SerializedName("totalLiquido") val totalLiquido: Double,
    @SerializedName("totalCorridas") val totalCorridas: Int,
    @SerializedName("mediaValorCorrida") val mediaValorCorrida: Double,
    @SerializedName("corridasPorDia") val corridasPorDia: Map<String, GanhosDiaResponse>
)

data class GanhosDiaResponse(
    @SerializedName("data") val data: String,
    @SerializedName("valor") val valor: Double,
    @SerializedName("quantidadeCorridas") val quantidadeCorridas: Int
)

// ==================== Chat ====================

data class MensagemResponse(
    @SerializedName("id") val id: String,
    @SerializedName("corridaId") val corridaId: String,
    @SerializedName("remetenteId") val remetenteId: String,
    @SerializedName("remetenteTipo") val remetenteTipo: String,
    @SerializedName("conteudo") val conteudo: String,
    @SerializedName("tipoMensagem") val tipoMensagem: String,
    @SerializedName("timestamp") val timestamp: Long
)

data class EnviarMensagemRequest(
    @SerializedName("conteudo") val conteudo: String,
    @SerializedName("tipo") val tipo: String = "TEXTO"
)

// ==================== Notificações ====================

data class FCMTokenRequest(
    @SerializedName("token") val token: String,
    @SerializedName("platform") val platform: String = "ANDROID"
)

data class NotificacaoResponse(
    @SerializedName("id") val id: String,
    @SerializedName("titulo") val titulo: String,
    @SerializedName("corpo") val corpo: String,
    @SerializedName("tipo") val tipo: String,
    @SerializedName("dados") val dados: Map<String, String>?,
    @SerializedName("timestamp") val timestamp: Long
)
