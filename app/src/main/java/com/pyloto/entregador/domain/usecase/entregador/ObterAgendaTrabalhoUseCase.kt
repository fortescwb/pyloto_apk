package com.pyloto.entregador.domain.usecase.entregador

import com.pyloto.entregador.domain.model.AgendaTrabalho
import com.pyloto.entregador.domain.repository.EntregadorRepository
import javax.inject.Inject

class ObterAgendaTrabalhoUseCase @Inject constructor(
    private val repository: EntregadorRepository
) {
    suspend operator fun invoke(): AgendaTrabalho {
        return repository.getWorkSchedule()
    }
}
