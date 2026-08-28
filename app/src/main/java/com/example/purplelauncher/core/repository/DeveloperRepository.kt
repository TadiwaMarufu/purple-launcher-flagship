package com.example.purplelauncher.core.repository

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.os.BatteryManager
import android.os.Build
import com.example.purplelauncher.core.model.AppInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.regex.Pattern

data class DetailedPackageInfo(
    val packageName: String,
    val appName: String,
    val versionName: String,
    val versionCode: Long,
    val targetSdk: Int,
    val minSdk: Int,
    val sourceDir: String,
    val apkSizeMb: Double,
    val installDate: String,
    val updateDate: String,
    val isSystemApp: Boolean,
    val permissions: List<String>,
    val signatureSha256: String,
    val activitiesCount: Int,
    val servicesCount: Int,
    val receiversCount: Int
)

data class SystemDeviceInfo(
    val deviceModel: String,
    val manufacturer: String,
    val androidVersion: String,
    val apiLevel: Int,
    val buildId: String,
    val supportedAbis: String,
    val totalRamMb: Long,
    val availableRamMb: Long,
    val screenDensityDpi: Int,
    val screenResolution: String,
    val batteryPercentage: Int,
    val isCharging: Boolean
)

data class GitHubRepoSummary(
    val name: String,
    val fullName: String,
    val description: String,
    val stars: Int,
    val forks: Int,
    val openIssues: Int,
    val latestCommitMessage: String,
    val latestCommitHash: String,
    val latestCommitAuthor: String,
    val workflowStatus: String = "Success"
)

class DeveloperRepository(private val context: Context) {

    fun getDetailedPackageInfo(packageName: String): DetailedPackageInfo? {
        return try {
            val pm = context.packageManager
            val flags = PackageManager.GET_PERMISSIONS or
                    PackageManager.GET_ACTIVITIES or
                    PackageManager.GET_SERVICES or
                    PackageManager.GET_RECEIVERS

            val pkgInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pm.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(flags.toLong()))
            } else {
                @Suppress("DEPRECATION")
                pm.getPackageInfo(packageName, flags)
            }

            val appInfo = pkgInfo.applicationInfo
            val appName = appInfo?.loadLabel(pm)?.toString() ?: packageName
            val isSystem = appInfo != null && (appInfo.flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0
            val targetSdk = appInfo?.targetSdkVersion ?: 34
            val minSdk = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && appInfo != null) appInfo.minSdkVersion else 24
            val sourceDir = appInfo?.sourceDir ?: ""

            var apkSizeMb = 0.0
            if (sourceDir.isNotBlank()) {
                val f = File(sourceDir)
                if (f.exists()) {
                    apkSizeMb = f.length().toDouble() / (1024 * 1024)
                }
            }

            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val installDate = dateFormat.format(Date(pkgInfo.firstInstallTime))
            val updateDate = dateFormat.format(Date(pkgInfo.lastUpdateTime))

