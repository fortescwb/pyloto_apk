package com.pyloto.entregador.domain.usecase.corrida

import com.pyloto.entregador.domain.repository.CorridaRepository
import javax.inject.Inject

class ColetarCorridaUseCase @Inject constructor(
    private val repository: CorridaRepository
) {
    suspend operator fun invoke(corridaId: String): Result<Unit> {
        return try {
            repository.coletarCorrida(corridaId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
