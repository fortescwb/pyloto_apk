package com.pyloto.entregador.domain.usecase.entregador

import com.pyloto.entregador.domain.model.OnboardingStatus
import com.pyloto.entregador.domain.repository.EntregadorRepository
import javax.inject.Inject

class EnviarAuditoriaVeiculoUseCase @Inject constructor(
    private val repository: EntregadorRepository
) {
    suspend operator fun invoke(
        incidentId: String,
        fotoVeiculoRef: String,
        placaInformada: String? = null
    ): OnboardingStatus {
        return repository.submitVehicleAuditEvidence(
            incidentId = incidentId,
            fotoVeiculoRef = fotoVeiculoRef,
            placaInformada = placaInformada
        )
    }
}
