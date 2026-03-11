package com.pyloto.entregador.domain.model

/**
 * Estatísticas diárias do entregador.
 *
 * Por ora, utiliza dados mockados (scaffold) enquanto o endpoint de
 * estatísticas do CORE não está disponível. Todos os campos são
 * calculáveis a partir de dados persistidos localmente ou provenientes
 * do backend quando a integração estiver pronta.
 */
data class DailyStats(
    /** Ganhos acumulados no dia em R$ */
    val earnings: Double = 0.0,

    /** Número de entregas finalizadas no dia */
    val deliveries: Int = 0,

    /** Tempo online em minutos */
    val timeOnlineMinutes: Int = 0,

    /** Tempo máximo configurável de jornada em minutos (padrão 10h) */
    val maxTimeMinutes: Int = 600,

    /** Economia total em taxas vs concorrentes */
    val totalFeeSavings: Double = 0.0,

    /** Média de ganhos por hora trabalhada */
    val averagePerHour: Double = 0.0
) {
    /** Tempo online formatado para exibição (ex: "5h 32m") */
    val timeOnlineFormatted: String
        get() {
            val hours = timeOnlineMinutes / 60
            val minutes = timeOnlineMinutes % 60
            return "${hours}h ${minutes}m"
        }

    /** Tempo restante formatado para exibição (ex: "4h 28m") */
    val timeRemainingFormatted: String
        get() {
            val remaining = (maxTimeMinutes - timeOnlineMinutes).coerceAtLeast(0)
            val hours = remaining / 60
            val minutes = remaining % 60
            return "${hours}h ${minutes}m"
        }

    /** Progresso em direção à meta diária (0-100) */
    fun goalProgress(goal: Double): Float {
        if (goal <= 0) return 0f
        return ((earnings / goal) * 100).coerceIn(0.0, 100.0).toFloat()
    }

    /** Valor faltante para atingir a meta */
    fun remainingToGoal(goal: Double): Double {
        return (goal - earnings).coerceAtLeast(0.0)
    }
}
