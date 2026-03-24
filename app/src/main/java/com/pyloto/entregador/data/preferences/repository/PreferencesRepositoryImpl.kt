package com.pyloto.entregador.data.preferences.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.pyloto.entregador.core.util.Constants
import com.pyloto.entregador.domain.model.ActiveRouteContext
import com.pyloto.entregador.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
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

    override fun observeActiveRouteContext(): Flow<ActiveRouteContext?> {
        return dataStore.data.map { preferences ->
            buildActiveRouteContext(preferences)
        }
    }

    override suspend fun getActiveRouteContext(): ActiveRouteContext? {
        return buildActiveRouteContext(dataStore.data.first())
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

    override suspend fun saveActiveRouteContext(context: ActiveRouteContext) {
        dataStore.edit { preferences ->
            preferences[ACTIVE_ROUTE_PEDIDO_ID_KEY] = context.pedidoId
            preferences[ACTIVE_ROUTE_PHASE_KEY] = context.phase
            preferences[ACTIVE_ROUTE_STARTED_AT_KEY] = context.startedAt
        }
    }

    override suspend fun clearActiveRouteContext() {
        dataStore.edit { preferences ->
            preferences.remove(ACTIVE_ROUTE_PEDIDO_ID_KEY)
            preferences.remove(ACTIVE_ROUTE_PHASE_KEY)
            preferences.remove(ACTIVE_ROUTE_STARTED_AT_KEY)
        }
    }

    private fun buildActiveRouteContext(preferences: Preferences): ActiveRouteContext? {
        val pedidoId = preferences[ACTIVE_ROUTE_PEDIDO_ID_KEY]?.trim().orEmpty()
        if (pedidoId.isEmpty()) return null

        return ActiveRouteContext(
            pedidoId = pedidoId,
            phase = preferences[ACTIVE_ROUTE_PHASE_KEY]?.trim().orEmpty(),
            startedAt = preferences[ACTIVE_ROUTE_STARTED_AT_KEY] ?: System.currentTimeMillis()
        )
    }

    private companion object {
        val DAILY_GOAL_KEY = doublePreferencesKey(Constants.PREF_DAILY_GOAL)
        val WEEKLY_GOAL_KEY = doublePreferencesKey(Constants.PREF_WEEKLY_GOAL)
        val ACTIVE_ROUTE_PEDIDO_ID_KEY = stringPreferencesKey(Constants.PREF_ACTIVE_ROUTE_PEDIDO_ID)
        val ACTIVE_ROUTE_PHASE_KEY = stringPreferencesKey(Constants.PREF_ACTIVE_ROUTE_PHASE)
        val ACTIVE_ROUTE_STARTED_AT_KEY = longPreferencesKey(Constants.PREF_ACTIVE_ROUTE_STARTED_AT)
    }
}
