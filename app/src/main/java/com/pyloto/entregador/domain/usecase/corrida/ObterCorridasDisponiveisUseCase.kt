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
        val location = locationRepository.getCurrentOrLastKnownLocation()
        val lat = location?.latitude ?: DEFAULT_LAT
        val lng = location?.longitude ?: DEFAULT_LNG

        val corridas = corridaRepository.getCorridasDisponiveis(
            lat = lat,
            lng = lng,
            raio = raio
        )
        emit(corridas)
    }

    private companion object {
        const val DEFAULT_LAT = -25.4284
        const val DEFAULT_LNG = -49.2733
    }
}
