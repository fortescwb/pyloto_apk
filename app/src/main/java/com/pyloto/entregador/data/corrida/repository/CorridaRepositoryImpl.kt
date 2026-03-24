package com.pyloto.entregador.data.corrida.repository

import com.pyloto.entregador.core.database.dao.CorridaDao
import com.pyloto.entregador.core.network.ApiService
import com.pyloto.entregador.core.network.ConnectivityMonitor
import com.pyloto.entregador.data.common.withCacheFallback
import com.pyloto.entregador.data.common.withFallbackValue
import com.pyloto.entregador.data.common.withNetworkGuard
import com.pyloto.entregador.data.corrida.mapper.CorridaMapper
import com.pyloto.entregador.data.corrida.remote.dto.CancelamentoRequest
import com.pyloto.entregador.data.corrida.remote.dto.FinalizacaoRequest
import com.pyloto.entregador.data.corrida.remote.dto.OperationalEventRequest
import com.pyloto.entregador.data.corrida.remote.dto.RecusaCorridaRequest
import com.pyloto.entregador.data.entregador.remote.dto.toDomain
import com.pyloto.entregador.domain.model.CapacityCheck
import com.pyloto.entregador.domain.model.Corrida
import com.pyloto.entregador.domain.model.CorridaOperationalEvent
import com.pyloto.entregador.domain.repository.CorridaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação offline-first do repositório de corridas.
 * Tenta obter dados da API primeiro, fallback para cache local.
 * Preparado para escala com sync queue e batch operations.
 */
@Singleton
class CorridaRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val corridaDao: CorridaDao,
    private val mapper: CorridaMapper,
    private val connectivityMonitor: ConnectivityMonitor
) : CorridaRepository {

    override suspend fun getCorridasDisponiveis(lat: Double, lng: Double, raio: Int): List<Corrida> {
        return withNetworkGuard(
            operation = "corridas_disponiveis",
            isConnected = connectivityMonitor.isCurrentlyConnected(),
            remote = {
                val response = apiService.getCorridasDisponiveis(lat, lng, raio)
                val corridas = response.data.map { mapper.toDomain(it) }
                corridaDao.insertAll(corridas.map { mapper.toEntity(it) })
                corridas
            },
            local = {
                corridaDao.getDisponiveis().map(mapper::toDomain)
            }
        )
    }

    override suspend fun getCorridaDetalhes(corridaId: String): Corrida {
        return withCacheFallback(
            operation = "corrida_detalhes",
            remote = {
                val response = apiService.getCorridaDetalhes(corridaId)
                val corrida = mapper.toDomain(response.data)
                corridaDao.insert(mapper.toEntity(corrida))
                corrida
            },
            local = {
                val entity = corridaDao.getById(corridaId)
                    ?: throw IllegalStateException("Corrida $corridaId nao encontrada no cache")
                mapper.toDomain(entity)
            }
        )
    }

    override suspend fun getCapacityCheck(corridaId: String): CapacityCheck {
        val response = apiService.getCorridaCapacityCheck(corridaId)
        return response.data.toDomain()
    }

    override suspend fun aceitarCorrida(corridaId: String): Corrida {
        val response = apiService.aceitarCorrida(corridaId)
        val corrida = mapper.toDomain(response.data)
        corridaDao.insert(mapper.toEntity(corrida))
        return corrida
    }

    override suspend fun recusarCorrida(corridaId: String, categoria: String, motivo: String) {
        apiService.recusarCorrida(
            corridaId,
            RecusaCorridaRequest(
                categoria = categoria,
                motivo = motivo
            )
        )
        corridaDao.atualizarStatus(corridaId, "CANCELADA")
    }

    override suspend fun iniciarCorrida(corridaId: String) {
        apiService.iniciarCorrida(corridaId)
        val corrida = mapper.toDomain(apiService.getCorridaDetalhes(corridaId).data)
        corridaDao.insert(mapper.toEntity(corrida))
    }

    override suspend fun coletarCorrida(corridaId: String) {
        apiService.coletarCorrida(corridaId)
        val corrida = mapper.toDomain(apiService.getCorridaDetalhes(corridaId).data)
        corridaDao.insert(mapper.toEntity(corrida))
    }

    override suspend fun finalizarCorrida(corridaId: String, fotoComprovante: String?) {
        apiService.finalizarCorrida(corridaId, FinalizacaoRequest(fotoComprovanteUrl = fotoComprovante))
        val corrida = mapper.toDomain(apiService.getCorridaDetalhes(corridaId).data)
        corridaDao.insert(mapper.toEntity(corrida))
    }

    override suspend fun cancelarCorrida(corridaId: String, motivo: String) {
        apiService.cancelarCorrida(corridaId, CancelamentoRequest(motivo))
        corridaDao.atualizarStatus(corridaId, "CANCELADA")
    }

    override suspend fun registrarEventoOperacional(
        corridaId: String,
        event: CorridaOperationalEvent
    ): Corrida {
        val response = apiService.registrarEventoOperacional(
            corridaId,
            OperationalEventRequest(
                kind = event.kind,
                message = event.message,
                latitude = event.latitude,
                longitude = event.longitude,
                accuracy = event.accuracy,
                speed = event.speed,
                bearing = event.bearing,
                source = event.source,
                metadata = event.metadata.takeIf { it.isNotEmpty() }
            )
        )
        val corrida = mapper.toDomain(response.data)
        corridaDao.insert(mapper.toEntity(corrida))
        return corrida
    }

    override fun observarCorridaAtiva(): Flow<Corrida?> {
        return corridaDao.observarCorridaAtiva().map { entity ->
            entity?.let { mapper.toDomain(it) }
        }
    }

    override fun observarHistorico(): Flow<List<Corrida>> {
        return corridaDao.observarHistorico().map { entities ->
            entities.map { mapper.toDomain(it) }
        }
    }

    override suspend fun getHistoricoPaginado(page: Int, size: Int): List<Corrida> {
        return withFallbackValue(
            operation = "corridas_historico",
            fallbackValue = emptyList()
        ) {
            val response = apiService.getHistoricoCorridas(page, size)
            response.data.items.map { mapper.toDomain(it) }
        }
    }
}
