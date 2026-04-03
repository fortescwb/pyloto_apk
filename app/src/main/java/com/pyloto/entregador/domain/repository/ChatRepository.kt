package com.pyloto.entregador.domain.repository

import com.pyloto.entregador.domain.model.Mensagem
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun observarMensagens(corridaId: String): Flow<List<Mensagem>>
    suspend fun enviarMensagem(corridaId: String, conteudo: String, tipo: String = "TEXTO"): Mensagem
    suspend fun getMensagensPaginadas(corridaId: String, page: Int): List<Mensagem>
    suspend fun marcarComoLidas(corridaId: String)
    suspend fun obterNaoLidasServidor(corridaId: String): Int
    fun contarNaoLidas(corridaId: String): Flow<Int>
}
