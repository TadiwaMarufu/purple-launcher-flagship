package com.example.purplelauncher.core.model

enum class HomeItemType {
    APP,
    WIDGET,
    WIDGET_STACK,
    FOLDER,
    CLOCK_BANNER,
    SMART_BAR,
    SPACE_CARD
}

data class HomeItem(
    val id: String,
    val type: HomeItemType,
    val pageIndex: Int = 0,
    val gridX: Int = 0,
    val gridY: Int = 0,
    val spanX: Int = 1,
    val spanY: Int = 1,
    val packageName: String? = null,
    val activityName: String? = null,
    val widgetId: Int? = null,
    val widgetProvider: String? = null,
    val folderId: String? = null,
    val stackId: String? = null,
    val spaceId: String? = null,
    val customTitle: String? = null,
    val customIcon: String? = null
)
