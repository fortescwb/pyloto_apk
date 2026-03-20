package com.pyloto.entregador.domain.model

/**
 * Estatisticas diarias do entregador.
 *
 * Esse modelo e preenchido a partir do backend e/ou calculos locais
 * da camada de aplicacao.
 */
data class DailyStats(
    val earnings: Double = 0.0,
    val deliveries: Int = 0,
    val timeOnlineMinutes: Int = 0,
    val maxTimeMinutes: Int = 600,
    val totalFeeSavings: Double = 0.0,
    val averagePerHour: Double = 0.0
) {
    val timeOnlineFormatted: String
        get() {
            val hours = timeOnlineMinutes / 60
            val minutes = timeOnlineMinutes % 60
            return "${hours}h ${minutes}m"
        }

    val timeRemainingFormatted: String
        get() {
            val remaining = (maxTimeMinutes - timeOnlineMinutes).coerceAtLeast(0)
            val hours = remaining / 60
            val minutes = remaining % 60
            return "${hours}h ${minutes}m"
        }

    fun goalProgress(goal: Double): Float {
        if (goal <= 0) return 0f
        return ((earnings / goal) * 100).coerceIn(0.0, 100.0).toFloat()
    }

    fun remainingToGoal(goal: Double): Double {
        return (goal - earnings).coerceAtLeast(0.0)
    }
}
