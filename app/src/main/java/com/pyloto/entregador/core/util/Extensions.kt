package com.pyloto.entregador.core.util

import android.content.Context
import android.location.Location
import android.widget.Toast
import java.math.BigDecimal
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

// ==================== String Extensions ====================

fun String.toSafeDouble(): Double = this.toDoubleOrNull() ?: 0.0

fun String.capitalizeWords(): String =
    this.split(" ").joinToString(" ") { word ->
        word.lowercase().replaceFirstChar { it.uppercase() }
    }

fun String.maskCPF(): String {
    if (this.length != 11) return this
    return "${substring(0, 3)}.${substring(3, 6)}.${substring(6, 9)}-${substring(9)}"
}

fun String.maskPhone(): String {
    if (this.length != 11) return this
    return "(${substring(0, 2)}) ${substring(2, 7)}-${substring(7)}"
}

// ==================== BigDecimal / Number Extensions ====================

fun BigDecimal.formatCurrency(): String {
    val format = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    return format.format(this)
}

fun Double.formatCurrency(): String {
    val format = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    return format.format(this)
}

fun Double.formatDistance(): String {
    return if (this < 1.0) {
        "${(this * 1000).toInt()}m"
    } else {
        String.format(Locale("pt", "BR"), "%.1f km", this)
    }
}

// ==================== Date / Time Extensions ====================

fun Long.toFormattedDate(pattern: String = "dd/MM/yyyy"): String {
    val sdf = SimpleDateFormat(pattern, Locale("pt", "BR"))
    return sdf.format(Date(this))
}

fun Long.toFormattedDateTime(): String {
    return this.toFormattedDate("dd/MM/yyyy HH:mm")
}

fun Long.toTimeAgo(): String {
    val now = System.currentTimeMillis()
    val diff = now - this
    return when {
        diff < 60_000 -> "agora"
        diff < 3_600_000 -> "${diff / 60_000}min atrás"
        diff < 86_400_000 -> "${diff / 3_600_000}h atrás"
        else -> "${diff / 86_400_000}d atrás"
    }
}

// ==================== Location Extensions ====================

fun Location.distanceTo(lat: Double, lng: Double): Float {
    val dest = Location("").apply {
        latitude = lat
        longitude = lng
    }
    return this.distanceTo(dest)
}

// ==================== Context Extensions ====================

fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}
