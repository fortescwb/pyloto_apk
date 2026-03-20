package com.pyloto.entregador.presentation.corridas

import com.pyloto.entregador.domain.model.Corrida

enum class CorridasViewMode { LISTA, MAPA }

data class CorridasUiState(
    val isLoading: Boolean = true,
    val corridas: List<Corrida> = emptyList(),
    val viewMode: CorridasViewMode = CorridasViewMode.LISTA,
    val entregadorLat: Double = -25.4284,
    val entregadorLng: Double = -49.2733,
    val erro: String? = null
) {
    val corridasOrdenadas: List<CorridaComDistancia>
        get() = corridas.map { corrida ->
            val distanciaAteColeta = haversineKm(
                entregadorLat, entregadorLng,
                corrida.origem.latitude, corrida.origem.longitude
            )
            CorridaComDistancia(corrida, distanciaAteColeta)
        }.sortedBy { it.distanciaAteColetaKm }
}

data class CorridaComDistancia(
    val corrida: Corrida,
    val distanciaAteColetaKm: Double
) {
    val distanciaAteColetaFormatada: String
        get() = if (distanciaAteColetaKm < 1.0) {
            "${(distanciaAteColetaKm * 1000).toInt()}m"
        } else {
            "%.1fkm".format(distanciaAteColetaKm)
        }
}

