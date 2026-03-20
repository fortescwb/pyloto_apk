package com.pyloto.entregador.presentation.ganhos

import com.pyloto.entregador.domain.model.CorridaStatus
import com.pyloto.entregador.domain.model.Ganhos

enum class PeriodoGanhos(val label: String) {
    HOJE("Hoje"),
    SEMANA("Semana"),
    MES("Mes"),
    TOTAL("Total");

    fun toApiPeriodo(): String {
        return when (this) {
            HOJE -> "DIARIO"
            SEMANA -> "SEMANAL"
            MES -> "MENSAL"
            TOTAL -> "TOTAL"
        }
    }
}

data class GanhosUiState(
    val isLoading: Boolean = true,
    val periodoSelecionado: PeriodoGanhos = PeriodoGanhos.SEMANA,
    val ganhos: Ganhos? = null,
    val corridasRealizadas: List<CorridaRealizada> = emptyList(),
    val totalKmRodados: Double = 0.0,
    val tempoOnlineMinutos: Int = 0,
    val ganhoPorKm: Double = 0.0,
    val ganhoPorHora: Double = 0.0,
    val erro: String? = null
)

data class CorridaRealizada(
    val id: String,
    val clienteNome: String,
    val origemBairro: String,
    val destinoBairro: String,
    val valor: Double,
    val distanciaKm: Double,
    val tempoMin: Int,
    val dataHora: String,
    val status: CorridaStatus
)

