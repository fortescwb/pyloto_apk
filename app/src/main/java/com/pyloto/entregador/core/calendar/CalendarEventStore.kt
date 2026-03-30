package com.pyloto.entregador.core.calendar

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.calendarEventStore: DataStore<Preferences> by preferencesDataStore(
    name = "pyloto_calendar_events"
)

@Singleton
class CalendarEventStore @Inject constructor(
    @ApplicationContext private val context: Context
) {

    suspend fun saveEventId(agendamentoId: String, calendarEventId: Long) {
        context.calendarEventStore.edit { prefs ->
            prefs[keyFor(agendamentoId)] = calendarEventId
        }
    }

    suspend fun getEventId(agendamentoId: String): Long? {
        return context.calendarEventStore.data
            .map { prefs -> prefs[keyFor(agendamentoId)] }
            .firstOrNull()
    }

    suspend fun removeEventId(agendamentoId: String) {
        context.calendarEventStore.edit { prefs ->
            prefs.remove(keyFor(agendamentoId))
        }
    }

    suspend fun clear() {
        context.calendarEventStore.edit { it.clear() }
    }

    private fun keyFor(agendamentoId: String): Preferences.Key<Long> {
        return longPreferencesKey("cal_event_$agendamentoId")
    }
}
