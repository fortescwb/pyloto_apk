package com.pyloto.entregador.core.calendar

import com.pyloto.entregador.domain.model.AgendaDia
import com.pyloto.entregador.domain.model.AgendaTrabalho
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalendarSyncManager @Inject constructor(
    private val permissionChecker: CalendarPermissionChecker,
    private val accountResolver: CalendarAccountResolver,
    private val eventManager: CalendarEventManager,
    private val eventStore: CalendarEventStore
) {

    suspend fun syncSchedule(agenda: AgendaTrabalho) {
        if (!permissionChecker.hasCalendarPermission()) return

        val calendarId = accountResolver.findPrimaryGoogleCalendarId() ?: return

        for (dia in agenda.dias) {
            when (dia.status) {
                "agendado" -> ensureEventExists(calendarId, dia)
                else -> removeEventIfExists(dia)
            }
        }
    }

    suspend fun addEvent(dia: AgendaDia) {
        if (!permissionChecker.hasCalendarPermission()) return
        val calendarId = accountResolver.findPrimaryGoogleCalendarId() ?: return
        ensureEventExists(calendarId, dia)
    }

    suspend fun removeEvent(agendamentoId: String) {
        if (!permissionChecker.hasCalendarPermission()) return

        val existingEventId = eventStore.getEventId(agendamentoId) ?: return
        eventManager.deleteEvent(existingEventId)
        eventStore.removeEventId(agendamentoId)
    }

    private suspend fun ensureEventExists(calendarId: Long, dia: AgendaDia) {
        val agendamentoId = dia.agendamentoId ?: return

        val existingEventId = eventStore.getEventId(agendamentoId)
        if (existingEventId != null) return

        val eventData = buildEventData(dia)
        val newEventId = eventManager.insertEvent(calendarId, eventData) ?: return
        eventStore.saveEventId(agendamentoId, newEventId)
    }

    private suspend fun removeEventIfExists(dia: AgendaDia) {
        val agendamentoId = dia.agendamentoId ?: return
        val existingEventId = eventStore.getEventId(agendamentoId) ?: return
        eventManager.deleteEvent(existingEventId)
        eventStore.removeEventId(agendamentoId)
    }

    private fun buildEventData(dia: AgendaDia): CalendarEventData {
        val date = LocalDate.parse(dia.data)
        val startTime = LocalTime.parse(dia.inicioLocal)
        val endTime = LocalTime.parse(dia.fimLocal)

        val zone = ZoneId.systemDefault()
        val startMillis = LocalDateTime.of(date, startTime).atZone(zone).toInstant().toEpochMilli()
        val endMillis = LocalDateTime.of(date, endTime).atZone(zone).toInstant().toEpochMilli()

        return CalendarEventData(
            title = EVENT_TITLE,
            description = "Turno: ${dia.inicioLocal} às ${dia.fimLocal}",
            startMillis = startMillis,
            endMillis = endMillis
        )
    }

    private companion object {
        const val EVENT_TITLE = "Turno Pyloto - Entrega"
    }
}
