package com.example.purplelauncher.core.model

data class LauncherBackup(
    val version: String = "0.1",
    val exportTimestamp: Long = System.currentTimeMillis(),
    val appName: String = "The Purple Launcher",
    val profiles: List<Profile> = emptyList(),
    val spaces: List<SpaceConfig> = emptyList(),
    val folders: List<Folder> = emptyList(),
    val widgetStacks: List<WidgetStack> = emptyList(),
    val themeConfig: ThemeConfig = ThemeConfig(),
    val gestureConfig: GestureConfig = GestureConfig()
)
