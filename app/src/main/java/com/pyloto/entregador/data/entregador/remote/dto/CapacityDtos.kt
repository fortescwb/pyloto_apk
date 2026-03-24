package com.pyloto.entregador.data.entregador.remote.dto

import com.google.gson.annotations.SerializedName
import com.pyloto.entregador.domain.model.CapacityBucket
import com.pyloto.entregador.domain.model.CapacityCheck
import com.pyloto.entregador.domain.model.CapacityMetrics
import com.pyloto.entregador.domain.model.OperationalCapacity

data class CapacityMetricsResponse(
    @SerializedName("volume_l") val volumeLitros: Double = 0.0,
    @SerializedName("peso_kg") val pesoKg: Double = 0.0,
    @SerializedName("valor_reais") val valorReais: Double = 0.0
)

data class CapacityBucketResponse(
    @SerializedName("volume_l") val volumeLitros: Double = 0.0,
    @SerializedName("peso_kg") val pesoKg: Double = 0.0,
    @SerializedName("valor_reais") val valorReais: Double = 0.0,
    @SerializedName("pedidos") val pedidos: Int = 0
)

data class CapacitySnapshotResponse(
    @SerializedName("partner_id") val partnerId: String = "",
    @SerializedName("bau_capacidade_litros") val bauCapacidadeLitros: Int = 0,
    @SerializedName("policy_version") val policyVersion: String = "",
    @SerializedName("policy_source") val policySource: String = "",
    @SerializedName("near_limit_threshold_ratio") val nearLimitThresholdRatio: Double = 0.0,
    @SerializedName("limits") val limits: CapacityMetricsResponse = CapacityMetricsResponse(),
    @SerializedName("reserved") val reserved: CapacityBucketResponse = CapacityBucketResponse(),
    @SerializedName("in_use") val inUse: CapacityBucketResponse = CapacityBucketResponse(),
    @SerializedName("committed") val committed: CapacityBucketResponse = CapacityBucketResponse(),
    @SerializedName("remaining") val remaining: CapacityMetricsResponse = CapacityMetricsResponse(),
    @SerializedName("usage_ratio") val usageRatio: CapacityMetricsResponse = CapacityMetricsResponse(),
    @SerializedName("near_limit_dimensions") val nearLimitDimensions: List<String> = emptyList(),
    @SerializedName("blocked_dimensions") val blockedDimensions: List<String> = emptyList(),
    @SerializedName("is_near_limit") val isNearLimit: Boolean = false,
    @SerializedName("is_blocked") val isBlocked: Boolean = false,
    @SerializedName("blocked_reason") val blockedReason: String = "",
    @SerializedName("updated_at") val updatedAt: Double = 0.0
)

data class CapacityCheckResponse(
    @SerializedName("pedido_id") val pedidoId: String = "",
    @SerializedName("pedido_status") val pedidoStatus: String = "",
    @SerializedName("fits") val fits: Boolean = false,
    @SerializedName("reason") val reason: String = "",
    @SerializedName("blocking_dimensions") val blockingDimensions: List<String> = emptyList(),
    @SerializedName("near_limit_dimensions_after_acceptance")
    val nearLimitDimensionsAfterAcceptance: List<String> = emptyList(),
    @SerializedName("order_demand") val orderDemand: CapacityMetricsResponse = CapacityMetricsResponse(),
    @SerializedName("capacity_before") val capacityBefore: CapacitySnapshotResponse = CapacitySnapshotResponse(),
    @SerializedName("projected_commitment")
    val projectedCommitment: CapacityMetricsResponse = CapacityMetricsResponse(),
    @SerializedName("projected_remaining")
    val projectedRemaining: CapacityMetricsResponse = CapacityMetricsResponse(),
    @SerializedName("policy_version") val policyVersion: String = "",
    @SerializedName("policy_source") val policySource: String = "",
    @SerializedName("updated_at") val updatedAt: Double = 0.0
)

fun CapacityMetricsResponse.toDomain(): CapacityMetrics {
    return CapacityMetrics(
        volumeLitros = volumeLitros,
        pesoKg = pesoKg,
        valorReais = valorReais
    )
}

fun CapacityBucketResponse.toDomain(): CapacityBucket {
    return CapacityBucket(
        volumeLitros = volumeLitros,
        pesoKg = pesoKg,
        valorReais = valorReais,
        pedidos = pedidos
    )
}

fun CapacitySnapshotResponse.toDomain(): OperationalCapacity {
    return OperationalCapacity(
        partnerId = partnerId,
        bauCapacidadeLitros = bauCapacidadeLitros,
        policyVersion = policyVersion,
        policySource = policySource,
        nearLimitThresholdRatio = nearLimitThresholdRatio,
        limits = limits.toDomain(),
        reserved = reserved.toDomain(),
        inUse = inUse.toDomain(),
        committed = committed.toDomain(),
        remaining = remaining.toDomain(),
        usageRatio = usageRatio.toDomain(),
        nearLimitDimensions = nearLimitDimensions,
        blockedDimensions = blockedDimensions,
        isNearLimit = isNearLimit,
        isBlocked = isBlocked,
        blockedReason = blockedReason,
        updatedAt = updatedAt
    )
}

fun CapacityCheckResponse.toDomain(): CapacityCheck {
    return CapacityCheck(
        pedidoId = pedidoId,
        pedidoStatus = pedidoStatus,
        fits = fits,
        reason = reason,
        blockingDimensions = blockingDimensions,
        nearLimitDimensionsAfterAcceptance = nearLimitDimensionsAfterAcceptance,
        orderDemand = orderDemand.toDomain(),
        capacityBefore = capacityBefore.toDomain(),
        projectedCommitment = projectedCommitment.toDomain(),
        projectedRemaining = projectedRemaining.toDomain(),
        policyVersion = policyVersion,
        policySource = policySource,
        updatedAt = updatedAt
    )
}
