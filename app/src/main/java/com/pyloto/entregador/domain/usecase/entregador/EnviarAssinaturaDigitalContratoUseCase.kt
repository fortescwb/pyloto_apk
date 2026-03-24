package com.pyloto.entregador.domain.usecase.entregador

import com.pyloto.entregador.domain.model.OnboardingStatus
import com.pyloto.entregador.domain.repository.EntregadorRepository
import javax.inject.Inject

class EnviarAssinaturaDigitalContratoUseCase @Inject constructor(
    private val repository: EntregadorRepository
) {
    suspend operator fun invoke(assinaturaDigitalRef: String): OnboardingStatus {
        return repository.submitDigitalContractSignature(assinaturaDigitalRef)
    }
}
