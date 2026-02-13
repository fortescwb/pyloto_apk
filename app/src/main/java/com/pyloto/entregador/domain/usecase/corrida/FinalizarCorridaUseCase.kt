package com.pyloto.entregador.domain.usecase.corrida

import com.pyloto.entregador.domain.repository.CorridaRepository
import javax.inject.Inject

class FinalizarCorridaUseCase @Inject constructor(
    private val repository: CorridaRepository
) {
    suspend operator fun invoke(corridaId: String, fotoComprovante: String? = null): Result<Unit> {
        return try {
            repository.finalizarCorrida(corridaId, fotoComprovante)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
