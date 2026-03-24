package com.pyloto.entregador.domain.usecase.entregador

import com.pyloto.entregador.domain.model.OperationalCapacity
import com.pyloto.entregador.domain.repository.EntregadorRepository
import javax.inject.Inject

class ObterCapacidadeOperacionalUseCase @Inject constructor(
    private val repository: EntregadorRepository
) {
    suspend operator fun invoke(): OperationalCapacity {
        return repository.getOperationalCapacity()
    }
}
