package com.pyloto.entregador.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ─── Material3 Color Schemes ───────────────────────────────────────

private val LightColorScheme = lightColorScheme(
    // Primary — Azul Técnico (navegação, interação, tab bar ativa)
    primary = PylotoColors.TechBlue,
    onPrimary = PylotoColors.White,
    primaryContainer = PylotoColors.TechBlueDark,
    onPrimaryContainer = PylotoColors.White,
    inversePrimary = PylotoColors.TechBlueLight,

    // Secondary — Dourado (CTAs, gamificação, ganhos, badges premium)
    secondary = PylotoColors.Gold,
    onSecondary = PylotoColors.Black,
    secondaryContainer = PylotoColors.GoldLight,
    onSecondaryContainer = PylotoColors.Black,

    // Tertiary — Verde Militar (aprovado, confirmado, positivo)
    tertiary = PylotoColors.MilitaryGreen,
    onTertiary = PylotoColors.White,
    tertiaryContainer = PylotoColors.GreenDark,
    onTertiaryContainer = PylotoColors.White,

    // Background & Surface
    background = PylotoColors.White,
    onBackground = PylotoColors.TextPrimary,
    surface = PylotoColors.White,
    onSurface = PylotoColors.TextPrimary,
    surfaceVariant = PylotoColors.SurfaceDim,
    onSurfaceVariant = PylotoColors.TextSecondary,
    surfaceTint = PylotoColors.TechBlue,

    // Outline
    outline = PylotoColors.Outline,
    outlineVariant = PylotoColors.OutlineVariant,

    // Error
    error = PylotoColors.StatusRejected,
    onError = PylotoColors.OnError,
    errorContainer = Color(0xFFFCE4EC),
    onErrorContainer = PylotoColors.ErrorDark,

    // Misc
    scrim = PylotoColors.Scrim,
    inverseSurface = PylotoColors.Black,
    inverseOnSurface = PylotoColors.TextOnDark
)

private val DarkColorScheme = darkColorScheme(
    primary = PylotoColors.Dark.Primary,
    onPrimary = PylotoColors.Black,
    primaryContainer = PylotoColors.TechBlue,
    onPrimaryContainer = PylotoColors.White,
    inversePrimary = PylotoColors.TechBlue,

    secondary = PylotoColors.Dark.Secondary,
    onSecondary = PylotoColors.Black,
    secondaryContainer = PylotoColors.GoldDark,
    onSecondaryContainer = PylotoColors.White,

    tertiary = PylotoColors.Dark.Tertiary,
    onTertiary = PylotoColors.Black,
    tertiaryContainer = PylotoColors.MilitaryGreen,
    onTertiaryContainer = PylotoColors.White,

    background = PylotoColors.Dark.Background,
    onBackground = PylotoColors.Dark.TextPrimary,
    surface = PylotoColors.Dark.Surface,
    onSurface = PylotoColors.Dark.TextPrimary,
    surfaceVariant = PylotoColors.Dark.SurfaceHigh,
    onSurfaceVariant = PylotoColors.Dark.TextSecondary,

    error = PylotoColors.Dark.Error,
    onError = PylotoColors.Black,

    outline = Color(0xFF6B6B6B),
    outlineVariant = Color(0xFF4A4A4A),
    scrim = PylotoColors.Scrim,
    inverseSurface = PylotoColors.White,
    inverseOnSurface = PylotoColors.Black
)

// ─── Extended Colors (fora do M3 color scheme) ─────────────────────

/**
 * Cores estendidas da Pyloto que não têm mapeamento direto no M3.
 * Acessíveis via [PylotoTheme.extendedColors].
 */
@Immutable
data class PylotoExtendedColors(
    val gold: Color,
    val goldDark: Color,
    val goldLight: Color,
    val sepia: Color,
    val militaryGreen: Color,
    val techBlue: Color,
    val parchment: Color,
    val statusApproved: Color,
    val statusPending: Color,
    val statusRejected: Color,
    val statusInfo: Color,
    val textDisabled: Color,
    val overlayDark: Color,
    val overlayLight: Color
)

private val LightExtendedColors = PylotoExtendedColors(
    gold = PylotoColors.Gold,
    goldDark = PylotoColors.GoldDark,
    goldLight = PylotoColors.GoldLight,
    sepia = PylotoColors.Sepia,
    militaryGreen = PylotoColors.MilitaryGreen,
    techBlue = PylotoColors.TechBlue,
    parchment = PylotoColors.Parchment,
    statusApproved = PylotoColors.StatusApproved,
    statusPending = PylotoColors.StatusPending,
    statusRejected = PylotoColors.StatusRejected,
    statusInfo = PylotoColors.StatusInfo,
    textDisabled = PylotoColors.TextDisabled,
    overlayDark = PylotoColors.OverlayDark,
    overlayLight = PylotoColors.OverlayLight
)

private val DarkExtendedColors = PylotoExtendedColors(
    gold = PylotoColors.Gold,
    goldDark = PylotoColors.GoldDark,
    goldLight = PylotoColors.GoldLight,
    sepia = PylotoColors.Sepia,
    militaryGreen = PylotoColors.Dark.Tertiary,
    techBlue = PylotoColors.Dark.Primary,
    parchment = PylotoColors.Dark.Surface,
    statusApproved = PylotoColors.Dark.Tertiary,
    statusPending = PylotoColors.Sepia,
    statusRejected = PylotoColors.Dark.Error,
    statusInfo = PylotoColors.Dark.Primary,
    textDisabled = Color(0xFF616161),
    overlayDark = PylotoColors.OverlayDark,
    overlayLight = PylotoColors.OverlayLight
)

val LocalPylotoExtendedColors = staticCompositionLocalOf { LightExtendedColors }

// ─── Theme Composable ──────────────────────────────────────────────

@Composable
fun PylotoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val extendedColors = if (darkTheme) DarkExtendedColors else LightExtendedColors

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    CompositionLocalProvider(
        LocalPylotoExtendedColors provides extendedColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = PylotoTypography,
            shapes = PylotoShapes,
            content = content
        )
    }
}

/**
 * Acesso conveniente ao tema Pyloto.
 *
 * Uso:
 * ```
 * val gold = PylotoTheme.extendedColors.gold
 * val approved = PylotoTheme.extendedColors.statusApproved
 * ```
 */
object PylotoTheme {
    val extendedColors: PylotoExtendedColors
        @Composable
        get() = LocalPylotoExtendedColors.current
}
