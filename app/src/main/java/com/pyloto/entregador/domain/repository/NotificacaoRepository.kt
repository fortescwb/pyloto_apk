package com.pyloto.entregador.domain.repository

import com.pyloto.entregador.domain.model.Notificacao
import kotlinx.coroutines.flow.Flow

interface NotificacaoRepository {
    fun observarNotificacoes(): Flow<List<Notificacao>>
    fun observarNaoLidas(): Flow<Int>
    suspend fun sincronizar(page: Int = 0, size: Int = 30)
    suspend fun obterNaoLidasServidor(): Int
    suspend fun marcarComoLida(notificacaoId: String)
    suspend fun marcarTodasComoLidas()
}
