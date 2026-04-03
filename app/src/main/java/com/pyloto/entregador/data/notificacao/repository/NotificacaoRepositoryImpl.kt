package com.pyloto.entregador.data.notificacao.repository

import com.google.gson.Gson
import com.pyloto.entregador.core.database.dao.NotificacaoDao
import com.pyloto.entregador.core.database.entity.NotificacaoEntity
import com.pyloto.entregador.core.network.ApiService
import com.pyloto.entregador.data.notificacao.remote.dto.NotificacaoResponse
import com.pyloto.entregador.domain.model.Notificacao
import com.pyloto.entregador.domain.repository.NotificacaoRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class NotificacaoRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val notificacaoDao: NotificacaoDao,
    private val gson: Gson
) : NotificacaoRepository {

    override fun observarNotificacoes(): Flow<List<Notificacao>> {
        return notificacaoDao.observarTodas().map { list ->
            list.map { entity ->
                Notificacao(
                    id = entity.id,
                    titulo = entity.titulo,
                    corpo = entity.corpo,
                    tipo = entity.tipo,
                    lida = entity.lida,
                    lidaEm = null,
                    timestamp = entity.timestamp
                )
            }
        }
    }

    override fun observarNaoLidas(): Flow<Int> {
        return notificacaoDao.contarNaoLidas()
    }

    override suspend fun sincronizar(page: Int, size: Int) {
        val response = apiService.getNotificacoes(page = page, size = size).requireData()
        response.items.forEach { remote ->
            notificacaoDao.insert(remote.toEntity())
        }
    }

    override suspend fun obterNaoLidasServidor(): Int {
        return apiService.getNotificacoesUnreadCount().requireData().count
    }

    override suspend fun marcarComoLida(notificacaoId: String) {
        runCatching { apiService.marcarNotificacaoComoLida(notificacaoId).requireData() }
        notificacaoDao.marcarComoLida(notificacaoId)
    }

    override suspend fun marcarTodasComoLidas() {
        runCatching { apiService.marcarNotificacoesComoLidas().requireData() }
        notificacaoDao.marcarTodasComoLidas()
    }

    private fun NotificacaoResponse.toEntity(): NotificacaoEntity {
        val lidaEmMillis = lidaEm?.toMillisEpoch()
        return NotificacaoEntity(
            id = id,
            titulo = titulo,
            corpo = corpo,
            tipo = tipo,
            dados = gson.toJson(dados ?: emptyMap<String, String>()),
            lida = lida,
            timestamp = lidaEmMillis ?: timestamp.toMillisEpoch()
        )
    }

    private fun Double.toMillisEpoch(): Long {
        return if (this > 1_000_000_000_000) this.toLong() else (this * 1000).toLong()
    }
}
