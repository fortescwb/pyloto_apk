package com.pyloto.entregador.domain.repository

import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {
    fun observeDailyGoal(): Flow<Double>
    fun observeWeeklyGoal(): Flow<Double>
    suspend fun saveDailyGoal(value: Double)
    suspend fun saveWeeklyGoal(value: Double)
}

