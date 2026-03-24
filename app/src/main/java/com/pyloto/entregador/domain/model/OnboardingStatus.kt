package com.pyloto.entregador.domain.model

data class OnboardingStatus(
    val requiresDigitalContractSignature: Boolean,
    val prontoParaOperacao: Boolean,
    val statusCadastral: String,
    val statusOperacional: String,
    val bloqueioOperacional: Boolean,
    val motivoBloqueioOperacional: String,
    val documentAlerts: List<String>,
    val documentBlockers: List<String>,
    val vehicleAuditRequired: Boolean,
    val vehicleAuditIncidentId: String,
    val treinamentoConcluido: Boolean,
    val contratoVersao: String,
    val contratoDownloadRef: String,
    val contratoAssinaturaDigitalConcluida: Boolean,
    val contratoAssinaturaDigitalRef: String,
    val contratoAssinaturaDigitalEnviadaEm: String,
    val pendingReason: String?
)
