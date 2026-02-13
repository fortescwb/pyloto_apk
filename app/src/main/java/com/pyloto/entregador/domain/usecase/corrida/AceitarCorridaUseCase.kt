package com.pyloto.entregador.domain.usecase.corrida

import com.pyloto.entregador.domain.model.Corrida
import com.pyloto.entregador.domain.repository.CorridaRepository
import javax.inject.Inject

class AceitarCorridaUseCase @Inject constructor(
    private val repository: CorridaRepository
) {
    suspend operator fun invoke(corridaId: String): Result<Corrida> {
        return try {
            val corrida = repository.aceitarCorrida(corridaId)
            Result.success(corrida)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
