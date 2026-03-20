package com.pyloto.entregador.presentation.perfil

import com.pyloto.entregador.core.util.Constants
import com.pyloto.entregador.domain.model.Entregador

data class PerfilUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val entregador: Entregador? = null,
    val metaSemanal: Double = Constants.DEFAULT_WEEKLY_GOAL,
    val erro: String? = null
)

sealed class PerfilEvent {
    data object DadosSalvos : PerfilEvent()
    data object LogoutRealizado : PerfilEvent()
}

