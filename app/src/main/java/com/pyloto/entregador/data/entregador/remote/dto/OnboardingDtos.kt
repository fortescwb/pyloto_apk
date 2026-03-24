package com.pyloto.entregador.data.entregador.remote.dto

import com.google.gson.annotations.SerializedName
import com.pyloto.entregador.domain.model.OnboardingStatus

data class OnboardingStatusResponse(
    @SerializedName("requires_digital_contract_signature")
    val requiresDigitalContractSignature: Boolean = false,
    @SerializedName("pronto_para_operacao")
    val prontoParaOperacao: Boolean = false,
    @SerializedName("status_cadastral")
    val statusCadastral: String = "",
    @SerializedName("status_operacional")
    val statusOperacional: String = "",
    @SerializedName("bloqueio_operacional")
    val bloqueioOperacional: Boolean = false,
    @SerializedName("motivo_bloqueio_operacional")
    val motivoBloqueioOperacional: String = "",
    @SerializedName("document_alerts")
    val documentAlerts: List<String> = emptyList(),
    @SerializedName("document_blockers")
    val documentBlockers: List<String> = emptyList(),
    @SerializedName("vehicle_audit_required")
    val vehicleAuditRequired: Boolean = false,
    @SerializedName("vehicle_audit_incident_id")
    val vehicleAuditIncidentId: String = "",
    @SerializedName("treinamento_concluido")
    val treinamentoConcluido: Boolean = false,
    @SerializedName("contrato_versao")
    val contratoVersao: String = "",
    @SerializedName("contrato_download_ref")
    val contratoDownloadRef: String = "",
    @SerializedName("contrato_assinatura_digital_concluida")
    val contratoAssinaturaDigitalConcluida: Boolean = false,
    @SerializedName("contrato_assinatura_digital_ref")
    val contratoAssinaturaDigitalRef: String = "",
    @SerializedName("contrato_assinatura_digital_enviada_em")
    val contratoAssinaturaDigitalEnviadaEm: String = "",
    @SerializedName("pending_reason")
    val pendingReason: String? = null
)

data class SubmitDigitalContractSignatureRequest(
    @SerializedName("assinatura_digital_ref")
    val assinaturaDigitalRef: String
)

data class SubmitVehicleAuditRequest(
    @SerializedName("incident_id")
    val incidentId: String,
    @SerializedName("foto_veiculo_ref")
    val fotoVeiculoRef: String,
    @SerializedName("placa_informada")
    val placaInformada: String? = null
)

fun OnboardingStatusResponse.toDomain(): OnboardingStatus {
    return OnboardingStatus(
        requiresDigitalContractSignature = requiresDigitalContractSignature,
        prontoParaOperacao = prontoParaOperacao,
        statusCadastral = statusCadastral,
        statusOperacional = statusOperacional,
        bloqueioOperacional = bloqueioOperacional,
        motivoBloqueioOperacional = motivoBloqueioOperacional,
        documentAlerts = documentAlerts,
        documentBlockers = documentBlockers,
        vehicleAuditRequired = vehicleAuditRequired,
        vehicleAuditIncidentId = vehicleAuditIncidentId,
        treinamentoConcluido = treinamentoConcluido,
        contratoVersao = contratoVersao,
        contratoDownloadRef = contratoDownloadRef,
        contratoAssinaturaDigitalConcluida = contratoAssinaturaDigitalConcluida,
        contratoAssinaturaDigitalRef = contratoAssinaturaDigitalRef,
        contratoAssinaturaDigitalEnviadaEm = contratoAssinaturaDigitalEnviadaEm,
        pendingReason = pendingReason
    )
}
