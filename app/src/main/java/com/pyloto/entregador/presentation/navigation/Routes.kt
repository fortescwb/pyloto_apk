package com.pyloto.entregador.presentation.navigation

object Routes {
    const val LOGIN = "login"
    const val CONTRACT_SIGNATURE = "contract_signature"

    const val HOME = "home"
    const val CORRIDAS = "corridas"
    const val CORRIDA_DETALHES = "corrida_detalhes"
    const val CORRIDA_ATIVA = "corrida_ativa"
    const val HISTORICO = "historico"
    const val PERFIL = "perfil"
    const val GANHOS = "ganhos"
    const val CHAT = "chat"
    const val NOTIFICACOES = "notificacoes"
    const val CONFIGURACOES = "configuracoes"

    fun corridaDetalhes(corridaId: String) = "$CORRIDA_DETALHES/$corridaId"
    fun corridaAtiva(corridaId: String) = "$CORRIDA_ATIVA/$corridaId"
    fun chat(corridaId: String) = "$CHAT/$corridaId"
}
