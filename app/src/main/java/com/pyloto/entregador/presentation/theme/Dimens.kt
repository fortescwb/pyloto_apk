package com.pyloto.entregador.presentation.theme

import androidx.compose.ui.unit.dp

/**
 * Tokens de espaçamento e dimensão — Design System Pyloto.
 *
 * Uso:
 * ```
 * Modifier.padding(PylotoDimens.ScreenPadding)
 * Spacer(modifier = Modifier.height(PylotoDimens.SpacingMd))
 * ```
 */
object PylotoDimens {

    // ─── Spacing Scale ─────────────────────────────────────────
    val SpacingXxs = 2.dp
    val SpacingXs  = 4.dp
    val SpacingSm  = 8.dp
    val SpacingMd  = 16.dp
    val SpacingLg  = 24.dp
    val SpacingXl  = 32.dp
    val SpacingXxl = 48.dp

    // ─── Screen ────────────────────────────────────────────────
    val ScreenPadding = 24.dp
    val ScreenPaddingCompact = 16.dp

    // ─── Cards ─────────────────────────────────────────────────
    val CardPadding = 16.dp
    val CardElevation = 2.dp
    val CardRadius = 12.dp

    // ─── Buttons ───────────────────────────────────────────────
    val ButtonHeight = 52.dp
    val ButtonHeightSmall = 40.dp
    val ButtonRadius = 12.dp

    // ─── Inputs ────────────────────────────────────────────────
    val InputHeight = 56.dp
    val InputRadius = 12.dp

    // ─── Icons ─────────────────────────────────────────────────
    val IconSizeSm = 20.dp
    val IconSizeMd = 24.dp
    val IconSizeLg = 32.dp
    val IconSizeXl = 48.dp

    // ─── Avatars / Logos ───────────────────────────────────────
    val AvatarSizeSm = 40.dp
    val AvatarSizeMd = 56.dp
    val AvatarSizeLg = 88.dp

    // ─── Bottom Sheet / Modal ──────────────────────────────────
    val BottomSheetRadius = 28.dp
    val BottomSheetHandleWidth = 40.dp
    val BottomSheetHandleHeight = 4.dp

    // ─── Dividers ──────────────────────────────────────────────
    val DividerThickness = 1.dp

    // ─── Progress ──────────────────────────────────────────────
    val ProgressStrokeWidth = 2.5.dp
    val ProgressSize = 22.dp
}
