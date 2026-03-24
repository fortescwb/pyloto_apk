package com.pyloto.entregador.domain.model

data class CapacityMetrics(
    val volumeLitros: Double,
    val pesoKg: Double,
    val valorReais: Double
)

data class CapacityBucket(
    val volumeLitros: Double,
    val pesoKg: Double,
    val valorReais: Double,
    val pedidos: Int
)

data class OperationalCapacity(
    val partnerId: String,
    val bauCapacidadeLitros: Int,
    val policyVersion: String,
    val policySource: String,
    val nearLimitThresholdRatio: Double,
    val limits: CapacityMetrics,
    val reserved: CapacityBucket,
    val inUse: CapacityBucket,
    val committed: CapacityBucket,
    val remaining: CapacityMetrics,
    val usageRatio: CapacityMetrics,
    val nearLimitDimensions: List<String>,
    val blockedDimensions: List<String>,
    val isNearLimit: Boolean,
    val isBlocked: Boolean,
    val blockedReason: String,
    val updatedAt: Double
)

data class CapacityCheck(
    val pedidoId: String,
    val pedidoStatus: String,
    val fits: Boolean,
    val reason: String,
    val blockingDimensions: List<String>,
    val nearLimitDimensionsAfterAcceptance: List<String>,
    val orderDemand: CapacityMetrics,
    val capacityBefore: OperationalCapacity,
    val projectedCommitment: CapacityMetrics,
    val projectedRemaining: CapacityMetrics,
    val policyVersion: String,
    val policySource: String,
    val updatedAt: Double
)
