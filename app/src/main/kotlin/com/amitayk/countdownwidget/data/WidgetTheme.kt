package com.amitayk.countdownwidget.data

data class WidgetTheme(
    val id: String,
    val name: String,
    val backgroundColorHex: String,   // used for solid fills; for gradients: fallback colour
    val primaryTextColorHex: String,
    val secondaryTextColorHex: String,
    val cornerRadiusDp: Int = 16,
    val backgroundIsGradient: Boolean = false,
    val gradientStartHex: String? = null,  // top colour
    val gradientEndHex: String? = null,    // bottom colour
    val borderColorHex: String? = null,
)

object WidgetThemes {

    val all: List<WidgetTheme> = listOf(
        // ── Solid dark ────────────────────────────────────────────────────────
        WidgetTheme(
            id = "navy", name = "Navy",
            backgroundColorHex = "#1A1A2E",
            primaryTextColorHex = "#FFFFFF",
            secondaryTextColorHex = "#BDBDBD",
        ),
        WidgetTheme(
            id = "light", name = "Light",
            backgroundColorHex = "#FFFFFF",
            primaryTextColorHex = "#1C1C1E",
            secondaryTextColorHex = "#8E8E93",
        ),
        WidgetTheme(
            id = "high_contrast", name = "High Contrast",
            backgroundColorHex = "#0A0A0A",
            primaryTextColorHex = "#FFFFFF",
            secondaryTextColorHex = "#666666",
            borderColorHex = "#FFFFFF",
        ),
        WidgetTheme(
            id = "teal_glass", name = "Teal Glass",
            backgroundColorHex = "#0d2428",
            primaryTextColorHex = "#03DAC5",
            secondaryTextColorHex = "#B203DAC5",
            borderColorHex = "#3303DAC5",
        ),
        // ── Gradient ──────────────────────────────────────────────────────────
        WidgetTheme(
            id = "purple_glow", name = "Purple Glow",
            backgroundColorHex = "#2d1b69",
            primaryTextColorHex = "#c4a0f5",
            secondaryTextColorHex = "#99C4A0F5",
            backgroundIsGradient = true,
            gradientStartHex = "#2d1b69",
            gradientEndHex = "#1a0f3c",
        ),
        WidgetTheme(
            id = "amber", name = "Warm Amber",
            backgroundColorHex = "#3d2000",
            primaryTextColorHex = "#FF9500",
            secondaryTextColorHex = "#B3C97B30",
            backgroundIsGradient = true,
            gradientStartHex = "#3d2000",
            gradientEndHex = "#1c0f00",
        ),
        WidgetTheme(
            id = "blue_depth", name = "Blue Depth",
            backgroundColorHex = "#0e2438",
            primaryTextColorHex = "#90c4ff",
            secondaryTextColorHex = "#9942A5F5",
            backgroundIsGradient = true,
            gradientStartHex = "#0e2438",
            gradientEndHex = "#091520",
        ),
        WidgetTheme(
            id = "rose", name = "Rose",
            backgroundColorHex = "#3d0a1e",
            primaryTextColorHex = "#FF5080",
            secondaryTextColorHex = "#99FF5078",
            backgroundIsGradient = true,
            gradientStartHex = "#3d0a1e",
            gradientEndHex = "#1a0410",
        ),
        // ── Material You (large corners) ─────────────────────────────────────
        WidgetTheme(
            id = "terracotta", name = "Terracotta",
            backgroundColorHex = "#4e2d22",
            primaryTextColorHex = "#ffb5a0",
            secondaryTextColorHex = "#80FFB5A0",
            cornerRadiusDp = 28,
        ),
        WidgetTheme(
            id = "sage", name = "Sage",
            backgroundColorHex = "#1e3323",
            primaryTextColorHex = "#7dd99a",
            secondaryTextColorHex = "#807DD99A",
            cornerRadiusDp = 28,
        ),
        WidgetTheme(
            id = "ocean", name = "Ocean",
            backgroundColorHex = "#162840",
            primaryTextColorHex = "#90c4ff",
            secondaryTextColorHex = "#8090C4FF",
            cornerRadiusDp = 28,
        ),
        WidgetTheme(
            id = "lavender", name = "Lavender",
            backgroundColorHex = "#26204a",
            primaryTextColorHex = "#c4b5ff",
            secondaryTextColorHex = "#80C4B5FF",
            cornerRadiusDp = 28,
        ),
        // ── AMOLED ────────────────────────────────────────────────────────────
        WidgetTheme(
            id = "amoled_white", name = "AMOLED White",
            backgroundColorHex = "#000000",
            primaryTextColorHex = "#FFFFFF",
            secondaryTextColorHex = "#66FFFFFF",
            borderColorHex = "#14FFFFFF",
        ),
        WidgetTheme(
            id = "amoled_teal", name = "AMOLED Teal",
            backgroundColorHex = "#000000",
            primaryTextColorHex = "#03DAC5",
            secondaryTextColorHex = "#8003DAC5",
            borderColorHex = "#2603DAC5",
        ),
        WidgetTheme(
            id = "amoled_purple", name = "AMOLED Purple",
            backgroundColorHex = "#000000",
            primaryTextColorHex = "#BB86FC",
            secondaryTextColorHex = "#80BB86FC",
            borderColorHex = "#26BB86FC",
        ),
        WidgetTheme(
            id = "amoled_rose", name = "AMOLED Rose",
            backgroundColorHex = "#000000",
            primaryTextColorHex = "#FF5080",
            secondaryTextColorHex = "#80FF5078",
            borderColorHex = "#26FF5078",
        ),
        // ── Frost (semi-transparent) ─────────────────────────────────────────
        WidgetTheme(
            id = "frost_dark", name = "Dark Frost",
            backgroundColorHex = "#12FFFFFF",
            primaryTextColorHex = "#FFFFFF",
            secondaryTextColorHex = "#80FFFFFF",
            borderColorHex = "#1FFFFFFF",
        ),
        WidgetTheme(
            id = "frost_light", name = "Light Frost",
            backgroundColorHex = "#73FFFFFF",
            primaryTextColorHex = "#1a2a4a",
            secondaryTextColorHex = "#801A2A4A",
            borderColorHex = "#1FFFFFFF",
        ),
        // ── Pastel (light bg, medium corners) ────────────────────────────────
        WidgetTheme(
            id = "blush", name = "Blush",
            backgroundColorHex = "#fce4ec",
            primaryTextColorHex = "#c2185b",
            secondaryTextColorHex = "#99E91E8C",
            cornerRadiusDp = 24,
        ),
        WidgetTheme(
            id = "mint", name = "Mint",
            backgroundColorHex = "#e0f2f1",
            primaryTextColorHex = "#00796b",
            secondaryTextColorHex = "#99009688",
            cornerRadiusDp = 24,
        ),
        WidgetTheme(
            id = "peach", name = "Peach",
            backgroundColorHex = "#fff3e0",
            primaryTextColorHex = "#e65100",
            secondaryTextColorHex = "#99EF6C00",
            cornerRadiusDp = 24,
        ),
        WidgetTheme(
            id = "periwinkle", name = "Periwinkle",
            backgroundColorHex = "#ede7f6",
            primaryTextColorHex = "#4527a0",
            secondaryTextColorHex = "#99673AB7",
            cornerRadiusDp = 24,
        ),
    )

    fun find(id: String): WidgetTheme = all.firstOrNull { it.id == id } ?: all.first()
    val default: WidgetTheme get() = all.first()
}
