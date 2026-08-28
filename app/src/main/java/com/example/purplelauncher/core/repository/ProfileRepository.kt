package com.example.purplelauncher.core.repository

import com.example.purplelauncher.core.database.LauncherDao
import com.example.purplelauncher.core.database.ProfileEntity
import com.example.purplelauncher.core.database.SpaceEntity
import com.example.purplelauncher.core.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID

class ProfileRepository(
    private val launcherDao: LauncherDao,
    private val scope: CoroutineScope
) {
    private val _activeProfileId = MutableStateFlow("profile_home")
    val activeProfileId: StateFlow<String> = _activeProfileId.asStateFlow()

    val allProfiles: Flow<List<Profile>> = launcherDao.getAllProfiles().map { entities ->
        if (entities.isEmpty()) {
            val defaults = createDefaultProfiles()
            launcherDao.insertProfiles(defaults.map { it.toEntity() })
            defaults
        } else {
            entities.map { it.toModel() }
        }
    }

    val activeProfile: StateFlow<Profile> = combine(
        allProfiles,
        activeProfileId
    ) { profiles, activeId ->
        profiles.find { it.id == activeId } ?: profiles.firstOrNull() ?: createDefaultHomeProfile()
    }.stateIn(scope, SharingStarted.Eagerly, createDefaultHomeProfile())

    val allSpaces: Flow<List<SpaceConfig>> = launcherDao.getAllSpaces().map { entities ->
        if (entities.isEmpty()) {
            val defaults = createDefaultSpaces()
            launcherDao.insertSpaces(defaults.map { it.toEntity() })
            defaults
        } else {
            entities.map { it.toModel() }
        }
    }

    init {
        scope.launch(Dispatchers.IO) {
            val count = launcherDao.getAllProfiles().first().size
            if (count == 0) {
                val defaults = createDefaultProfiles()
                launcherDao.insertProfiles(defaults.map { it.toEntity() })
                val spaces = createDefaultSpaces()
                launcherDao.insertSpaces(spaces.map { it.toEntity() })
            }
        }
    }

    fun switchProfile(profileId: String) {
        _activeProfileId.value = profileId
    }

    suspend fun saveProfile(profile: Profile) {
        launcherDao.insertProfile(profile.toEntity())
    }

    suspend fun createProfile(name: String, iconName: String, accentHex: String): Profile {
        val newProfile = Profile(
            id = "profile_${UUID.randomUUID().toString().take(8)}",
            name = name,
            iconName = iconName,
            accentColorHex = accentHex,
            themeMode = ThemeMode.DYNAMIC,
            dockApps = listOf("com.google.android.apps.messaging", "com.android.chrome"),
            homeItems = createDefaultHomeItems()
        )
        launcherDao.insertProfile(newProfile.toEntity())
        return newProfile
    }

    suspend fun duplicateProfile(sourceProfileId: String): Profile? {
        val source = allProfiles.first().find { it.id == sourceProfileId } ?: return null
        val copy = source.copy(
            id = "profile_${UUID.randomUUID().toString().take(8)}",
            name = "${source.name} (Copy)",
            createdTimestamp = System.currentTimeMillis()
        )
        launcherDao.insertProfile(copy.toEntity())
        return copy
    }

    suspend fun deleteProfile(profileId: String) {
        // Prevent deleting last profile
        val current = allProfiles.first()
        if (current.size > 1) {
            launcherDao.deleteProfileById(profileId)
            if (_activeProfileId.value == profileId) {
                val remaining = current.filter { it.id != profileId }
                _activeProfileId.value = remaining.first().id
            }
        }
    }

    suspend fun updateHomeItems(profileId: String, items: List<HomeItem>) {
        val current = allProfiles.first().find { it.id == profileId } ?: return
        val updated = current.copy(homeItems = items)
        launcherDao.insertProfile(updated.toEntity())
    }

    suspend fun updateDockApps(profileId: String, dockApps: List<String>) {
        val current = allProfiles.first().find { it.id == profileId } ?: return
        val updated = current.copy(dockApps = dockApps)
        launcherDao.insertProfile(updated.toEntity())
    }

    suspend fun updateWallpaperConfig(profileId: String, wallpaperConfig: WallpaperConfig) {
        val current = allProfiles.first().find { it.id == profileId } ?: return
        val updated = current.copy(wallpaperConfig = wallpaperConfig)
        launcherDao.insertProfile(updated.toEntity())
    }

    suspend fun saveSpace(space: SpaceConfig) {
        launcherDao.insertSpace(space.toEntity())
    }

    suspend fun deleteSpace(spaceId: String) {
        launcherDao.deleteSpaceById(spaceId)
    }

    private fun createDefaultHomeItems(): List<HomeItem> {
        return listOf(
            HomeItem(
                id = "item_clock",
                type = HomeItemType.CLOCK_BANNER,
                pageIndex = 0,
                gridX = 0,
                gridY = 0,
                spanX = 4,
                spanY = 2
            ),
            HomeItem(
                id = "item_smart",
                type = HomeItemType.SMART_BAR,
                pageIndex = 0,
                gridX = 0,
                gridY = 2,
                spanX = 4,
                spanY = 1
            )
        )
    }

    private fun createDefaultProfiles(): List<Profile> {
        return listOf(
            createDefaultHomeProfile(),
            Profile(
                id = "profile_work",
                name = "WORK",
                iconName = "business_center",
                accentColorHex = "#818CF8",
                themeMode = ThemeMode.DYNAMIC,
                dockApps = listOf("com.google.android.gm", "com.google.android.calendar", "com.slack", "com.google.android.apps.docs"),
                homeItems = listOf(
                    HomeItem(id = "work_clock", type = HomeItemType.CLOCK_BANNER, pageIndex = 0, gridX = 0, gridY = 0, spanX = 4, spanY = 2),
                    HomeItem(id = "work_smart", type = HomeItemType.SMART_BAR, pageIndex = 0, gridX = 0, gridY = 2, spanX = 4, spanY = 1),
                    HomeItem(id = "work_space", type = HomeItemType.SPACE_CARD, pageIndex = 0, gridX = 0, gridY = 3, spanX = 4, spanY = 2, spaceId = "space_work")
                ),
                activeSpaceIds = listOf("space_work")
            ),
            Profile(
                id = "profile_dev",
                name = "DEV",
                iconName = "code",
                accentColorHex = "#C084FC",
                themeMode = ThemeMode.DARK,
                dockApps = listOf("com.termux", "com.github.android", "com.android.chrome", "com.google.android.documentsui"),
                homeItems = listOf(
                    HomeItem(id = "dev_clock", type = HomeItemType.CLOCK_BANNER, pageIndex = 0, gridX = 0, gridY = 0, spanX = 4, spanY = 2),
                    HomeItem(id = "dev_space_card", type = HomeItemType.SPACE_CARD, pageIndex = 0, gridX = 0, gridY = 2, spanX = 4, spanY = 2, spaceId = "space_emo_launcher"),
                    HomeItem(id = "dev_smart", type = HomeItemType.SMART_BAR, pageIndex = 0, gridX = 0, gridY = 4, spanX = 4, spanY = 1)
                ),
                activeSpaceIds = listOf("space_emo_launcher")
            ),
            Profile(
                id = "profile_study",
                name = "STUDY",
                iconName = "menu_book",
                accentColorHex = "#38BDF8",
                themeMode = ThemeMode.MONOCHROME,
                dockApps = listOf("com.google.android.keep", "com.google.android.calculator", "com.notion.id"),
                homeItems = createDefaultHomeItems()
            ),
            Profile(
                id = "profile_relax",
                name = "RELAX",
                iconName = "spa",
                accentColorHex = "#F472B6",
                themeMode = ThemeMode.DARK,
                dockApps = listOf("com.spotify.music", "com.google.android.youtube", "com.netflix.mediaclient"),
                homeItems = createDefaultHomeItems()
            ),
            Profile(
                id = "profile_travel",
                name = "TRAVEL",
                iconName = "flight",
                accentColorHex = "#FB923C",
                themeMode = ThemeMode.DYNAMIC,
                dockApps = listOf("com.google.android.apps.maps", "com.ubercab", "com.airbnb.android"),
                homeItems = createDefaultHomeItems()
            ),
            Profile(
                id = "profile_gaming",
                name = "GAMING",
                iconName = "sports_esports",
                accentColorHex = "#E879F9",
                themeMode = ThemeMode.AMOLED,
                dockApps = listOf("com.discord", "com.google.android.play.games"),
                homeItems = createDefaultHomeItems()
            )
        )
    }

    private fun createDefaultHomeProfile(): Profile {
        return Profile(
            id = "profile_home",
            name = "HOME",
            iconName = "home",
            accentColorHex = "#D0BCFF",
            themeMode = ThemeMode.DYNAMIC,
            dockApps = listOf("com.google.android.dialer", "com.google.android.apps.messaging", "com.android.chrome", "com.google.android.apps.photos"),
            homeItems = createDefaultHomeItems()
        )
    }

    private fun createDefaultSpaces(): List<SpaceConfig> {
        return listOf(
            SpaceConfig(
                id = "space_emo_launcher",
                title = "EMO LAUNCHER",
                subtitle = "v0.1 · Main Branch",
                description = "Custom expressive Android HOME environment with spring physics and monochrome dynamic theming.",
                type = SpaceType.DEV,
                repoOwner = "purple-launcher",
                repoName = "purple-launcher",
                gitBranch = "main",
                accentColorHex = "#C084FC",
                tasks = listOf(
                    SpaceTask(id = "t1", title = "Implement monochrome wallpaper pipeline", isCompleted = true),
                    SpaceTask(id = "t2", title = "Optimize universal fuzzy search index", isCompleted = true),
                    SpaceTask(id = "t3", title = "Verify AppWidgetHost and multi-profile sync", isCompleted = true)
                )
            ),
            SpaceConfig(
                id = "space_work",
                title = "WORK PROJECTS",
                subtitle = "Sprint 14 · Delivery",
                description = "Focus on critical tasks and prioritized deliverables.",
                type = SpaceType.WORK,
                accentColorHex = "#818CF8",
                tasks = listOf(
                    SpaceTask(id = "w1", title = "Review team pull requests", isCompleted = true),
                    SpaceTask(id = "w2", title = "Prepare architecture demo notes", isCompleted = false)
                )
            )
        )
    }

    private fun Profile.toEntity() = ProfileEntity(
        id = id,
        name = name,
        iconName = iconName,
        accentColorHex = accentColorHex,
        themeMode = themeMode.name,
        wallpaperConfig = wallpaperConfig,
        dockApps = dockApps,
        hiddenApps = hiddenApps,
        favoriteApps = favoriteApps,
        homeItems = homeItems,
        activeSpaceIds = activeSpaceIds,
        isAutomated = isAutomated,
        createdTimestamp = createdTimestamp
    )

    private fun ProfileEntity.toModel() = Profile(
        id = id,
        name = name,
        iconName = iconName,
        accentColorHex = accentColorHex,
        themeMode = try { ThemeMode.valueOf(themeMode) } catch (_: Exception) { ThemeMode.DYNAMIC },
        wallpaperConfig = wallpaperConfig,
        dockApps = dockApps,
        hiddenApps = hiddenApps,
        favoriteApps = favoriteApps,
        homeItems = homeItems,
        activeSpaceIds = activeSpaceIds,
        isAutomated = isAutomated,
        createdTimestamp = createdTimestamp
    )

    private fun SpaceConfig.toEntity() = SpaceEntity(
        id = id,
        title = title,
        subtitle = subtitle,
        description = description,
        type = type.name,
        repoOwner = repoOwner,
        repoName = repoName,
        gitBranch = gitBranch,
        pinnedApps = pinnedApps,
        notes = notes,
        tasks = tasks,
        accentColorHex = accentColorHex
    )

    private fun SpaceEntity.toModel() = SpaceConfig(
        id = id,
        title = title,
        subtitle = subtitle,
        description = description,
        type = try { SpaceType.valueOf(type) } catch (_: Exception) { SpaceType.GENERAL },
        repoOwner = repoOwner,
        repoName = repoName,
        gitBranch = gitBranch,
        pinnedApps = pinnedApps,
        notes = notes,
        tasks = tasks,
        accentColorHex = accentColorHex
    )
}
