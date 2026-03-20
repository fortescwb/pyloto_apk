package com.pyloto.entregador.domain.usecase.home

import com.pyloto.entregador.domain.model.DailyStats
import com.pyloto.entregador.domain.usecase.entregador.ObterGanhosUseCase
import java.math.BigDecimal
import javax.inject.Inject

class ObterDailyStatsUseCase @Inject constructor(
    private val obterGanhosUseCase: ObterGanhosUseCase
) {
    suspend operator fun invoke(): DailyStats {
        val ganhos = obterGanhosUseCase(periodo = "DIARIO")
        val feeSavings = ganhos.totalBruto.subtract(ganhos.totalLiquido).coerceAtLeast(BigDecimal.ZERO)

        return DailyStats(
            earnings = ganhos.totalLiquido.toDouble(),
            deliveries = ganhos.totalCorridas,
            timeOnlineMinutes = 0,
            totalFeeSavings = feeSavings.toDouble(),
            averagePerHour = 0.0
        )
    }
}

