package com.pyloto.entregador.data.repository

import com.pyloto.entregador.core.database.dao.MensagemDao
import com.pyloto.entregador.core.database.entity.MensagemEntity
import com.pyloto.entregador.core.network.ApiService
import com.pyloto.entregador.data.remote.model.EnviarMensagemRequest
import com.pyloto.entregador.domain.model.Mensagem
import com.pyloto.entregador.domain.model.RemetenteTipo
import com.pyloto.entregador.domain.model.TipoMensagem
import com.pyloto.entregador.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val mensagemDao: MensagemDao
) : ChatRepository {

    override fun observarMensagens(corridaId: String): Flow<List<Mensagem>> {
        return mensagemDao.observarMensagens(corridaId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun enviarMensagem(corridaId: String, conteudo: String, tipo: String): Mensagem {
        val response = apiService.enviarMensagem(
            corridaId,
            EnviarMensagemRequest(conteudo = conteudo, tipo = tipo)
        )
        val entity = MensagemEntity(
            id = response.data.id,
            corridaId = response.data.corridaId,
            remetenteId = response.data.remetenteId,
            remetenteTipo = response.data.remetenteTipo,
            conteudo = response.data.conteudo,
            tipoMensagem = response.data.tipoMensagem,
            timestamp = response.data.timestamp,
            lida = true,
            sincronizada = true
        )
        mensagemDao.insert(entity)
        return entity.toDomain()
    }

    override suspend fun getMensagensPaginadas(corridaId: String, page: Int): List<Mensagem> {
        return try {
            val response = apiService.getMensagens(corridaId, page)
            val entities = response.data.content.map { msg ->
                MensagemEntity(
                    id = msg.id,
                    corridaId = msg.corridaId,
                    remetenteId = msg.remetenteId,
                    remetenteTipo = msg.remetenteTipo,
                    conteudo = msg.conteudo,
                    tipoMensagem = msg.tipoMensagem,
                    timestamp = msg.timestamp,
                    sincronizada = true
                )
            }
            mensagemDao.insertAll(entities)
            entities.map { it.toDomain() }
        } catch (e: Exception) {
            mensagemDao.getMensagensPaginadas(corridaId, offset = page * 20)
                .map { it.toDomain() }
        }
    }

    override suspend fun marcarComoLidas(corridaId: String) {
        mensagemDao.marcarTodasComoLidas(corridaId)
    }

    override fun contarNaoLidas(corridaId: String): Flow<Int> {
        return mensagemDao.contarNaoLidas(corridaId)
    }

    private fun MensagemEntity.toDomain(): Mensagem {
        return Mensagem(
            id = this.id,
            corridaId = this.corridaId,
            remetenteId = this.remetenteId,
            remetenteTipo = RemetenteTipo.valueOf(this.remetenteTipo),
            conteudo = this.conteudo,
            tipo = TipoMensagem.valueOf(this.tipoMensagem),
            timestamp = this.timestamp,
            lida = this.lida
        )
    }
}
