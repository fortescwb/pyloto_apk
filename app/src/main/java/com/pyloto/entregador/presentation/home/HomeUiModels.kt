package com.pyloto.entregador.presentation.home

import com.pyloto.entregador.domain.model.Corrida
import com.pyloto.entregador.domain.model.DailyStats
import com.pyloto.entregador.domain.model.AgendaTrabalho
import com.pyloto.entregador.domain.model.OperationalCapacity
import com.pyloto.entregador.core.util.Constants

data class HomeUiState(
    val isLoading: Boolean = true,
    val modoVisualizacao: HomeModoVisualizacao = HomeModoVisualizacao.PADRAO,
    val corridas: List<Corrida> = emptyList(),
    val localizacaoAtual: HomeLocation? = null,
    val erro: String? = null,
    val isOnline: Boolean = false,
    val cidadeAtual: String = "Ponta Grossa, PR",
    val regiaoAtual: String = "Centro",
    val dailyStats: DailyStats = DailyStats(),
    val dailyGoal: Double = Constants.DEFAULT_DAILY_GOAL,
    val operationalCapacity: OperationalCapacity? = null,
    val agendaTrabalho: AgendaTrabalho? = null,
    val isUpdatingAgenda: Boolean = false
)

enum class HomeModoVisualizacao {
    PADRAO,
    MAPA
}

data class HomeLocation(
    val latitude: Double,
    val longitude: Double
)

sealed class HomeEvent {
    data class CorridaAceita(val corridaId: String) : HomeEvent()
}
