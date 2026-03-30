package com.pyloto.entregador.presentation.corridas.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.pyloto.entregador.presentation.corridas.CorridaComDistancia
import com.pyloto.entregador.presentation.theme.PylotoColors
import kotlin.math.abs
import java.text.NumberFormat
import java.util.Locale

/**
 * Modo Mapa: Google Maps com círculos de raio 250m para cada coleta.
 *
 * A localização exata dos pontos de coleta é ofuscada por um
 * círculo de 250m de raio. O entregador só visualiza o endereço
 * completo após aceitar a solicitação.
 *
 * @param corridasOrdenadas lista de corridas com distância
 * @param entregadorLat latitude do entregador
 * @param entregadorLng longitude do entregador
 * @param onCorridaClick callback ao tocar num card
 */
@Composable
fun CorridasMapView(
    corridasOrdenadas: List<CorridaComDistancia>,
    entregadorLat: Double,
    entregadorLng: Double,
    onCorridaClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val currencyFormatter = remember {
        NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    }

    val entregadorPosition = remember(entregadorLat, entregadorLng) {
        val lat = entregadorLat.takeIf(::isValidLatitude) ?: DEFAULT_LATITUDE
        val lng = entregadorLng.takeIf(::isValidLongitude) ?: DEFAULT_LONGITUDE
        LatLng(lat, lng)
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
        // ══════════════════════════════════════════════════════
        // GOOGLE MAPS COM CÍRCULOS DE RAIO 250m
        // ══════════════════════════════════════════════════════

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            ) {
                // Marcador do entregador
                Marker(
                    state = MarkerState(position = entregadorPosition),
                    title = "Sua localização",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                )

                // Círculos de raio 250m para cada ponto de coleta
                corridasOrdenadas.forEach { item ->
                    val coletaPosition = LatLng(
                        item.corrida.origem.latitude,
                        item.corrida.origem.longitude
                    )

                    val circleColor = if (item.corrida.prioridade) {
                        CIRCLE_COLOR_PRIORITY
                    } else {
                        CIRCLE_COLOR_NORMAL
                    }

                    val strokeColor = if (item.corrida.prioridade) {
                        STROKE_COLOR_PRIORITY
                    } else {
                        STROKE_COLOR_NORMAL
                    }

                    Circle(
                        center = coletaPosition,
                        radius = RADIUS_METERS,
                        fillColor = circleColor,
                        strokeColor = strokeColor,
                        strokeWidth = STROKE_WIDTH
                    )
                }
            }

            // Legenda sobreposta ao mapa
            MapLegend(
                corridasCount = corridasOrdenadas.size,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
            )
        }

        // ══════════════════════════════════════════════════════
        // LISTA RESUMIDA (scroll horizontal) embaixo do mapa
        // ══════════════════════════════════════════════════════

        if (corridasOrdenadas.isNotEmpty()) {
            MapBottomCards(
                corridasOrdenadas = corridasOrdenadas,
                currencyFormatter = currencyFormatter,
                onCorridaClick = onCorridaClick
            )
        }
    }
}

/**
 * Legenda sobreposta ao mapa.
 */
@Composable
private fun MapLegend(
    corridasCount: Int,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.92f)
        ),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "$corridasCount coletas disponíveis",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = PylotoColors.TextPrimary
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                LegendItem(color = PylotoColors.MilitaryGreen, label = "Coleta")
                LegendItem(color = PylotoColors.Gold, label = "Prioridade")
            }
            Text(
                text = "Raio de 250m · endereço exato após aceite",
                style = MaterialTheme.typography.labelSmall,
                color = PylotoColors.TextSecondary
            )
        }
    }
}

/**
 * Item de legenda do mapa.
 */
@Composable
private fun LegendItem(
    color: Color,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, CircleShape)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = PylotoColors.TextSecondary
        )
    }
}

/**
 * Cards compactos horizontais abaixo do mapa (scroll horizontal).
 */
@Composable
private fun MapBottomCards(
    corridasOrdenadas: List<CorridaComDistancia>,
    currencyFormatter: NumberFormat,
    onCorridaClick: (String) -> Unit
) {
    LazyRow(
        contentPadding = PaddingValues(
            horizontal = 16.dp,
            vertical = 8.dp
        ),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        itemsIndexed(
            items = corridasOrdenadas,
            key = { _, item -> item.corrida.id }
        ) { _, item ->
            MapCompactCard(
                bairro = item.corrida.origem.bairro,
                distanciaAteColeta = item.distanciaAteColetaFormatada,
                valor = currencyFormatter.format(item.corrida.valor.toDouble()),
                distanciaTotal = "%.1f km".format(item.corrida.distanciaKm),
                isPrioridade = item.corrida.prioridade,
                onClick = { onCorridaClick(item.corrida.id) }
            )
        }
    }
}

/**
 * Card compacto para scroll horizontal no modo Mapa.
 * Mostra apenas o bairro da coleta, não o endereço completo.
 */
@Composable
private fun MapCompactCard(
    bairro: String,
    distanciaAteColeta: String,
    valor: String,
    distanciaTotal: String,
    isPrioridade: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .height(90.dp)
            .padding(vertical = 2.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = if (isPrioridade)
                            PylotoColors.Gold.copy(alpha = 0.12f)
                        else
                            PylotoColors.MilitaryGreen.copy(alpha = 0.12f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = if (isPrioridade) PylotoColors.Gold else PylotoColors.MilitaryGreen,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Coleta · $bairro",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = PylotoColors.Black,
                    maxLines = 1
                )
                Text(
                    text = "$distanciaAteColeta até coleta · $distanciaTotal total",
                    style = MaterialTheme.typography.labelSmall,
                    color = PylotoColors.TextSecondary,
                    maxLines = 1
                )
            }

            Box(
                modifier = Modifier
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(PylotoColors.Gold, PylotoColors.GoldDark)
                        ),
                        shape = RoundedCornerShape(6.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = valor,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

private fun isValidLatitude(value: Double): Boolean = value.isFinite() && value in -90.0..90.0

private fun isValidLongitude(value: Double): Boolean = value.isFinite() && value in -180.0..180.0

private const val DEFAULT_ZOOM = 14f
private const val CAMERA_ANIMATION_DURATION_MS = 600
private const val CAMERA_RECENTER_EPSILON = 0.0001
private const val DEFAULT_LATITUDE = -25.4284
private const val DEFAULT_LONGITUDE = -49.2733
private const val RADIUS_METERS = 250.0
private const val STROKE_WIDTH = 3f
private val CIRCLE_COLOR_NORMAL = Color(0x3034592A)
private val CIRCLE_COLOR_PRIORITY = Color(0x30C8962A)
private val STROKE_COLOR_NORMAL = Color(0xFF34592A)
private val STROKE_COLOR_PRIORITY = Color(0xFFC8962A)
