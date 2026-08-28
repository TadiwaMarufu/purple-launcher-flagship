package com.example.purplelauncher.core.database

import androidx.room.*
import com.example.purplelauncher.core.model.*

@Entity(tableName = "app_custom_data")
data class AppCustomEntity(
    @PrimaryKey val packageName: String,
    val customLabel: String? = null,
    val categoryOverride: String? = null,
    val isFavorite: Boolean = false,
    val isHidden: Boolean = false,
    val launchCount: Int = 0,
    val lastLaunchedTime: Long = 0L
)

@Entity(tableName = "profiles")
data class ProfileEntity(
    @PrimaryKey val id: String,
    val name: String,
    val iconName: String,
    val accentColorHex: String,
    val themeMode: String,
    val wallpaperConfig: WallpaperConfig,
    val dockApps: List<String>,
    val hiddenApps: List<String>,
    val favoriteApps: List<String>,
    val homeItems: List<HomeItem>,
    val activeSpaceIds: List<String>,
    val isAutomated: Boolean,
    val triggerType: String? = null,
    val triggerTimeHour: Int = 9,
    val triggerTimeMinute: Int = 0,
    val triggerDays: List<String> = emptyList(),
    val triggerWifi: String? = null,
    val triggerBluetooth: String? = null,
    val createdTimestamp: Long
)

@Entity(tableName = "spaces")
data class SpaceEntity(
    @PrimaryKey val id: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val type: String,
    val repoOwner: String?,
    val repoName: String?,
    val gitBranch: String?,
    val pinnedApps: List<String>,
    val notes: String,
    val tasks: List<SpaceTask>,
    val accentColorHex: String
)

@Entity(tableName = "folders")
data class FolderEntity(
    @PrimaryKey val id: String,
    val name: String,
    val appPackages: List<String>,
    val colorHex: String?
)

@Entity(tableName = "widget_stacks")
data class WidgetStackEntity(
    @PrimaryKey val id: String,
    val name: String,
    val entries: List<WidgetStackEntry>,
    val currentIndex: Int
)
