package com.example.purplelauncher.core.model

enum class ThemeMode(val displayName: String) {
    DYNAMIC("Dynamic"),
    DARK("Dark"),
    LIGHT("Light"),
    AMOLED("AMOLED Pure Black"),
    PURE_WHITE("Pure White"),
    MONOCHROME("Pure Monochrome"),
    CUSTOM("Custom Purple")
}

enum class IconShape(val displayName: String) {
    SQUIRCLE("Squircle"),
    CIRCLE("Circle"),
    ROUNDED("Rounded Square"),
    TEARDROP("Teardrop")
}

data class ThemeConfig(
    val themeMode: ThemeMode = ThemeMode.DYNAMIC,
    val primaryAccentHex: String = "#D0BCFF",
    val secondaryAccentHex: String = "#A855F7",
    val surfaceOpacity: Float = 0.85f,
    val gridRows: Int = 5,
    val gridCols: Int = 4,
    val iconShape: IconShape = IconShape.SQUIRCLE,
    val iconSizeDp: Int = 54,
    val showLabels: Boolean = true,
    val iconMonochrome: Boolean = true,
    val dockSize: Int = 4,
    val dockVisible: Boolean = true,
    val reducedMotion: Boolean = false,
    val feedEnabled: Boolean = false,
    val notificationDotsEnabled: Boolean = true
)
