package com.pyloto.entregador.domain.usecase.corrida

import com.pyloto.entregador.domain.model.Corrida
import com.pyloto.entregador.domain.model.CorridaOperationalEvent
import com.pyloto.entregador.domain.repository.CorridaRepository
import javax.inject.Inject

class RegistrarEventoOperacionalCorridaUseCase @Inject constructor(
    private val repository: CorridaRepository
) {
    suspend operator fun invoke(
        corridaId: String,
        event: CorridaOperationalEvent
    ): Result<Corrida> {
        return try {
            Result.success(repository.registrarEventoOperacional(corridaId, event))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
