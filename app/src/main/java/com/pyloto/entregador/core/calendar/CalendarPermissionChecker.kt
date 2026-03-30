package com.pyloto.entregador.core.calendar

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalendarPermissionChecker @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun hasCalendarPermission(): Boolean {
        return hasPermission(Manifest.permission.READ_CALENDAR) &&
            hasPermission(Manifest.permission.WRITE_CALENDAR)
    }

    private fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) ==
            PackageManager.PERMISSION_GRANTED
    }

    companion object {
        val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR
        )
    }
}
