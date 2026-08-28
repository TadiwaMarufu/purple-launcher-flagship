package com.example.purplelauncher.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.purplelauncher.core.model.ThemeConfig
import com.example.purplelauncher.core.model.ThemeMode

fun parseColorHex(hex: String, defaultColor: Color): Color {
    return try {
        val clean = hex.removePrefix("#")
        val colorInt = clean.toLong(16)
        if (clean.length == 6) {
            Color(0xFF000000 or colorInt)
        } else {
            Color(colorInt)
        }
    } catch (_: Exception) {
        defaultColor
    }
}

@Composable
fun ThePurpleLauncherTheme(
    themeConfig: ThemeConfig = ThemeConfig(),
    wallpaperLuminance: Float = 0.2f,
    accentOverrideHex: String? = null,
    content: @Composable () -> Unit
) {
    val accentHex = accentOverrideHex ?: themeConfig.primaryAccentHex
    val primaryAccent = parseColorHex(accentHex, PurpleAccentDark)

    val isDark = when (themeConfig.themeMode) {
        ThemeMode.LIGHT, ThemeMode.PURE_WHITE -> false
        ThemeMode.DARK, ThemeMode.AMOLED -> true
        ThemeMode.DYNAMIC -> wallpaperLuminance < 0.6f
        ThemeMode.MONOCHROME, ThemeMode.CUSTOM -> isSystemInDarkTheme()
    }

    val isAmoled = themeConfig.themeMode == ThemeMode.AMOLED
    val isMonochrome = themeConfig.themeMode == ThemeMode.MONOCHROME

    val colorScheme = if (isDark) {
        darkColorScheme(
            primary = if (isMonochrome) MonoTextPrimary else primaryAccent,
            onPrimary = if (isMonochrome) MonoDarkest else MonoDarkest,
            primaryContainer = if (isMonochrome) MonoSurfaceLight else primaryAccent.copy(alpha = 0.25f),
            onPrimaryContainer = if (isMonochrome) MonoTextPrimary else primaryAccent,
            secondary = PurpleAccentVibrant,
            onSecondary = MonoDarkest,
            background = if (isAmoled) MonoBlack else MonoDarkest,
            onBackground = MonoTextPrimary,
            surface = if (isAmoled) MonoBlack else MonoDarker,
            onSurface = MonoTextPrimary,
            surfaceVariant = MonoSurface,
            onSurfaceVariant = MonoTextSecondary,
            outline = MonoOutlineVariant,
            outlineVariant = GlassBorderDark
        )
    } else {
        lightColorScheme(
            primary = if (isMonochrome) MonoLightTextPrimary else primaryAccent,
            onPrimary = MonoWhite,
            primaryContainer = primaryAccent.copy(alpha = 0.15f),
            onPrimaryContainer = primaryAccent,
            secondary = PurpleAccentLight,
            onSecondary = MonoWhite,
            background = MonoLightBackground,
            onBackground = MonoLightTextPrimary,
            surface = MonoLightSurface,
            onSurface = MonoLightTextPrimary,
            surfaceVariant = MonoLightOutline.copy(alpha = 0.5f),
            onSurfaceVariant = MonoLightTextSecondary,
            outline = MonoLightOutline,
            outlineVariant = GlassBorderLight
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
