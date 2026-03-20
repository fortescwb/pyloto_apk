package com.pyloto.entregador.data.entregador.repository

import com.pyloto.entregador.core.database.dao.EntregadorDao
import com.pyloto.entregador.core.network.ApiService
import com.pyloto.entregador.data.common.withCacheFallback
import com.pyloto.entregador.data.entregador.mapper.EntregadorMapper
import com.pyloto.entregador.data.entregador.remote.dto.StatusRequest
import com.pyloto.entregador.data.ganhos.mapper.GanhosMapper
import com.pyloto.entregador.domain.model.Entregador
import com.pyloto.entregador.domain.model.Ganhos
import com.pyloto.entregador.domain.repository.EntregadorRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EntregadorRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val entregadorDao: EntregadorDao,
    private val entregadorMapper: EntregadorMapper,
    private val ganhosMapper: GanhosMapper
) : EntregadorRepository {

    override suspend fun getPerfil(): Entregador {
        return withCacheFallback(
            operation = "entregador_perfil",
            remote = {
                val response = apiService.getPerfil()
                val entity = entregadorMapper.toEntity(response.data)
                entregadorDao.insert(entity)
                entregadorMapper.toDomain(entity)
            },
            local = {
                val cached = entregadorDao.getEntregador()
                    ?: throw IllegalStateException("Perfil nao encontrado no cache")
                entregadorMapper.toDomain(cached)
            }
        )
    }

    override fun observarPerfil(): Flow<Entregador?> {
        return entregadorDao.observarEntregador().map { entity ->
            entity?.let(entregadorMapper::toDomain)
        }
    }

    override suspend fun atualizarPerfil(entregador: Entregador): Entregador {
        val request = entregadorMapper.toAtualizarPerfilRequest(entregador)
        apiService.atualizarPerfil(request)
        return getPerfil()
    }

    override suspend fun atualizarStatusOnline(online: Boolean) {
        apiService.atualizarStatus(StatusRequest(disponivel = online))
        entregadorDao.atualizarStatusOnline(online)
    }

    override suspend fun getGanhos(periodo: String, dataInicio: String?, dataFim: String?): Ganhos {
        val response = apiService.getGanhos(periodo, dataInicio, dataFim)
        return ganhosMapper.toDomain(response.data)
    }
}
