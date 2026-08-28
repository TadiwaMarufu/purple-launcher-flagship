package com.example.purplelauncher.core.model

enum class SearchResultType {
    APP,
    SETTING,
    ACTION,
    CONTACT,
    SHORTCUT,
    DEV_TOOL,
    SPACE,
    WEB
}

data class SearchResult(
    val id: String,
    val type: SearchResultType,
    val title: String,
    val subtitle: String,
    val iconName: String? = null,
    val packageName: String? = null,
    val activityName: String? = null,
    val actionIntent: String? = null,
    val score: Double = 1.0
)
