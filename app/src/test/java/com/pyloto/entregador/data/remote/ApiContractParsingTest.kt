package com.pyloto.entregador.data.remote

import com.google.common.truth.Truth.assertThat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.pyloto.entregador.core.network.model.ApiResponse
import com.pyloto.entregador.core.network.model.PaginatedResponse
import com.pyloto.entregador.data.auth.remote.dto.AuthToken
import com.pyloto.entregador.data.chat.remote.dto.MensagemResponse
import com.pyloto.entregador.data.corrida.remote.dto.CorridaDetalhesResponse
import com.pyloto.entregador.data.corrida.remote.dto.CorridaResponse
import com.pyloto.entregador.data.entregador.remote.dto.EntregadorPerfilResponse
import com.pyloto.entregador.data.notificacao.remote.dto.NotificacaoResponse
import org.junit.Test

class ApiContractParsingTest {

    private val gson = Gson()

    @Test
    fun parseAuthTokenFromBackendEnvelope() {
        val json =
            """
            {
              "success": true,
              "data": {
                "access_token": "access-jwt",
                "refresh_token": "refresh-jwt",
                "token_type": "bearer",
                "parceiro": {
                  "id": "par-123",
                  "nome": "Entregador Teste",
                  "email": "teste@pyloto.com"
                }
              }
            }
            """.trimIndent()

        val type = object : TypeToken<ApiResponse<AuthToken>>() {}.type
        val response: ApiResponse<AuthToken> = gson.fromJson(json, type)

        assertThat(response.success).isTrue()
        assertThat(response.requireData().accessToken).isEqualTo("access-jwt")
        assertThat(response.requireData().refreshToken).isEqualTo("refresh-jwt")
        assertThat(response.requireData().parceiro?.id).isEqualTo("par-123")
    }

    @Test
    fun parseEntregadorPerfilSnakeCaseContract() {
        val json =
            """
            {
              "success": true,
              "data": {
                "id": "par-123",
                "nome": "Entregador Teste",
                "email": "teste@pyloto.com",
                "telefone": "11999999999",
                "cpf": "12345678900",
                "foto_url": "https://cdn/pic.jpg",
                "veiculo_tipo": "moto",
                "veiculo_placa": "ABC1234",
                "nota_media": 4.9,
                "total_corridas": 12,
                "online": true
              }
            }
            """.trimIndent()

        val type = object : TypeToken<ApiResponse<EntregadorPerfilResponse>>() {}.type
        val response: ApiResponse<EntregadorPerfilResponse> = gson.fromJson(json, type)

        assertThat(response.requireData().veiculoTipo).isEqualTo("moto")
        assertThat(response.requireData().veiculoPlaca).isEqualTo("ABC1234")
        assertThat(response.requireData().rating).isEqualTo(4.9)
        assertThat(response.requireData().totalCorridas).isEqualTo(12)
        assertThat(response.requireData().statusOnline).isTrue()
    }

    @Test
    fun parseCorridaResponseFromPedidoShape() {
        val json =
            """
            {
              "success": true,
              "data": [
                {
                  "id": "PED-001",
                  "status": "disponivel",
                  "valor_parceiro": 22.5,
                  "endereco_origem": {
                    "rua": "Rua A",
                    "bairro": "Centro",
                    "lat": -23.55,
                    "lng": -46.63
                  },
                  "endereco_destino": {
                    "logradouro": "Rua B",
                    "bairro": "Jardins",
                    "latitude": -23.56,
                    "longitude": -46.64
                  },
                  "created_at": 1741290000,
                  "dados": {
                    "nome": "Cliente XPTO",
                    "telefone": "11988887777"
                  }
                }
              ]
            }
            """.trimIndent()

        val type = object : TypeToken<ApiResponse<List<CorridaResponse>>>() {}.type
        val response: ApiResponse<List<CorridaResponse>> = gson.fromJson(json, type)

        val corrida = response.requireData().first()
        assertThat(corrida.id).isEqualTo("PED-001")
        assertThat(corrida.valorEntrega).isEqualTo(22.5)
        assertThat(corrida.enderecoOrigem?.logradouro).isEqualTo("Rua A")
        assertThat(corrida.enderecoDestino?.logradouro).isEqualTo("Rua B")
        assertThat(corrida.dados?.get("nome") as String).isEqualTo("Cliente XPTO")
    }

    @Test
    fun parseCorridaDetalhesResponseFromPedidoShape() {
        val json =
            """
            {
              "success": true,
              "data": {
                "id": "PED-010",
                "status": "aceito",
                "valor_estimado": 31.0,
                "endereco_origem": { "rua": "Origem" },
                "endereco_destino": { "rua": "Destino" },
                "aceito_at": 1741290100,
                "created_at": 1741290000
              }
            }
            """.trimIndent()

        val type = object : TypeToken<ApiResponse<CorridaDetalhesResponse>>() {}.type
        val response: ApiResponse<CorridaDetalhesResponse> = gson.fromJson(json, type)

        assertThat(response.requireData().id).isEqualTo("PED-010")
        assertThat(response.requireData().aceitaEm).isEqualTo(1741290100.0)
        assertThat(response.requireData().valorEntrega).isEqualTo(31.0)
    }

    @Test
    fun parseMensagemResponsePaginatedContract() {
        val json =
            """
            {
              "success": true,
              "data": {
                "items": [
                  {
                    "id": "msg-1",
                    "corrida_id": "PED-001",
                    "remetente_id": "par-123",
                    "remetente_tipo": "ENTREGADOR",
                    "conteudo": "Cheguei",
                    "tipo_mensagem": "TEXTO",
                    "timestamp": 1741290300000
                  }
                ],
                "page": 0,
                "page_size": 20,
                "total": 1,
                "has_next": false
              }
            }
            """.trimIndent()

        val type = object : TypeToken<ApiResponse<PaginatedResponse<MensagemResponse>>>() {}.type
        val response: ApiResponse<PaginatedResponse<MensagemResponse>> = gson.fromJson(json, type)

        assertThat(response.requireData().items).hasSize(1)
        assertThat(response.requireData().items.first().corridaId).isEqualTo("PED-001")
        assertThat(response.requireData().items.first().tipoMensagem).isEqualTo("TEXTO")
    }

    @Test
    fun parseNotificacaoResponsePaginatedContract() {
        val json =
            """
            {
              "success": true,
              "data": {
                "items": [
                  {
                    "id": "notif-1",
                    "title": "Nova corrida",
                    "mensagem": "Pedido disponivel",
                    "tipo": "NOVO_PEDIDO",
                    "dados": { "pedido_id": "PED-1" },
                    "created_at": 1741290300
                  }
                ],
                "page": 0,
                "page_size": 20,
                "total": 1,
                "has_next": false
              }
            }
            """.trimIndent()

        val type = object : TypeToken<ApiResponse<PaginatedResponse<NotificacaoResponse>>>() {}.type
        val response: ApiResponse<PaginatedResponse<NotificacaoResponse>> = gson.fromJson(json, type)

        assertThat(response.requireData().items).hasSize(1)
        assertThat(response.requireData().items.first().titulo).isEqualTo("Nova corrida")
        assertThat(response.requireData().items.first().corpo).isEqualTo("Pedido disponivel")
        assertThat(response.requireData().items.first().timestamp).isEqualTo(1741290300.0)
    }
}
