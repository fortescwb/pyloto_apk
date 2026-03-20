package com.pyloto.entregador.domain.usecase.entregador

import com.pyloto.entregador.domain.model.Ganhos
import com.pyloto.entregador.domain.repository.EntregadorRepository
import javax.inject.Inject

class ObterGanhosUseCase @Inject constructor(
    private val repository: EntregadorRepository
) {
    suspend operator fun invoke(
        periodo: String,
        dataInicio: String? = null,
        dataFim: String? = null
    ): Ganhos {
        return repository.getGanhos(periodo = periodo, dataInicio = dataInicio, dataFim = dataFim)
    }
}

