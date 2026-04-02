package com.pyloto.entregador.presentation.corridas.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.pyloto.entregador.presentation.corridas.CorridaComDistancia
import com.pyloto.entregador.presentation.corridas.components.mapa.CAMERA_ANIMATION_DURATION_MS
import com.pyloto.entregador.presentation.corridas.components.mapa.CAMERA_RECENTER_EPSILON
import com.pyloto.entregador.presentation.corridas.components.mapa.CIRCLE_COLOR_NORMAL
import com.pyloto.entregador.presentation.corridas.components.mapa.CIRCLE_COLOR_PRIORITY
import com.pyloto.entregador.presentation.corridas.components.mapa.DEFAULT_ZOOM
import com.pyloto.entregador.presentation.corridas.components.mapa.MapaCardsHorizontais
import com.pyloto.entregador.presentation.corridas.components.mapa.MapaLegenda
import com.pyloto.entregador.presentation.corridas.components.mapa.RADIUS_METERS
import com.pyloto.entregador.presentation.corridas.components.mapa.STROKE_COLOR_NORMAL
import com.pyloto.entregador.presentation.corridas.components.mapa.STROKE_COLOR_PRIORITY
import com.pyloto.entregador.presentation.corridas.components.mapa.STROKE_WIDTH
import com.pyloto.entregador.presentation.corridas.components.mapa.createMotorcycleMarkerIcon
import com.pyloto.entregador.presentation.corridas.components.mapa.resolveEntregadorPosition
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs

/**
 * Modo Mapa: Google Maps com círculos de raio 250m para cada coleta.
 *
 * A localização exata dos pontos de coleta é ofuscada por um
 * círculo de 250m de raio. O entregador só visualiza o endereço
 * completo após aceitar a solicitação.
 *
 * Composição delegada:
 * - Constantes visuais e geográficas → [mapa.MapaConstants]
 * - Ícone e validação do marcador → [mapa.MapaEntregadorMarker]
 * - Legenda sobreposta → [mapa.MapaLegenda]
 * - Cards horizontais de resumo → [mapa.MapaCardsHorizontais]
 */
@Composable
fun CorridasMapView(
    corridasOrdenadas: List<CorridaComDistancia>,
    entregadorLat: Double,
    entregadorLng: Double,
    onCorridaClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currencyFormatter = remember {
        NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    }
    val motorcycleMarkerIcon = remember(context) {
        createMotorcycleMarkerIcon(context)
    }

    val entregadorPosition = remember(entregadorLat, entregadorLng) {
        resolveEntregadorPosition(entregadorLat, entregadorLng)
    }

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(entregadorPosition, DEFAULT_ZOOM)
    }

    LaunchedEffect(entregadorPosition) {
        val current = cameraPositionState.position.target
        val shouldRecenter =
            abs(current.latitude - entregadorPosition.latitude) > CAMERA_RECENTER_EPSILON ||
                abs(current.longitude - entregadorPosition.longitude) > CAMERA_RECENTER_EPSILON
        if (shouldRecenter) {
            runCatching {
                cameraPositionState.animate(
                    update = CameraUpdateFactory.newLatLngZoom(entregadorPosition, DEFAULT_ZOOM),
                    durationMs = CAMERA_ANIMATION_DURATION_MS
                )
            }
        }
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            ) {
                Marker(
                    state = MarkerState(position = entregadorPosition),
                    title = "Sua localização",
                    icon = motorcycleMarkerIcon
                )

                corridasOrdenadas.forEach { item ->
                    val coletaPosition = LatLng(
                        item.corrida.origem.latitude,
                        item.corrida.origem.longitude
                    )

                    Circle(
                        center = coletaPosition,
                        radius = RADIUS_METERS,
                        fillColor = if (item.corrida.prioridade) CIRCLE_COLOR_PRIORITY else CIRCLE_COLOR_NORMAL,
                        strokeColor = if (item.corrida.prioridade) STROKE_COLOR_PRIORITY else STROKE_COLOR_NORMAL,
                        strokeWidth = STROKE_WIDTH
                    )
                }
            }

            MapaLegenda(
                corridasCount = corridasOrdenadas.size,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
            )
        }

        if (corridasOrdenadas.isNotEmpty()) {
            MapaCardsHorizontais(
                corridasOrdenadas = corridasOrdenadas,
                currencyFormatter = currencyFormatter,
                onCorridaClick = onCorridaClick
            )
        }
    }
}
