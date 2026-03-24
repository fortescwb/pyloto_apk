package com.pyloto.entregador.domain.repository

import com.pyloto.entregador.domain.model.CapacityCheck
import com.pyloto.entregador.domain.model.Corrida
import com.pyloto.entregador.domain.model.CorridaOperationalEvent
import kotlinx.coroutines.flow.Flow

interface CorridaRepository {
    suspend fun getCorridasDisponiveis(lat: Double, lng: Double, raio: Int = 5000): List<Corrida>
    suspend fun getCorridaDetalhes(corridaId: String): Corrida
    suspend fun getCapacityCheck(corridaId: String): CapacityCheck
    suspend fun aceitarCorrida(corridaId: String): Corrida
    suspend fun recusarCorrida(corridaId: String, categoria: String, motivo: String)
    suspend fun iniciarCorrida(corridaId: String)
    suspend fun coletarCorrida(corridaId: String)
    suspend fun finalizarCorrida(corridaId: String, fotoComprovante: String?)
    suspend fun cancelarCorrida(corridaId: String, motivo: String)
    suspend fun registrarEventoOperacional(corridaId: String, event: CorridaOperationalEvent): Corrida
    fun observarCorridaAtiva(): Flow<Corrida?>
    fun observarHistorico(): Flow<List<Corrida>>
    suspend fun getHistoricoPaginado(page: Int, size: Int = 20): List<Corrida>
}
