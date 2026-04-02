package com.pyloto.entregador.presentation.corridas.components.mapa

import androidx.compose.ui.graphics.Color

/** Zoom padrão do mapa (nível de rua). */
const val DEFAULT_ZOOM = 14f

/** Duração da animação de recentragem da câmera em milissegundos. */
const val CAMERA_ANIMATION_DURATION_MS = 600

/** Epsilon mínimo para disparar recentragem da câmera. */
const val CAMERA_RECENTER_EPSILON = 0.0001

/** Latitude fallback quando o dispositivo não tem posição conhecida (Curitiba). */
const val DEFAULT_LATITUDE = -25.4284

/** Longitude fallback quando o dispositivo não tem posição conhecida (Curitiba). */
const val DEFAULT_LONGITUDE = -49.2733

/** Raio de ofuscação aplicado ao ponto de coleta no mapa (metros). */
const val RADIUS_METERS = 250.0

/** Espessura do traço do círculo de ofuscação. */
const val STROKE_WIDTH = 3f

/** Tamanho do ícone do marcador do entregador em pixels. */
const val MARKER_ICON_SIZE_PX = 64

// ── Cores dos círculos de ofuscação ─────────────────────────

val CIRCLE_COLOR_NORMAL = Color(0x3034592A)
val CIRCLE_COLOR_PRIORITY = Color(0x30C8962A)
val STROKE_COLOR_NORMAL = Color(0xFF34592A)
val STROKE_COLOR_PRIORITY = Color(0xFFC8962A)
