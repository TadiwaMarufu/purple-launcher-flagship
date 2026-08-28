package com.example.purplelauncher.core.model

enum class SpaceType(val displayName: String) {
    DEV("Developer Space"),
    WORK("Work & Projects"),
    STUDY("Deep Study"),
    GAMING("Gaming Lounge"),
    PORTFOLIO("Portfolio"),
    GENERAL("Personal Space")
}

data class SpaceTask(
    val id: String,
    val title: String,
    val isCompleted: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

data class SpaceConfig(
    val id: String,
    val title: String,
    val subtitle: String = "",
    val description: String = "",
    val type: SpaceType = SpaceType.GENERAL,
    val repoOwner: String? = null,
    val repoName: String? = null,
    val gitBranch: String? = "main",
    val pinnedApps: List<String> = emptyList(),
    val notes: String = "",
    val tasks: List<SpaceTask> = emptyList(),
    val accentColorHex: String = "#A855F7"
)
