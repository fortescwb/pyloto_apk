package com.pyloto.entregador.presentation.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Paleta Pyloto Corp
val PylotoPrimary = Color(0xFF2C5F7D)        // Azul técnico
val PylotoOnPrimary = Color(0xFFFFFFFF)      // Branco
val PylotoSecondary = Color(0xFFD4AF37)      // Dourado premium
val PylotoOnSecondary = Color(0xFF1A1A1A)    // Preto elegante
val PylotoTertiary = Color(0xFF3D5A40)       // Verde militar
val PylotoBackground = Color(0xFFFFFFFF)     // Branco puro
val PylotoSurface = Color(0xFFF5F1E8)        // Bege pergaminho
val PylotoOnSurface = Color(0xFF2A2A2A)      // Cinza escuro
val PylotoSepia = Color(0xFF8B4513)          // Marrom sépia
val PylotoError = Color(0xFFC4342D)          // Vermelho
val PylotoOnError = Color(0xFFFFFFFF)

private val LightColorScheme = lightColorScheme(
    primary = PylotoPrimary,
    onPrimary = PylotoOnPrimary,
    secondary = PylotoSecondary,
    onSecondary = PylotoOnSecondary,
    tertiary = PylotoTertiary,
    background = PylotoBackground,
    surface = PylotoSurface,
    onSurface = PylotoOnSurface,
    error = PylotoError,
    onError = PylotoOnError,
    surfaceVariant = Color(0xFFEDE8DD),
    onSurfaceVariant = Color(0xFF6B6B6B)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF5B9CC4),
    onPrimary = Color(0xFF1A1A1A),
    secondary = PylotoSecondary,
    onSecondary = Color(0xFF1A1A1A),
    tertiary = Color(0xFF6B9B6E),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE0E0E0),
    error = Color(0xFFEF5350),
    onError = Color(0xFF1A1A1A)
)

@Composable
fun PylotoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
