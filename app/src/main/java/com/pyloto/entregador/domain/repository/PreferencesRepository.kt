package com.pyloto.entregador.domain.repository

import com.pyloto.entregador.domain.model.ActiveRouteContext
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    fun observeDailyGoal(): Flow<Double>
    fun observeWeeklyGoal(): Flow<Double>
    fun observeActiveRouteContext(): Flow<ActiveRouteContext?>
    suspend fun getActiveRouteContext(): ActiveRouteContext?
    suspend fun saveDailyGoal(value: Double)
    suspend fun saveWeeklyGoal(value: Double)
    suspend fun saveActiveRouteContext(context: ActiveRouteContext)
    suspend fun clearActiveRouteContext()
}
