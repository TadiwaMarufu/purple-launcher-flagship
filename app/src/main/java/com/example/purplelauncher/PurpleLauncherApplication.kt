package com.example.purplelauncher

import android.app.Application
import com.example.purplelauncher.core.database.LauncherDatabase
import com.example.purplelauncher.core.repository.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class PurpleLauncherApplication : Application() {

    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val database: LauncherDatabase by lazy {
        LauncherDatabase.getInstance(this)
    }

    val appRepository: AppRepository by lazy {
        AppRepository(this, database.launcherDao(), applicationScope)
    }

    val profileRepository: ProfileRepository by lazy {
        ProfileRepository(database.launcherDao(), applicationScope)
    }

    val wallpaperRepository: WallpaperRepository by lazy {
        WallpaperRepository(this)
    }

    val searchRepository: SearchRepository by lazy {
        SearchRepository(this, appRepository)
    }

    val developerRepository: DeveloperRepository by lazy {
        DeveloperRepository(this)
    }

    val settingsRepository: SettingsRepository by lazy {
        SettingsRepository(this)
    }

    val widgetRepository: WidgetRepository by lazy {
        WidgetRepository(this)
    }

    override fun onCreate() {
        super.onCreate()
    }
}
