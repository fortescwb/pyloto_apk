package com.pyloto.entregador.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Shapes Pyloto — cantos arredondados equilibrados entre modernidade
 * e a sobriedade da identidade visual.
 *
 * Referência: Material3 shape scale
 * - Botões primários: Medium (12dp) — premium, acolhedor
 * - Cards: Medium (12dp) — consistente com ações
 * - Modais / Bottom sheets: ExtraLarge (28dp) — suavidade
 * - Chips / badges: Small (8dp) — compacto
 */
val PylotoShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(28.dp)
)
