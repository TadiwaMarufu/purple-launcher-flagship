package com.example.purplelauncher.core.model

enum class TriggerType {
    TIME,
    WIFI,
    BLUETOOTH,
    MANUAL
}

data class ProfileTrigger(
    val type: TriggerType = TriggerType.MANUAL,
    val timeHour: Int = 9,
    val timeMinute: Int = 0,
    val daysOfWeek: List<Int> = listOf(1, 2, 3, 4, 5), // Monday to Friday
    val wifiSsid: String? = null,
    val bluetoothDeviceName: String? = null
)

data class Profile(
    val id: String,
    val name: String,
    val iconName: String = "home",
    val accentColorHex: String = "#D0BCFF",
    val themeMode: ThemeMode = ThemeMode.DYNAMIC,
    val wallpaperConfig: WallpaperConfig = WallpaperConfig(),
    val dockApps: List<String> = emptyList(),
    val hiddenApps: List<String> = emptyList(),
    val favoriteApps: List<String> = emptyList(),
    val homeItems: List<HomeItem> = emptyList(),
    val activeSpaceIds: List<String> = emptyList(),
    val isAutomated: Boolean = false,
    val automationTrigger: ProfileTrigger? = null,
    val createdTimestamp: Long = System.currentTimeMillis()
)
