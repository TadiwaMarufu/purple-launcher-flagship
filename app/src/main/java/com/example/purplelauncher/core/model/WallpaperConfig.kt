package com.example.purplelauncher.core.model

enum class WallpaperPreset(val displayName: String) {
    PURE("Pure"),
    SOFT("Soft"),
    NOIR("Noir"),
    FILM("Film"),
    MATTE("Matte"),
    HIGH_CONTRAST("High Contrast")
}

data class WallpaperConfig(
    val sourceUri: String? = null,
    val preset: WallpaperPreset = WallpaperPreset.PURE,
    val brightness: Float = 0.0f,
    val contrast: Float = 1.0f,
    val grayscaleIntensity: Float = 1.0f,
    val blurRadius: Float = 0.0f,
    val grainAmount: Float = 0.04f,
    val vignetteAmount: Float = 0.12f,
    val darkening: Float = 0.35f,
    val zoom: Float = 1.0f,
    val offsetX: Float = 0.0f,
    val offsetY: Float = 0.0f,
    val isParallaxEnabled: Boolean = true
)
