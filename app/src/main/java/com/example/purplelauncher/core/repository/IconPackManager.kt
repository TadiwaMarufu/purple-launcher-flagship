package com.example.purplelauncher.core.repository

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class InstalledIconPack(
    val packageName: String,
    val name: String,
    val iconBitmap: Bitmap?
)

class IconPackManager(private val context: Context) {

    private val packageManager: PackageManager = context.packageManager

    suspend fun getInstalledIconPacks(): List<InstalledIconPack> = withContext(Dispatchers.IO) {
        val iconPackIntents = listOf(
            Intent("org.adw.launcher.THEMES"),
            Intent("com.novalauncher.THEME"),
            Intent("com.gau.go.launcherex.theme"),
            Intent("com.dlto.atom.launcher.THEME"),
            Intent("com.teslacoilsw.launcher.THEME")
        )

        val foundPacks = mutableMapOf<String, InstalledIconPack>()

        for (intent in iconPackIntents) {
            val resolveInfos: List<ResolveInfo> = packageManager.queryIntentActivities(intent, PackageManager.GET_META_DATA)
            for (info in resolveInfos) {
                val pkg = info.activityInfo.packageName
                if (!foundPacks.containsKey(pkg)) {
                    val label = info.loadLabel(packageManager).toString()
                    val iconBmp = try {
                        info.loadIcon(packageManager).toBitmap(64, 64)
                    } catch (_: Exception) {
                        null
                    }
                    foundPacks[pkg] = InstalledIconPack(
                        packageName = pkg,
                        name = label,
                        iconBitmap = iconBmp
                    )
                }
            }
        }

        foundPacks.values.toList()
    }

    suspend fun loadIconFromPack(appPackageName: String, iconPackPackage: String?): Bitmap? = withContext(Dispatchers.IO) {
        if (iconPackPackage.isNullOrEmpty()) return@withContext null
        try {
            val packResources = packageManager.getResourcesForApplication(iconPackPackage)
            // Try common naming conventions inside icon pack
            val cleanPkgName = appPackageName.replace(".", "_").lowercase()
            var resId = packResources.getIdentifier(cleanPkgName, "drawable", iconPackPackage)
            if (resId == 0) {
                resId = packResources.getIdentifier(appPackageName, "drawable", iconPackPackage)
            }
            if (resId != 0) {
                val drawable = packResources.getDrawable(resId, null)
                return@withContext drawable.toBitmap(128, 128)
            }
        } catch (_: Exception) {}
        null
    }

    suspend fun getAppIconBitmap(packageName: String, iconPackPackage: String? = null): Bitmap? = withContext(Dispatchers.IO) {
        // Try icon pack first if configured
        if (!iconPackPackage.isNullOrEmpty()) {
            val packBmp = loadIconFromPack(packageName, iconPackPackage)
            if (packBmp != null) return@withContext packBmp
        }

        // Standard system icon
        try {
            val iconDrawable: Drawable = packageManager.getApplicationIcon(packageName)
            iconDrawable.toBitmap(width = 128, height = 128)
        } catch (_: Exception) {
            null
        }
    }
}
