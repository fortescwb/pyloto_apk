package com.pyloto.entregador.presentation.theme

import androidx.compose.ui.graphics.Color

/**
 * Design Tokens de Cor — Paleta Pyloto Corp
 *
 * Contexto: APP (Entregador)
 * Hierarquia de aplicação conforme paleta_cores_Pyloto.md:
 *   Primária  → Azul Técnico (navegação, interação)
 *   Secundária → Dourado (CTAs, conquistas, ganhos)
 *   Terciária  → Verde Militar (aprovação, sucesso)
 *
 * @see paleta_cores_Pyloto.md na raiz do workspace
 */
object PylotoColors {

    // ─── Cores Institucionais ──────────────────────────────────────
    val Black          = Color(0xFF1A1A1A)   // Preto elegante
    val Sepia          = Color(0xFF8B4513)   // Marrom sépia
    val Gold           = Color(0xFFD4AF37)   // Dourado premium
    val Parchment      = Color(0xFFF5F1E8)   // Bege pergaminho
    val DarkGray       = Color(0xFF2A2A2A)   // Cinza escuro (texto)
    val White          = Color(0xFFFFFFFF)   // Branco puro
    val MilitaryGreen  = Color(0xFF3D5A40)   // Verde militar
    val TechBlue       = Color(0xFF2C5F7D)   // Azul técnico

    // ─── Variações para M3 color roles ─────────────────────────────
    val TechBlueDark   = Color(0xFF1E4A63)   // Primary container
    val TechBlueLight  = Color(0xFF5B9CC4)   // Inverse primary / dark mode
    val GoldDark       = Color(0xFFB8952E)   // Secondary container pressed
    val GoldLight      = Color(0xFFE8CC6B)   // Secondary container
    val GreenLight     = Color(0xFF6B9B6E)   // Tertiary container (dark)
    val GreenDark      = Color(0xFF2E4530)   // Tertiary container

    // ─── Superfícies ───────────────────────────────────────────────
    val SurfaceDim     = Color(0xFFEDE8DD)   // Surface variant
    val SurfaceBright  = Color(0xFFFAF8F3)   // Surface container low
    val Outline        = Color(0xFFB0A99E)   // Outline
    val OutlineVariant = Color(0xFFD6D0C6)   // Outline variant

    // ─── Texto ─────────────────────────────────────────────────────
    val TextPrimary    = Color(0xFF2A2A2A)
    val TextSecondary  = Color(0xFF6B6B6B)
    val TextDisabled   = Color(0xFF9E9E9E)
    val TextOnDark     = Color(0xFFE0E0E0)

    // ─── Status / Semânticas ───────────────────────────────────────
    val StatusApproved   = Color(0xFF3D5A40)   // Verde — Aprovado / Confirmado
    val StatusPending    = Color(0xFF8B4513)   // Marrom — Aguardando / Pendente
    val StatusRejected   = Color(0xFFC4342D)   // Vermelho — Rejeitado / Erro
    val StatusInfo       = Color(0xFF2C5F7D)   // Azul — Informativo

    val ErrorDark        = Color(0xFFB71C1C)
    val ErrorLight       = Color(0xFFEF5350)
    val OnError          = Color(0xFFFFFFFF)

    // ─── Overlays ──────────────────────────────────────────────────
    val OverlayDark    = Color(0x80000000)
    val OverlayLight   = Color(0x40FFFFFF)
    val Scrim          = Color(0x52000000)

    // ─── Dark Mode ─────────────────────────────────────────────────
    object Dark {
        val Background   = Color(0xFF121212)
        val Surface      = Color(0xFF1E1E1E)
        val SurfaceHigh  = Color(0xFF2C2C2C)
        val TextPrimary  = Color(0xFFE0E0E0)
        val TextSecondary = Color(0xFF9E9E9E)
        val Primary      = Color(0xFF5B9CC4)   // TechBlue mais claro
        val Secondary    = Color(0xFFD4AF37)   // Dourado mantém
        val Tertiary     = Color(0xFF6B9B6E)   // Verde mais claro
        val Error        = Color(0xFFEF5350)
    }
}
