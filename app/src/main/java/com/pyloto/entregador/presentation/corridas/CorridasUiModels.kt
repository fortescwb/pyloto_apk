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
        get() = corridas
            .map { corrida ->
                val distanciaAteColetaKm = corrida.distanciaAteColetaM
                    ?.takeIf { it >= 0 }
                    ?.let { it / 1000.0 }
                    ?: haversineKm(
                        entregadorLat, entregadorLng,
                        corrida.origem.latitude, corrida.origem.longitude
                    )
                CorridaComDistancia(corrida, distanciaAteColetaKm)
            }
            .sortedWith(
                compareBy<CorridaComDistancia>(
                    { it.corrida.rankDispatch ?: Int.MAX_VALUE },
                    { it.distanciaAteColetaKm }
                )
            )
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

    /** Distância do percurso (coleta→entrega) em km. */
    val distanciaPercursoKm: Double
        get() = corrida.distanciaKm

    /** Tempo total estimado: tempo até coleta + tempo do percurso. */
    val tempoTotalMin: Int
        get() {
            val etaColeta = corrida.etaAteColetaMin ?: 0
            return etaColeta + corrida.tempoEstimadoMin
        }

    /** Ganho por km rodado: valor ÷ (distância até coleta + distância do percurso). */
    val ganhoPorKm: Double
        get() {
            val totalKm = distanciaAteColetaKm + distanciaPercursoKm
            return if (totalKm > 0) corrida.valor.toDouble() / totalKm else 0.0
        }
}

