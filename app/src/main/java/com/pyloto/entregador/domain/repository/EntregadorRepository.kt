package com.pyloto.entregador.domain.repository

import com.pyloto.entregador.domain.model.AgendaTrabalho
import com.pyloto.entregador.domain.model.Entregador
import com.pyloto.entregador.domain.model.Ganhos
import com.pyloto.entregador.domain.model.OnboardingStatus
import com.pyloto.entregador.domain.model.OperationalCapacity
import kotlinx.coroutines.flow.Flow

interface EntregadorRepository {
    suspend fun getPerfil(): Entregador
    fun observarPerfil(): Flow<Entregador?>
    suspend fun atualizarPerfil(entregador: Entregador): Entregador
    suspend fun atualizarStatusOnline(online: Boolean)
    suspend fun getGanhos(periodo: String, dataInicio: String? = null, dataFim: String? = null): Ganhos
    suspend fun getOnboardingStatus(): OnboardingStatus
    suspend fun getOperationalCapacity(): OperationalCapacity
    suspend fun getWorkSchedule(): AgendaTrabalho
    suspend fun createWorkSchedule(date: String): AgendaTrabalho
    suspend fun cancelWorkSchedule(scheduleId: String, reason: String? = null): AgendaTrabalho
    suspend fun submitDigitalContractSignature(assinaturaDigitalRef: String): OnboardingStatus
    suspend fun submitVehicleAuditEvidence(
        incidentId: String,
        fotoVeiculoRef: String,
        placaInformada: String? = null
    ): OnboardingStatus
}
