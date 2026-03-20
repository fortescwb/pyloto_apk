package com.pyloto.entregador.data.preferences.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import com.pyloto.entregador.core.util.Constants
import com.pyloto.entregador.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : PreferencesRepository {

    override fun observeDailyGoal(): Flow<Double> {
        return dataStore.data.map { preferences ->
            preferences[DAILY_GOAL_KEY] ?: Constants.DEFAULT_DAILY_GOAL
        }
    }

    override fun observeWeeklyGoal(): Flow<Double> {
        return dataStore.data.map { preferences ->
            preferences[WEEKLY_GOAL_KEY] ?: Constants.DEFAULT_WEEKLY_GOAL
        }
    }

    override suspend fun saveDailyGoal(value: Double) {
        dataStore.edit { preferences ->
            preferences[DAILY_GOAL_KEY] = value.coerceAtLeast(0.0)
        }
    }

    override suspend fun saveWeeklyGoal(value: Double) {
        dataStore.edit { preferences ->
            preferences[WEEKLY_GOAL_KEY] = value.coerceAtLeast(0.0)
        }
    }

    private companion object {
        val DAILY_GOAL_KEY = doublePreferencesKey(Constants.PREF_DAILY_GOAL)
        val WEEKLY_GOAL_KEY = doublePreferencesKey(Constants.PREF_WEEKLY_GOAL)
    }
}

