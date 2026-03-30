package com.pyloto.entregador.core.calendar

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.CalendarContract
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalendarEventManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun insertEvent(calendarId: Long, event: CalendarEventData): Long? {
        val contentResolver: ContentResolver = context.contentResolver

        val values = ContentValues().apply {
            put(CalendarContract.Events.CALENDAR_ID, calendarId)
            put(CalendarContract.Events.TITLE, event.title)
            put(CalendarContract.Events.DESCRIPTION, event.description)
            put(CalendarContract.Events.DTSTART, event.startMillis)
            put(CalendarContract.Events.DTEND, event.endMillis)
            put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().id)
            put(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
        }

        val uri: Uri? = contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
        val eventId = uri?.lastPathSegment?.toLongOrNull() ?: return null

        if (event.reminderMinutesBefore != null) {
            insertReminder(contentResolver, eventId, event.reminderMinutesBefore)
        }

        return eventId
    }

    fun deleteEvent(eventId: Long): Boolean {
        val contentResolver: ContentResolver = context.contentResolver
        val deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId)
        val rowsDeleted = contentResolver.delete(deleteUri, null, null)
        return rowsDeleted > 0
    }

    private fun insertReminder(contentResolver: ContentResolver, eventId: Long, minutesBefore: Int) {
        val reminderValues = ContentValues().apply {
            put(CalendarContract.Reminders.EVENT_ID, eventId)
            put(CalendarContract.Reminders.MINUTES, minutesBefore)
            put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT)
        }
        contentResolver.insert(CalendarContract.Reminders.CONTENT_URI, reminderValues)
    }
}
