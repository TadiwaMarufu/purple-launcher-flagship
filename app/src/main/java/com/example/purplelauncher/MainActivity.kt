package com.example.purplelauncher

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.purplelauncher.core.model.*
import com.example.purplelauncher.ui.components.AppContextMenu
import com.example.purplelauncher.ui.components.ParallaxWallpaperView
import com.example.purplelauncher.ui.screens.*
import com.example.purplelauncher.ui.theme.ThePurpleLauncherTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class LauncherScreen {
    HOME,
    APP_DRAWER,
    UNIVERSAL_SEARCH,
    WALLPAPER_STUDIO,
    DEVELOPER_HUB,
    SETTINGS
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as PurpleLauncherApplication
        val appRepo = app.appRepository
        val profileRepo = app.profileRepository
        val settingsRepo = app.settingsRepository
        val wallpaperRepo = app.wallpaperRepository
        val searchRepo = app.searchRepository
        val devRepo = app.developerRepository

        setContent {
            val scope = rememberCoroutineScope()

            // State collection
            val installedApps by appRepo.installedApps.collectAsStateWithLifecycle()
            val activeProfile by profileRepo.activeProfile.collectAsStateWithLifecycle()
            val allProfiles by profileRepo.allProfiles.collectAsStateWithLifecycle(initialValue = emptyList())
            val allSpaces by profileRepo.allSpaces.collectAsStateWithLifecycle(initialValue = emptyList())
            val themeConfig by settingsRepo.themeConfig.collectAsStateWithLifecycle(initialValue = ThemeConfig())
            val gestureConfig by settingsRepo.gestureConfig.collectAsStateWithLifecycle(initialValue = GestureConfig())
            val activeWidgets by settingsRepo.activeWidgets.collectAsStateWithLifecycle(initialValue = emptyList())
            val isOnboardingCompleted by settingsRepo.isOnboardingCompleted.collectAsStateWithLifecycle(initialValue = true)

            // UI Flow State
            var currentScreen by remember { mutableStateOf(LauncherScreen.HOME) }
            var showProfileSwitcher by remember { mutableStateOf(false) }
            var selectedAppForMenu by remember { mutableStateOf<AppInfo?>(null) }
            var devHubInitialPackage by remember { mutableStateOf<String?>(null) }
            var devHubInitialSpaceId by remember { mutableStateOf<String?>(null) }

            // Wallpaper state
            var wallpaperBitmap by remember { mutableStateOf<Bitmap?>(null) }
            var wallpaperLuminance by remember { mutableStateOf(0.2f) }

            // Process wallpaper when active profile config changes
            LaunchedEffect(activeProfile.id, activeProfile.wallpaperConfig) {
                withContext(Dispatchers.IO) {
                    val (bmp, analysis) = wallpaperRepo.processWallpaperBitmap(
                        sourceUriString = activeProfile.wallpaperConfig.sourceUri,
                        config = activeProfile.wallpaperConfig
                    )
                    wallpaperBitmap = bmp
                    wallpaperLuminance = analysis.averageLuminance
                }
            }

            ThePurpleLauncherTheme(
                themeConfig = themeConfig,
                wallpaperLuminance = wallpaperLuminance,
                accentOverrideHex = activeProfile.accentColorHex
            ) {
                // Back press handling
                BackHandler(enabled = currentScreen != LauncherScreen.HOME || showProfileSwitcher || selectedAppForMenu != null) {
                    when {
                        selectedAppForMenu != null -> selectedAppForMenu = null
                        showProfileSwitcher -> showProfileSwitcher = false
                        currentScreen != LauncherScreen.HOME -> currentScreen = LauncherScreen.HOME
                    }
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    // Wallpaper layer
                    ParallaxWallpaperView(
                        wallpaperBitmap = wallpaperBitmap,
                        config = activeProfile.wallpaperConfig
                    )

                    // Onboarding flow or Main launcher screen
                    if (!isOnboardingCompleted) {
                        OnboardingScreen(
                            onComplete = { selectedTheme ->
                                scope.launch {
                                    settingsRepo.updateThemeConfig(selectedTheme)
                                    settingsRepo.setOnboardingCompleted(true)
                                }
                            }
                        )
                    } else {
                        when (currentScreen) {
                            LauncherScreen.HOME -> {
                                HomeScreen(
                                    activeProfile = activeProfile,
                                    installedApps = installedApps,
                                    themeConfig = themeConfig,
                                    activeWidgets = activeWidgets,
                                    activeSpaces = allSpaces,
                                    onLaunchApp = { appInfo ->
                                        scope.launch {
                                            appRepo.launchApp(appInfo.packageName, appInfo.activityName)
                                        }
                                    },
                                    onOpenDrawer = { currentScreen = LauncherScreen.APP_DRAWER },
                                    onOpenSearch = { currentScreen = LauncherScreen.UNIVERSAL_SEARCH },
                                    onOpenProfileSwitcher = { showProfileSwitcher = true },
                                    onOpenWallpaperStudio = { currentScreen = LauncherScreen.WALLPAPER_STUDIO },
                                    onOpenDeveloperHub = { spaceId ->
                                        devHubInitialSpaceId = spaceId
                                        devHubInitialPackage = null
                                        currentScreen = LauncherScreen.DEVELOPER_HUB
                                    },
                                    onOpenSettings = { currentScreen = LauncherScreen.SETTINGS },
                                    onUpdateThemeConfig = { newTheme ->
                                        scope.launch {
                                            settingsRepo.updateThemeConfig(newTheme)
                                        }
                                    },
                                    onAddWidget = { type, span ->
                                        scope.launch {
                                            settingsRepo.addWidget(type, span)
                                        }
                                    },
                                    onRemoveWidget = { widgetId ->
                                        scope.launch {
                                            settingsRepo.removeWidget(widgetId)
                                        }
                                    },
                                    onUpdateWidgetSpan = { widgetId, span ->
                                        scope.launch {
                                            settingsRepo.updateWidgetSpan(widgetId, span)
                                        }
                                    },
                                    onMoveWidget = { widgetId, direction ->
                                        scope.launch {
                                            settingsRepo.moveWidget(widgetId, direction)
                                        }
                                    },
                                    onAppLongClick = { appInfo -> selectedAppForMenu = appInfo },
                                    onCustomizeHome = { currentScreen = LauncherScreen.WALLPAPER_STUDIO }
                                )
                            }
                            LauncherScreen.APP_DRAWER -> {
                                AppDrawerScreen(
                                    installedApps = installedApps,
                                    themeConfig = themeConfig,
                                    onLaunchApp = { appInfo ->
                                        scope.launch {
                                            appRepo.launchApp(appInfo.packageName, appInfo.activityName)
                                        }
                                        currentScreen = LauncherScreen.HOME
                                    },
                                    onAppLongClick = { appInfo -> selectedAppForMenu = appInfo },
                                    onCloseDrawer = { currentScreen = LauncherScreen.HOME }
                                )
                            }
                            LauncherScreen.UNIVERSAL_SEARCH -> {
                                UniversalSearchScreen(
                                    searchRepository = searchRepo,
                                    profiles = allProfiles,
                                    spaces = allSpaces,
                                    onLaunchApp = { pkg, act ->
                                        scope.launch {
                                            appRepo.launchApp(pkg, act)
                                        }
                                    },
                                    onSwitchProfile = { profId ->
                                        profileRepo.switchProfile(profId)
                                    },
                                    onOpenSpace = { spaceId ->
                                        devHubInitialSpaceId = spaceId
                                        currentScreen = LauncherScreen.DEVELOPER_HUB
                                    },
                                    onOpenDevTool = { _ ->
                                        devHubInitialPackage = null
                                        currentScreen = LauncherScreen.DEVELOPER_HUB
                                    },
                                    onCloseSearch = { currentScreen = LauncherScreen.HOME }
                                )
                            }
                            LauncherScreen.WALLPAPER_STUDIO -> {
                                WallpaperStudioScreen(
                                    currentConfig = activeProfile.wallpaperConfig,
                                    wallpaperRepository = wallpaperRepo,
                                    onSaveConfig = { newConfig, processedBmp ->
                                        scope.launch {
                                            profileRepo.updateWallpaperConfig(activeProfile.id, newConfig)
                                            wallpaperBitmap = processedBmp
                                        }
                                    },
                                    onCloseStudio = { currentScreen = LauncherScreen.HOME }
                                )
                            }
                            LauncherScreen.DEVELOPER_HUB -> {
                                DeveloperHubScreen(
                                    developerRepository = devRepo,
                                    installedApps = installedApps,
                                    activeSpaces = allSpaces,
                                    initialSpaceId = devHubInitialSpaceId,
                                    initialPackageName = devHubInitialPackage,
                                    onLaunchApp = { appInfo ->
                                        scope.launch {
                                            appRepo.launchApp(appInfo.packageName, appInfo.activityName)
                                        }
                                    },
                                    onCloseHub = { currentScreen = LauncherScreen.HOME }
                                )
                            }
                            LauncherScreen.SETTINGS -> {
                                SettingsScreen(
                                    themeConfig = themeConfig,
                                    gestureConfig = gestureConfig,
                                    settingsRepository = settingsRepo,
                                    profiles = allProfiles,
                                    spaces = allSpaces,
                                    onUpdateTheme = { newTheme ->
                                        scope.launch {
                                            settingsRepo.updateThemeConfig(newTheme)
                                        }
                                    },
                                    onUpdateGestures = { newGestures ->
                                        scope.launch {
                                            settingsRepo.updateGestureConfig(newGestures)
                                        }
                                    },
                                    onCloseSettings = { currentScreen = LauncherScreen.HOME }
                                )
                            }
                        }

                        // Modal Profile Switcher
                        if (showProfileSwitcher) {
                            ProfileSwitcherSheet(
                                profiles = allProfiles,
                                activeProfileId = activeProfile.id,
                                onSelectProfile = { profId ->
                                    profileRepo.switchProfile(profId)
                                    showProfileSwitcher = false
                                },
                                onCreateProfile = {
                                    scope.launch {
                                        profileRepo.createProfile("NEW SPACE", "star", "#C084FC")
                                    }
                                },
                                onDuplicateProfile = { profId ->
                                    scope.launch {
                                        profileRepo.duplicateProfile(profId)
                                    }
                                },
                                onDeleteProfile = { profId ->
                                    scope.launch {
                                        profileRepo.deleteProfile(profId)
                                    }
                                },
                                onDismiss = { showProfileSwitcher = false }
                            )
                        }

                        // App Long Press Context Menu
                        if (selectedAppForMenu != null) {
                            val targetApp = selectedAppForMenu!!
                            AppContextMenu(
                                app = targetApp,
                                onDismiss = { selectedAppForMenu = null },
                                onOpen = {
                                    scope.launch {
                                        appRepo.launchApp(targetApp.packageName, targetApp.activityName)
                                    }
                                },
                                onAddToHome = {
                                    scope.launch {
                                        val currentFavs = activeProfile.favoriteApps.toMutableList()
                                        if (!currentFavs.contains(targetApp.packageName)) {
                                            currentFavs.add(targetApp.packageName)
                                            val updated = activeProfile.copy(favoriteApps = currentFavs)
                                            profileRepo.saveProfile(updated)
                                        }
                                    }
                                },
                                onToggleFavorite = {
                                    scope.launch {
                                        appRepo.toggleFavorite(targetApp.packageName, !targetApp.isFavorite)
                                    }
                                },
                                onToggleHidden = {
                                    scope.launch {
                                        appRepo.toggleHidden(targetApp.packageName, !targetApp.isHidden)
                                    }
                                },
                                onAppInfo = {
                                    appRepo.openAppDetails(targetApp.packageName)
                                },
                                onUninstall = {
                                    appRepo.uninstallApp(targetApp.packageName)
                                },
                                onInspectPackage = {
                                    devHubInitialPackage = targetApp.packageName
                                    currentScreen = LauncherScreen.DEVELOPER_HUB
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
