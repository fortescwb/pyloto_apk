package com.pyloto.entregador.domain.usecase.corrida

import com.pyloto.entregador.domain.model.Corrida
import com.pyloto.entregador.domain.repository.CorridaRepository
import com.pyloto.entregador.domain.repository.LocationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ObterCorridasDisponiveisUseCase @Inject constructor(
    private val corridaRepository: CorridaRepository,
    private val locationRepository: LocationRepository
) {
    operator fun invoke(raio: Int = 5000): Flow<List<Corrida>> = flow {
        val location = locationRepository.getLastKnownLocation()
        val lat = location?.latitude ?: 0.0
        val lng = location?.longitude ?: 0.0

        val corridas = corridaRepository.getCorridasDisponiveis(
            lat = lat,
            lng = lng,
            raio = raio
        )
        emit(corridas)
    }
}
