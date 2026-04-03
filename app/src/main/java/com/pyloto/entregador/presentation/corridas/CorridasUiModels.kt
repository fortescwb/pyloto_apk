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
                CorridaComDistancia(corrida, distanciaAteColetaKm)
            }
            .sortedWith(
                compareBy<CorridaComDistancia>(
                    { it.corrida.rankDispatch ?: Int.MAX_VALUE },
                    { it.distanciaAteColetaKm ?: Double.MAX_VALUE }
                )
            )
}

data class CorridaComDistancia(
    val corrida: Corrida,
    val distanciaAteColetaKm: Double?
) {
    val distanciaAteColetaFormatada: String
        get() {
            val distancia = distanciaAteColetaKm ?: return "--"
            return if (distancia < 1.0) {
                "${(distancia * 1000).toInt()}m"
            } else {
                "%.1fkm".format(distancia)
            }
        }

    /** Distância do percurso (coleta→entrega) em km. */
    val distanciaPercursoKm: Double
        get() = corrida.distanciaTotalM?.takeIf { it > 0 }?.div(1000.0) ?: corrida.distanciaKm

    /** Distância total: parceiro→coleta + coleta→entrega. */
    val distanciaTotalKm: Double
        get() {
            val distanciaAteColeta = distanciaAteColetaKm ?: 0.0
            return distanciaAteColeta + distanciaPercursoKm
        }

    /** Distância total formatada para exibição. */
    val distanciaTotalFormatada: String
        get() = if (distanciaTotalKm < 1.0) {
            "${(distanciaTotalKm * 1000).toInt()}m"
        } else {
            "%.1fkm".format(distanciaTotalKm)
        }

    /** Tempo total estimado: tempo até coleta + tempo do percurso. */
    val tempoTotalMin: Int
        get() {
            return corrida.tempoTotalMin ?: run {
                val etaColeta = corrida.etaAteColetaMin ?: 0
                etaColeta + corrida.tempoEstimadoMin
            }
        }

    /** Ganho por km rodado: valor ÷ (distância até coleta + distância do percurso). */
    val ganhoPorKm: Double
        get() = corrida.ganhoPorKm ?: 0.0
}

