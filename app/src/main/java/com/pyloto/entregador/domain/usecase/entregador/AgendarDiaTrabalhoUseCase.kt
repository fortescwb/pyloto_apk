package com.pyloto.entregador.domain.usecase.entregador

import com.pyloto.entregador.domain.model.AgendaTrabalho
import com.pyloto.entregador.domain.repository.EntregadorRepository
import javax.inject.Inject

class AgendarDiaTrabalhoUseCase @Inject constructor(
    private val repository: EntregadorRepository
) {
    suspend operator fun invoke(date: String): AgendaTrabalho {
        return repository.createWorkSchedule(date)
    }
}
