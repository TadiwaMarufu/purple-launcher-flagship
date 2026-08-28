package com.example.purplelauncher.core.model

enum class AppCategory(val displayName: String) {
    ALL("All"),
    FAVORITES("Favorites"),
    COMMUNICATION("Communication"),
    SOCIAL("Social"),
    PRODUCTIVITY("Productivity"),
    MEDIA("Media"),
    GAMES("Games"),
    FINANCE("Finance"),
    SHOPPING("Shopping"),
    TRAVEL("Travel"),
    TOOLS("Tools"),
    DEVELOPER("Developer"),
    SYSTEM("System"),
    OTHER("Other")
}

data class AppInfo(
    val packageName: String,
    val activityName: String,
    val label: String,
    val isSystemApp: Boolean = false,
    val installTime: Long = 0L,
    val lastUpdateTime: Long = 0L,
    val versionName: String = "1.0",
    val versionCode: Long = 1L,
    val targetSdk: Int = 34,
    val minSdk: Int = 24,
    val category: AppCategory = AppCategory.OTHER,
    val isFavorite: Boolean = false,
    val isHidden: Boolean = false,
    val launchCount: Int = 0,
    val lastLaunchedTime: Long = 0L,
    val customLabel: String? = null,
    val permissionsCount: Int = 0,
    val appSizeFormatted: String = ""
) {
    val displayTitle: String
        get() = customLabel?.takeIf { it.isNotBlank() } ?: label
}
