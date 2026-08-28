package com.example.purplelauncher.core.model

enum class ThemeMode(val displayName: String) {
    DARK("Dark Minimal"),
    LIGHT("Light Canvas"),
    AMOLED("OLED Pure Black"),
    MONOCHROME("Monochrome Tint"),
    DYNAMIC("Dynamic Adaptive"),
    PURE_WHITE("Clean White"),
    CUSTOM("Custom Gradient")
}

enum class ThemePreset(val displayName: String, val subtitle: String) {
    NEO_OBSIDIAN("Neo Obsidian", "OLED black, glossy squircle icons & battery rings"),
    ONEUI_FROSTED_GLASS("OneUI Frosted Glass", "Sage teal blurred glass & quick control center"),
    NOTHING_DOT_MATRIX("Nothing Dot Matrix", "Dot matrix typography, day progress & petal launcher"),
    EMERALD_FOREST("Emerald Sage", "Lush olive green, split clock & quote cards"),
    PARISIAN_EDITORIAL("Parisian Editorial", "High-contrast serif typography & floating now playing"),
    WIDGETSMITH_AESTHETIC("Widgetsmith Aesthetic", "Atmospheric polaroids, stencil clocks & mood cards"),
    CYBER_VIBRANT("Cyber Vibrant", "Vibrant purple neon & futuristic glass cards"),
    MATERIAL_YOU_DYNAMIC("Material You Dynamic", "Wallpaper-adaptive dynamic colors & rounded pills")
}

enum class IconStyle(val displayName: String) {
    ORIGINAL_VIBRANT("Vibrant Full Color"),
    GLOSSY_SQUIRCLE("3D Glossy Squircle"),
    THEMED_TINTED("Material You Tinted"),
    MONOCHROME_DARK("Monochrome Dark"),
    MONOCHROME_LIGHT("Monochrome Light"),
    NOTHING_DOT_GLYPH("Nothing Dot Glyph"),
    EDITORIAL_OUTLINE("Minimalist Outline"),
    NEON_GLOW("Neon Glow")
}

enum class IconShape(val displayName: String) {
    SQUIRCLE("Squircle"),
    CIRCLE("Circle"),
    ROUNDED("Rounded Square"),
    TEARDROP("Teardrop"),
    HEXAGON("Hexagon"),
    PEBBLE("Pebble Continuous")
}

enum class ClockStyle(val displayName: String) {
    TALL_CONDENSED("Tall Condensed Split"),
    EDITORIAL_STACK("Giant Editorial Stack"),
    NOTHING_DOT_MATRIX("Nothing Dot Matrix"),
    SPLIT_CAPSULE("Split Capsule Clock"),
    MINIMAL_SERIF("Minimalist Serif"),
    ANALOG_MODERN("Analog Minimalist Dial"),
    DIGITAL_PILL("Modern Pill Clock")
}

enum class FontPreset(val displayName: String) {
    MODERN_SANS("Modern Sans"),
    EDITORIAL_SERIF("Editorial Serif"),
    DOT_MATRIX("Dot Matrix Digital"),
    CLEAN_MONO("Technical Mono"),
    ROUNDED_SOFT("Rounded Soft")
}

data class ThemeConfig(
    val activePreset: ThemePreset = ThemePreset.NEO_OBSIDIAN,
    val themeMode: ThemeMode = ThemeMode.DARK,
    val primaryAccentHex: String = "#38BDF8", // Dynamic modern accent
    val secondaryAccentHex: String = "#818CF8",
    val backgroundHex: String = "#090A0F",
    val surfaceOpacity: Float = 0.85f,
    val blurRadiusDp: Int = 24,
    val gridRows: Int = 5,
    val gridCols: Int = 4,
    val iconStyle: IconStyle = IconStyle.GLOSSY_SQUIRCLE,
    val iconShape: IconShape = IconShape.SQUIRCLE,
    val iconSizeDp: Int = 56,
    val showLabels: Boolean = true,
    val iconMonochrome: Boolean = false,
    val fontPreset: FontPreset = FontPreset.MODERN_SANS,
    val clockStyle: ClockStyle = ClockStyle.TALL_CONDENSED,
    val selectedIconPackPackage: String? = null,
    val dockSize: Int = 4,
    val dockVisible: Boolean = true,
    val dockGlassmorphic: Boolean = true,
    val reducedMotion: Boolean = false,
    val feedEnabled: Boolean = true,
    val nowPlayingBarEnabled: Boolean = true,
    val controlCenterEnabled: Boolean = true,
    val doubleTapAction: GestureAction = GestureAction.LOCK_SCREEN,
    val swipeDownAction: GestureAction = GestureAction.OPEN_CONTROL_CENTER,
    val swipeUpAction: GestureAction = GestureAction.APP_DRAWER,
    val notificationDotsEnabled: Boolean = true
)