            val permissions = pkgInfo.requestedPermissions?.toList() ?: emptyList()
            val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                pkgInfo.longVersionCode
            } else {
                @Suppress("DEPRECATION")
                pkgInfo.versionCode.toLong()
            }

            val sigSha256 = getSignatureSha256(packageName)

            DetailedPackageInfo(
                packageName = packageName,
                appName = appName,
                versionName = pkgInfo.versionName ?: "1.0",
                versionCode = versionCode,
                targetSdk = targetSdk,
                minSdk = minSdk,
                sourceDir = sourceDir,
                apkSizeMb = apkSizeMb,
                installDate = installDate,
                updateDate = updateDate,
                isSystemApp = isSystem,
                permissions = permissions,
                signatureSha256 = sigSha256,
                activitiesCount = pkgInfo.activities?.size ?: 0,
                servicesCount = pkgInfo.services?.size ?: 0,
                receiversCount = pkgInfo.receivers?.size ?: 0
            )
        } catch (_: Exception) {
            null
        }
    }

    private fun getSignatureSha256(packageName: String): String {
        return try {
            val pm = context.packageManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val signingInfo = pm.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(PackageManager.GET_SIGNING_CERTIFICATES.toLong())).signingInfo
                val signatures = signingInfo?.apkContentsSigners ?: signingInfo?.signingCertificateHistory
                signatures?.firstOrNull()?.let { calcSha256(it.toByteArray()) } ?: "N/A"
            } else {
                @Suppress("DEPRECATION")
                val pkgInfo = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
                @Suppress("DEPRECATION")
                pkgInfo.signatures?.firstOrNull()?.let { calcSha256(it.toByteArray()) } ?: "N/A"
            }
        } catch (_: Exception) {
            "N/A"
        }
    }

    fun getSystemDeviceInfo(): SystemDeviceInfo {
        val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        actManager?.getMemoryInfo(memInfo)

        val totalRamMb = (memInfo.totalMem / (1024 * 1024))
        val availRamMb = (memInfo.availMem / (1024 * 1024))

        val dm = context.resources.displayMetrics
        val dpi = dm.densityDpi
        val res = "${dm.widthPixels} × ${dm.heightPixels}"

        var batteryPct = 100
        var isCharging = false
        try {
            val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { filter ->
                context.registerReceiver(null, filter)
            }
            batteryStatus?.let { intent ->
                val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                batteryPct = if (level >= 0 && scale > 0) (level * 100 / scale) else 100
                val status: Int = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
            }
        } catch (_: Exception) {}

        val abis = Build.SUPPORTED_ABIS.joinToString(", ")

        return SystemDeviceInfo(
            deviceModel = Build.MODEL,
            manufacturer = Build.MANUFACTURER,
            androidVersion = Build.VERSION.RELEASE,
            apiLevel = Build.VERSION.SDK_INT,
            buildId = Build.ID,
            supportedAbis = abis,
            totalRamMb = totalRamMb,
            availableRamMb = availRamMb,
            screenDensityDpi = dpi,
            screenResolution = res,
            batteryPercentage = batteryPct,
            isCharging = isCharging
        )
    }

    // Developer Utilities
    fun formatJson(input: String): Pair<Boolean, String> {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return Pair(false, "Input is empty")
        return try {
            if (trimmed.startsWith("{")) {
                val obj = JSONObject(trimmed)
                Pair(true, obj.toString(2))
            } else if (trimmed.startsWith("[")) {
                val arr = JSONArray(trimmed)
                Pair(true, arr.toString(2))
            } else {
                Pair(false, "Invalid JSON root element")
            }
        } catch (e: Exception) {
            Pair(false, "Invalid JSON: ${e.localizedMessage}")
        }
    }

    fun encodeBase64(input: String): String {
        return android.util.Base64.encodeToString(input.toByteArray(Charsets.UTF_8), android.util.Base64.NO_WRAP)
    }

    fun decodeBase64(input: String): Pair<Boolean, String> {
        return try {
            val bytes = android.util.Base64.decode(input.trim(), android.util.Base64.DEFAULT)
            Pair(true, String(bytes, Charsets.UTF_8))
        } catch (e: Exception) {
            Pair(false, "Invalid Base64: ${e.localizedMessage}")
        }
    }

    fun computeHash(input: String, algorithm: String = "SHA-256"): String {
        return try {
            val md = MessageDigest.getInstance(algorithm)
            val digest = md.digest(input.toByteArray(Charsets.UTF_8))
            digest.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            "Error: ${e.message}"
        }
    }

    fun testRegex(patternStr: String, testString: String): Pair<Boolean, String> {
        return try {
            val pattern = Pattern.compile(patternStr)
            val matcher = pattern.matcher(testString)
            val matches = mutableListOf<String>()
            var count = 0
            while (matcher.find()) {
                count++
                matches.add("Match $count: '${matcher.group()}' at [${matcher.start()}..${matcher.end()}]")
            }
            if (count > 0) {
                Pair(true, "Found $count match(es):\n" + matches.joinToString("\n"))
            } else {
                Pair(false, "No matches found.")
            }
        } catch (e: Exception) {
            Pair(false, "Regex syntax error: ${e.message}")
        }
    }

    private fun calcSha256(bytes: ByteArray): String {
        val md = MessageDigest.getInstance("SHA-256")
        val d = md.digest(bytes)
        return d.joinToString(":") { "%02X".format(it) }
    }
}
