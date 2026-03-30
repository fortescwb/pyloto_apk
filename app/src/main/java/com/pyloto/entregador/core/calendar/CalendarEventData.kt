package com.pyloto.entregador.core.calendar

data class CalendarEventData(
    val title: String,
    val description: String,
    val startMillis: Long,
    val endMillis: Long,
    val reminderMinutesBefore: Int? = DEFAULT_REMINDER_MINUTES
) {
    companion object {
        const val DEFAULT_REMINDER_MINUTES = 30
    }
}
