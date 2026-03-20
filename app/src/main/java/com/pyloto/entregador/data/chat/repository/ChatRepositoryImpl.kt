package com.pyloto.entregador.data.chat.repository

import com.pyloto.entregador.core.database.dao.MensagemDao
import com.pyloto.entregador.core.network.ApiService
import com.pyloto.entregador.data.common.withCacheFallback
import com.pyloto.entregador.data.chat.mapper.MensagemMapper
import com.pyloto.entregador.data.chat.remote.dto.EnviarMensagemRequest
import com.pyloto.entregador.domain.model.Mensagem
import com.pyloto.entregador.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val mensagemDao: MensagemDao,
    private val mensagemMapper: MensagemMapper
) : ChatRepository {

    override fun observarMensagens(corridaId: String): Flow<List<Mensagem>> {
        return mensagemDao.observarMensagens(corridaId).map { entities ->
            entities.map(mensagemMapper::toDomain)
        }
    }

    override suspend fun enviarMensagem(corridaId: String, conteudo: String, tipo: String): Mensagem {
        val response = apiService.enviarMensagem(
            corridaId,
            EnviarMensagemRequest(conteudo = conteudo, tipo = tipo)
        )
        val entity = mensagemMapper.toEntity(
            response.data,
            lida = true,
            sincronizada = true,
            corridaIdFallback = corridaId,
            remetenteTipoFallback = "ENTREGADOR"
        )
        mensagemDao.insert(entity)
        return mensagemMapper.toDomain(entity)
    }

    override suspend fun getMensagensPaginadas(corridaId: String, page: Int): List<Mensagem> {
        return withCacheFallback(
            operation = "chat_mensagens_paginadas",
            remote = {
                val response = apiService.getMensagens(corridaId, page)
                val entities = response.data.items.map { msg ->
                    mensagemMapper.toEntity(
                        msg,
                        sincronizada = true,
                        corridaIdFallback = corridaId
                    )
                }
                mensagemDao.insertAll(entities)
                entities.map(mensagemMapper::toDomain)
            },
            local = {
                mensagemDao.getMensagensPaginadas(corridaId, offset = page * 20)
                    .map(mensagemMapper::toDomain)
            }
        )
    }

    override suspend fun marcarComoLidas(corridaId: String) {
        mensagemDao.marcarTodasComoLidas(corridaId)
    }

    override fun contarNaoLidas(corridaId: String): Flow<Int> {
        return mensagemDao.contarNaoLidas(corridaId)
    }
}
