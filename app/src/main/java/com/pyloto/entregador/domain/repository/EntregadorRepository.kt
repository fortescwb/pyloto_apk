package com.pyloto.entregador.domain.repository

import com.pyloto.entregador.domain.model.Entregador
import com.pyloto.entregador.domain.model.Ganhos
import kotlinx.coroutines.flow.Flow

interface EntregadorRepository {
    suspend fun getPerfil(): Entregador
    fun observarPerfil(): Flow<Entregador?>
    suspend fun atualizarPerfil(entregador: Entregador): Entregador
    suspend fun atualizarStatusOnline(online: Boolean)
    suspend fun getGanhos(periodo: String, dataInicio: String? = null, dataFim: String? = null): Ganhos
}
