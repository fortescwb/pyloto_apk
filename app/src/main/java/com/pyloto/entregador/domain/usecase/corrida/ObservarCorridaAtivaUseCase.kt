package com.pyloto.entregador.domain.usecase.corrida

import com.pyloto.entregador.domain.model.Corrida
import com.pyloto.entregador.domain.repository.CorridaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservarCorridaAtivaUseCase @Inject constructor(
    private val repository: CorridaRepository
) {
    operator fun invoke(): Flow<Corrida?> = repository.observarCorridaAtiva()
}
