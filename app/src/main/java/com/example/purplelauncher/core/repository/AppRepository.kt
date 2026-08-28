package com.example.purplelauncher.core.repository

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import com.example.purplelauncher.core.database.AppCustomEntity
import com.example.purplelauncher.core.database.LauncherDao
import com.example.purplelauncher.core.model.AppCategory
import com.example.purplelauncher.core.model.AppInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File

class AppRepository(
    private val context: Context,
    private val launcherDao: LauncherDao,
    private val scope: CoroutineScope
) {
    private val _installedApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val installedApps: StateFlow<List<AppInfo>> = _installedApps.asStateFlow()

    val customData: Flow<List<AppCustomEntity>> = launcherDao.getAllAppCustomData()

    init {
        scope.launch(Dispatchers.IO) {
            combine(
                flow { emit(loadRawApps()) },
                customData
            ) { rawApps, customs ->
                mergeCustomData(rawApps, customs)
            }.collect { merged ->
                _installedApps.value = merged
            }
        }
    }

    suspend fun refreshInstalledApps() {
        val raw = loadRawApps()
        val customs = customData.firstOrNull() ?: emptyList()
        _installedApps.value = mergeCustomData(raw, customs)
    }

    private fun loadRawApps(): List<AppInfo> {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val resolveInfos = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.queryIntentActivities(intent, PackageManager.ResolveInfoFlags.of(0))
        } else {
            @Suppress("DEPRECATION")
            pm.queryIntentActivities(intent, 0)
        }

        val myPackage = context.packageName

        return resolveInfos.mapNotNull { resolveInfo ->
            val pkg = resolveInfo.activityInfo.packageName
            // Hide own launcher from app drawer to avoid self-launch loop
            if (pkg == myPackage) return@mapNotNull null

            val activityName = resolveInfo.activityInfo.name
            val label = resolveInfo.loadLabel(pm).toString()

            var isSystem = false
            var installTime = 0L
            var updateTime = 0L
            var versionName = "1.0"
            var versionCode = 1L
            var targetSdk = 34
            var minSdk = 24
            var permissionsCount = 0
            var sizeStr = ""
            var appCategory = AppCategory.OTHER

            try {
                val pkgInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    pm.getPackageInfo(pkg, PackageManager.PackageInfoFlags.of(PackageManager.GET_PERMISSIONS.toLong()))
                } else {
                    @Suppress("DEPRECATION")
                    pm.getPackageInfo(pkg, PackageManager.GET_PERMISSIONS)
                }

                val appInfo = pkgInfo.applicationInfo
                if (appInfo != null) {
                    isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                    targetSdk = appInfo.targetSdkVersion
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        minSdk = appInfo.minSdkVersion
                    }
                    if (appInfo.sourceDir != null) {
                        val file = File(appInfo.sourceDir)
                        if (file.exists()) {
                            val sizeMb = file.length().toDouble() / (1024 * 1024)
                            sizeStr = String.format("%.1f MB", sizeMb)
                        }
                    }
                }

                installTime = pkgInfo.firstInstallTime
                updateTime = pkgInfo.lastUpdateTime
                versionName = pkgInfo.versionName ?: "1.0"
                versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    pkgInfo.longVersionCode
                } else {
                    @Suppress("DEPRECATION")
                    pkgInfo.versionCode.toLong()
                }
                permissionsCount = pkgInfo.requestedPermissions?.size ?: 0
                appCategory = categorizeApp(pkg, label, appInfo)
            } catch (_: Exception) {}

            AppInfo(
                packageName = pkg,
                activityName = activityName,
                label = label,
                isSystemApp = isSystem,
                installTime = installTime,
                lastUpdateTime = updateTime,
                versionName = versionName,
                versionCode = versionCode,
                targetSdk = targetSdk,
                minSdk = minSdk,
                category = appCategory,
                permissionsCount = permissionsCount,
                appSizeFormatted = sizeStr
            )
        }.sortedBy { it.label.lowercase() }
    }

    private fun mergeCustomData(raw: List<AppInfo>, customs: List<AppCustomEntity>): List<AppInfo> {
        val customMap = customs.associateBy { it.packageName }
        return raw.map { app ->
            val custom = customMap[app.packageName]
            if (custom != null) {
                val cat = if (!custom.categoryOverride.isNullOrBlank()) {
                    try { AppCategory.valueOf(custom.categoryOverride) } catch (_: Exception) { app.category }
                } else {
                    app.category
                }
                app.copy(
                    customLabel = custom.customLabel,
                    isFavorite = custom.isFavorite,
                    isHidden = custom.isHidden,
                    launchCount = custom.launchCount,
                    lastLaunchedTime = custom.lastLaunchedTime,
                    category = cat
                )
            } else {
                app
            }
        }.sortedBy { it.displayTitle.lowercase() }
    }

    private fun categorizeApp(pkg: String, label: String, appInfo: ApplicationInfo?): AppCategory {
        val p = pkg.lowercase()
        val l = label.lowercase()

        // Match Developer
        if (p.contains("github") || p.contains("termux") || p.contains("git") || p.contains("code") ||
            p.contains("ide") || p.contains("terminal") || p.contains("studio") || p.contains("sqlite") ||
            p.contains("debug") || l.contains("github") || l.contains("termux") || l.contains("terminal") ||
            l.contains("dev") || l.contains("code")
        ) return AppCategory.DEVELOPER

        // Match Communication
        if (p.contains("dialer") || p.contains("phone") || p.contains("contact") || p.contains("messaging") ||
            p.contains("sms") || p.contains("mms") || p.contains("whatsapp") || p.contains("telegram") ||
            p.contains("signal") || p.contains("viber") || p.contains("discord") || p.contains("slack") ||
            p.contains("teams") || p.contains("zoom") || p.contains("meet") || l.contains("phone") ||
            l.contains("messages") || l.contains("contacts") || l.contains("chat")
        ) return AppCategory.COMMUNICATION

        // Match Social
        if (p.contains("instagram") || p.contains("facebook") || p.contains("twitter") || p.contains("x.com") ||
            p.contains("tiktok") || p.contains("snapchat") || p.contains("reddit") || p.contains("linkedin") ||
            p.contains("pinterest") || p.contains("threads") || l.contains("instagram") || l.contains("reddit")
        ) return AppCategory.SOCIAL

        // Match Media
        if (p.contains("spotify") || p.contains("music") || p.contains("youtube") || p.contains("netflix") ||
            p.contains("podcast") || p.contains("audio") || p.contains("video") || p.contains("camera") ||
            p.contains("gallery") || p.contains("photo") || p.contains("vlc") || p.contains("soundcloud") ||
            l.contains("music") || l.contains("camera") || l.contains("photos") || l.contains("gallery") ||
            l.contains("video") || l.contains("player")
        ) return AppCategory.MEDIA

        // Match Productivity
        if (p.contains("mail") || p.contains("gmail") || p.contains("calendar") || p.contains("keep") ||
            p.contains("notes") || p.contains("docs") || p.contains("sheet") || p.contains("office") ||
            p.contains("notion") || p.contains("todo") || p.contains("task") || p.contains("pdf") ||
            p.contains("drive") || p.contains("dropbox") || l.contains("calendar") || l.contains("notes") ||
            l.contains("tasks") || l.contains("mail") || l.contains("drive")
        ) return AppCategory.PRODUCTIVITY

        // Match Shopping & Finance
        if (p.contains("bank") || p.contains("pay") || p.contains("wallet") || p.contains("finance") ||
            p.contains("crypto") || p.contains("revolut") || p.contains("paypal") || l.contains("wallet") ||
            l.contains("bank") || l.contains("pay")
        ) return AppCategory.FINANCE

        if (p.contains("amazon") || p.contains("shop") || p.contains("store") || p.contains("ebay") ||
            p.contains("aliexpress") || l.contains("shop") || l.contains("store")
        ) return AppCategory.SHOPPING

        // Match Travel
        if (p.contains("map") || p.contains("uber") || p.contains("bolt") || p.contains("transit") ||
            p.contains("waze") || p.contains("booking") || p.contains("airbnb") || p.contains("flight") ||
            l.contains("maps") || l.contains("navigation") || l.contains("travel")
        ) return AppCategory.TRAVEL

        // Match Games
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && appInfo != null && appInfo.category == ApplicationInfo.CATEGORY_GAME) {
            return AppCategory.GAMES
        }
        if (p.contains("game") || p.contains("play.games") || p.contains("unity") || p.contains("chess") ||
            p.contains("sudoku") || l.contains("game") || l.contains("chess")
        ) return AppCategory.GAMES

        // Match Tools & System
        if (p.contains("settings") || p.contains("calculator") || p.contains("clock") || p.contains("deskclock") ||
            p.contains("browser") || p.contains("chrome") || p.contains("firefox") || p.contains("files") ||
            p.contains("manager") || p.contains("download") || l.contains("settings") || l.contains("calculator") ||
            l.contains("clock") || l.contains("files") || l.contains("browser")
        ) return AppCategory.TOOLS

        if (appInfo != null && (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0) {
            return AppCategory.SYSTEM
        }

        return AppCategory.OTHER
    }

    suspend fun launchApp(packageName: String, activityName: String? = null): Boolean {
        return try {
            val intent = if (!activityName.isNullOrBlank()) {
                Intent().apply {
                    setClassName(packageName, activityName)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
                }
            } else {
                context.packageManager.getLaunchIntentForPackage(packageName)?.apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
                }
            }
            if (intent != null) {
                context.startActivity(intent)
                launcherDao.incrementAppLaunch(packageName, System.currentTimeMillis())
                refreshInstalledApps()
                true
            } else {
                false
            }
        } catch (_: Exception) {
            false
        }
    }

    fun openAppDetails(packageName: String) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", packageName, null)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {}
    }

    fun uninstallApp(packageName: String) {
        try {
            val intent = Intent(Intent.ACTION_UNINSTALL_PACKAGE).apply {
                data = Uri.fromParts("package", packageName, null)
                putExtra(Intent.EXTRA_RETURN_RESULT, true)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {
            try {
                val fallbackIntent = Intent(Intent.ACTION_DELETE).apply {
                    data = Uri.fromParts("package", packageName, null)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(fallbackIntent)
            } catch (_: Exception) {}
        }
    }

    suspend fun toggleFavorite(packageName: String, isFavorite: Boolean) {
        val existing = customData.firstOrNull()?.find { it.packageName == packageName }
        if (existing != null) {
            launcherDao.insertAppCustom(existing.copy(isFavorite = isFavorite))
        } else {
            launcherDao.insertAppCustom(AppCustomEntity(packageName = packageName, isFavorite = isFavorite))
        }
        refreshInstalledApps()
    }

    suspend fun toggleHidden(packageName: String, isHidden: Boolean) {
        val existing = customData.firstOrNull()?.find { it.packageName == packageName }
        if (existing != null) {
            launcherDao.insertAppCustom(existing.copy(isHidden = isHidden))
        } else {
            launcherDao.insertAppCustom(AppCustomEntity(packageName = packageName, isHidden = isHidden))
        }
        refreshInstalledApps()
    }

    suspend fun setCustomLabel(packageName: String, label: String?) {
        val existing = customData.firstOrNull()?.find { it.packageName == packageName }
        if (existing != null) {
            launcherDao.insertAppCustom(existing.copy(customLabel = label))
        } else {
            launcherDao.insertAppCustom(AppCustomEntity(packageName = packageName, customLabel = label))
        }
        refreshInstalledApps()
    }

    suspend fun setCategoryOverride(packageName: String, category: AppCategory) {
        val existing = customData.firstOrNull()?.find { it.packageName == packageName }
        if (existing != null) {
            launcherDao.insertAppCustom(existing.copy(categoryOverride = category.name))
        } else {
            launcherDao.insertAppCustom(AppCustomEntity(packageName = packageName, categoryOverride = category.name))
        }
        refreshInstalledApps()
    }
}
