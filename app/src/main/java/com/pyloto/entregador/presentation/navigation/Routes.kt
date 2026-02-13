package com.pyloto.entregador.presentation.navigation

/**
 * Definição centralizada de rotas de navegação.
 * Preparada para deep links e navegação aninhada.
 */
object Routes {
    // Auth
    const val LOGIN = "login"
    const val REGISTER = "register"

    // Main
    const val HOME = "home"
    const val CORRIDA_DETALHES = "corrida_detalhes"
    const val CORRIDA_ATIVA = "corrida_ativa"
    const val HISTORICO = "historico"
    const val PERFIL = "perfil"
    const val GANHOS = "ganhos"
    const val CHAT = "chat"
    const val NOTIFICACOES = "notificacoes"
    const val CONFIGURACOES = "configuracoes"

    // Deep link patterns
    fun corridaDetalhes(corridaId: String) = "$CORRIDA_DETALHES/$corridaId"
    fun chat(corridaId: String) = "$CHAT/$corridaId"
}
