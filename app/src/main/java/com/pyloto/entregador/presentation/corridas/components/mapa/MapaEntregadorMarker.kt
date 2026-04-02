package com.pyloto.entregador.presentation.corridas.components.mapa

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.pyloto.entregador.R

/**
 * Cria o [BitmapDescriptor] do ícone de moto usado para marcar
 * a posição do entregador no mapa.
 */
fun createMotorcycleMarkerIcon(context: Context): BitmapDescriptor {
    val drawable = checkNotNull(ContextCompat.getDrawable(context, R.drawable.ic_motorcycle_marker))
    val bitmap = Bitmap.createBitmap(MARKER_ICON_SIZE_PX, MARKER_ICON_SIZE_PX, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, MARKER_ICON_SIZE_PX, MARKER_ICON_SIZE_PX)
    drawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bitmap)
}

/**
 * Resolve a posição do entregador, caindo para coordenadas padrão
 * quando latitude ou longitude são inválidas.
 */
fun resolveEntregadorPosition(lat: Double, lng: Double): LatLng {
    val safeLat = lat.takeIf(::isValidLatitude) ?: DEFAULT_LATITUDE
    val safeLng = lng.takeIf(::isValidLongitude) ?: DEFAULT_LONGITUDE
    return LatLng(safeLat, safeLng)
}

private fun isValidLatitude(value: Double): Boolean = value.isFinite() && value in -90.0..90.0

private fun isValidLongitude(value: Double): Boolean = value.isFinite() && value in -180.0..180.0
