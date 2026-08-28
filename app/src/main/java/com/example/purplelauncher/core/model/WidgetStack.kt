package com.example.purplelauncher.core.model

data class WidgetStackEntry(
    val id: String,
    val title: String,
    val type: String, // "CLOCK", "WEATHER", "BATTERY", "CALENDAR", "TASKS", "DEV_STATUS", "APP_WIDGET"
    val appWidgetId: Int? = null
)

data class WidgetStack(
    val id: String,
    val name: String = "Widget Stack",
    val entries: List<WidgetStackEntry> = emptyList(),
    val currentIndex: Int = 0
)

data class Folder(
    val id: String,
    val name: String,
    val appPackages: List<String> = emptyList(),
    val colorHex: String? = null
)
