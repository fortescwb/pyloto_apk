package com.pyloto.entregador.data.entregador.repository

import com.pyloto.entregador.core.database.dao.EntregadorDao
import com.pyloto.entregador.core.network.ApiService
import com.pyloto.entregador.core.util.TokenManager
import com.pyloto.entregador.data.common.withCacheFallback
import com.pyloto.entregador.data.entregador.mapper.EntregadorMapper
import com.pyloto.entregador.data.entregador.remote.dto.CancelarAgendaRequest
import com.pyloto.entregador.data.entregador.remote.dto.CriarAgendaRequest
import com.pyloto.entregador.data.entregador.remote.dto.SubmitDigitalContractSignatureRequest
import com.pyloto.entregador.data.entregador.remote.dto.StatusRequest
import com.pyloto.entregador.data.entregador.remote.dto.SubmitVehicleAuditRequest
import com.pyloto.entregador.data.entregador.remote.dto.toDomain
import com.pyloto.entregador.domain.model.AgendaTrabalho
import com.pyloto.entregador.data.ganhos.mapper.GanhosMapper
import com.pyloto.entregador.domain.model.Entregador
import com.pyloto.entregador.domain.model.Ganhos
import com.pyloto.entregador.domain.model.OnboardingStatus
import com.pyloto.entregador.domain.model.OperationalCapacity
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
    private val ganhosMapper: GanhosMapper,
    private val tokenManager: TokenManager
) : EntregadorRepository {

    private suspend fun cacheOnboardingStatus(status: OnboardingStatus) {
        tokenManager.saveOnboardingComplete(status.prontoParaOperacao)
    }

    override suspend fun getPerfil(): Entregador {
        return withCacheFallback(
            operation = "entregador_perfil",
            remote = {
                val response = apiService.getPerfil()
                val entity = entregadorMapper.toEntity(response.requireData())
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
        return ganhosMapper.toDomain(response.requireData())
    }

    override suspend fun getOnboardingStatus(): OnboardingStatus {
        val status = apiService.getOnboardingStatus().requireData().toDomain()
        cacheOnboardingStatus(status)
        return status
    }

    override suspend fun getOperationalCapacity(): OperationalCapacity {
        return apiService.getOperationalCapacity().requireData().toDomain()
    }

    override suspend fun getWorkSchedule(): AgendaTrabalho {
        return apiService.getWorkSchedule().requireData().toDomain()
    }

    override suspend fun createWorkSchedule(date: String): AgendaTrabalho {
        return apiService.createWorkSchedule(
            request = CriarAgendaRequest(data = date)
        ).requireData().toDomain()
    }

    override suspend fun cancelWorkSchedule(scheduleId: String, reason: String?): AgendaTrabalho {
        return apiService.cancelWorkSchedule(
            agendaId = scheduleId,
            request = CancelarAgendaRequest(motivo = reason?.trim()?.ifBlank { null })
        ).requireData().toDomain()
    }

    override suspend fun submitDigitalContractSignature(assinaturaDigitalRef: String): OnboardingStatus {
        val status = apiService.submitDigitalContractSignature(
            request = SubmitDigitalContractSignatureRequest(
                assinaturaDigitalRef = assinaturaDigitalRef
            )
        ).requireData().toDomain()
        cacheOnboardingStatus(status)
        return status
    }

    override suspend fun submitVehicleAuditEvidence(
        incidentId: String,
        fotoVeiculoRef: String,
        placaInformada: String?
    ): OnboardingStatus {
        val status = apiService.submitVehicleAuditEvidence(
            request = SubmitVehicleAuditRequest(
                incidentId = incidentId,
                fotoVeiculoRef = fotoVeiculoRef,
                placaInformada = placaInformada?.trim()?.ifBlank { null }
            )
        ).requireData().toDomain()
        cacheOnboardingStatus(status)
        return status
    }
}
