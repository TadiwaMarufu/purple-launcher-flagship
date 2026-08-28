package com.example.purplelauncher.core.repository

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import android.provider.Settings
import androidx.core.content.ContextCompat
import com.example.purplelauncher.core.model.AppInfo
import com.example.purplelauncher.core.model.Profile
import com.example.purplelauncher.core.model.SearchResult
import com.example.purplelauncher.core.model.SearchResultType
import com.example.purplelauncher.core.model.SpaceConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.max

class SearchRepository(
    private val context: Context,
    private val appRepository: AppRepository
) {
    suspend fun search(
        query: String,
        profiles: List<Profile> = emptyList(),
        spaces: List<SpaceConfig> = emptyList()
    ): List<SearchResult> = withContext(Dispatchers.Default) {
        val q = query.trim().lowercase()
        if (q.isEmpty()) {
            return@withContext getSuggestedResults()
        }

        val results = mutableListOf<SearchResult>()
        val installed = appRepository.installedApps.value

        // 1. Search Applications
        for (app in installed) {
            val titleMatch = fuzzyScore(q, app.displayTitle.lowercase())
            val pkgMatch = fuzzyScore(q, app.packageName.lowercase())
            val catMatch = fuzzyScore(q, app.category.name.lowercase())
            val score = maxOf(titleMatch * 1.5, pkgMatch * 0.9, catMatch * 0.7)

            if (score > 0.35) {
                results.add(
                    SearchResult(
                        id = "app_${app.packageName}",
                        type = SearchResultType.APP,
                        title = app.displayTitle,
                        subtitle = "${app.category.displayName} · ${app.packageName}",
                        packageName = app.packageName,
                        activityName = app.activityName,
                        score = score
                    )
                )
            }
        }

        // 2. Search Settings & Actions
        val systemSettings = listOf(
            Triple("Bluetooth Settings", "Wireless & Device Connection", Settings.ACTION_BLUETOOTH_SETTINGS),
            Triple("Wi-Fi Settings", "Network & Internet", Settings.ACTION_WIFI_SETTINGS),
            Triple("Display & Brightness", "System Display & Dark Theme", Settings.ACTION_DISPLAY_SETTINGS),
            Triple("Sound & Vibration", "Volume & Ringtone", Settings.ACTION_SOUND_SETTINGS),
            Triple("Battery & Power", "Battery usage and optimization", Settings.ACTION_BATTERY_SAVER_SETTINGS),
            Triple("Date & Time", "System Clock & Time Zone", Settings.ACTION_DATE_SETTINGS),
            Triple("Application Manager", "Installed Apps & Permissions", Settings.ACTION_APPLICATION_SETTINGS),
            Triple("Developer Options", "Android Development Settings", Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS),
            Triple("System Information", "About Phone & Hardware", Settings.ACTION_DEVICE_INFO_SETTINGS),
            Triple("Location Settings", "GPS & Privacy Services", Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        )

        for ((title, sub, intentAction) in systemSettings) {
            val score = max(fuzzyScore(q, title.lowercase()), fuzzyScore(q, sub.lowercase()) * 0.8)
            if (score > 0.38) {
                results.add(
                    SearchResult(
                        id = "setting_${intentAction}",
                        type = SearchResultType.SETTING,
                        title = title,
                        subtitle = sub,
                        actionIntent = intentAction,
                        score = score
                    )
                )
            }
        }

        // 3. Search Developer Tools
        val devTools = listOf(
            Triple("JSON Formatter & Validator", "Developer Tool · Validate & pretty-print JSON", "dev_tool_json"),
            Triple("Base64 Encoder / Decoder", "Developer Tool · Text & Token processing", "dev_tool_base64"),
            Triple("Hash Calculator (SHA-256 / MD5)", "Developer Tool · Cryptographic checksums", "dev_tool_hash"),
            Triple("Regex Tester", "Developer Tool · Regular expressions evaluator", "dev_tool_regex"),
            Triple("Package Inspector", "Developer Tool · SDK, permissions & APK specs", "dev_tool_inspector"),
            Triple("System & Hardware Monitor", "Developer Tool · CPU, RAM, display metrics", "dev_tool_system")
        )

        for ((title, sub, devId) in devTools) {
            val score = max(fuzzyScore(q, title.lowercase()), fuzzyScore(q, sub.lowercase()) * 0.7)
            if (score > 0.35 || q.contains("dev") || q.contains("tool")) {
                results.add(
                    SearchResult(
                        id = devId,
                        type = SearchResultType.DEV_TOOL,
                        title = title,
                        subtitle = sub,
                        actionIntent = devId,
                        score = max(score, 0.5)
                    )
                )
            }
        }

        // 4. Search Spaces & Profiles
        for (profile in profiles) {
            val score = fuzzyScore(q, profile.name.lowercase())
            if (score > 0.4) {
                results.add(
                    SearchResult(
                        id = "profile_${profile.id}",
                        type = SearchResultType.SHORTCUT,
                        title = "Switch to ${profile.name} Profile",
                        subtitle = "Launcher Profile · ${profile.accentColorHex}",
                        actionIntent = "switch_profile:${profile.id}",
                        score = score * 1.2
                    )
                )
            }
        }

        for (space in spaces) {
            val score = max(fuzzyScore(q, space.title.lowercase()), fuzzyScore(q, space.description.lowercase()) * 0.7)
            if (score > 0.35) {
                results.add(
                    SearchResult(
                        id = "space_${space.id}",
                        type = SearchResultType.SPACE,
                        title = space.title,
                        subtitle = "${space.type.displayName} · ${space.subtitle}",
                        actionIntent = "open_space:${space.id}",
                        score = score * 1.1
                    )
                )
            }
        }

        // 5. Query Contacts if Permission Granted
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            results.addAll(searchContacts(q))
        }

        // 6. Web Search Fallback
        results.add(
            SearchResult(
                id = "web_google_${q}",
                type = SearchResultType.WEB,
                title = "Search Google for \"$query\"",
                subtitle = "Web search provider",
                actionIntent = "https://www.google.com/search?q=${Uri.encode(query)}",
                score = 0.3
            )
        )
        results.add(
            SearchResult(
                id = "web_github_${q}",
                type = SearchResultType.WEB,
                title = "Search GitHub for \"$query\"",
                subtitle = "Repositories & Code",
                actionIntent = "https://github.com/search?q=${Uri.encode(query)}",
                score = 0.28
            )
        )

        results.sortedByDescending { it.score }
    }

    private fun getSuggestedResults(): List<SearchResult> {
        val list = mutableListOf<SearchResult>()
        // Top launched apps
        val topApps = appRepository.installedApps.value
            .sortedByDescending { it.launchCount }
            .take(6)

        for (app in topApps) {
            list.add(
                SearchResult(
                    id = "app_${app.packageName}",
                    type = SearchResultType.APP,
                    title = app.displayTitle,
                    subtitle = "Frequently Used · ${app.category.displayName}",
                    packageName = app.packageName,
                    activityName = app.activityName,
                    score = 1.0
                )
            )
        }

        // Quick Tools
        list.add(
            SearchResult(
                id = "dev_tool_inspector",
                type = SearchResultType.DEV_TOOL,
                title = "Package Inspector",
                subtitle = "Inspect installed apps, SDKs & permissions",
                actionIntent = "dev_tool_inspector",
                score = 0.9
            )
        )
        list.add(
            SearchResult(
                id = "setting_android.settings.SETTINGS",
                type = SearchResultType.SETTING,
                title = "System Settings",
                subtitle = "Device configuration",
                actionIntent = Settings.ACTION_SETTINGS,
                score = 0.85
            )
        )

        return list
    }

    private fun searchContacts(query: String): List<SearchResult> {
        val list = mutableListOf<SearchResult>()
        try {
            val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
            val projection = arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID
            )
            val selection = "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} LIKE ?"
            val selectionArgs = arrayOf("%$query%")
            val cursor: Cursor? = context.contentResolver.query(uri, projection, selection, selectionArgs, null)

            cursor?.use {
                val nameIdx = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val numIdx = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                var count = 0
                while (it.moveToNext() && count < 5) {
                    val name = if (nameIdx >= 0) it.getString(nameIdx) else "Contact"
                    val number = if (numIdx >= 0) it.getString(numIdx) else ""
                    list.add(
                        SearchResult(
                            id = "contact_${name}_$number",
                            type = SearchResultType.CONTACT,
                            title = name,
                            subtitle = number,
                            actionIntent = "tel:$number",
                            score = 0.8
                        )
                    )
                    count++
                }
            }
        } catch (_: Exception) {}
        return list
    }

    /**
     * Fast fuzzy match score between query and target (0.0 to 1.0)
     */
    fun fuzzyScore(query: String, target: String): Double {
        if (query.isEmpty() || target.isEmpty()) return 0.0
        if (target == query) return 1.0
        if (target.startsWith(query)) return 0.95
        if (target.contains(query)) return 0.80

        var qIdx = 0
        var matches = 0
        var consecutive = 0
        var maxConsecutive = 0

        for (i in target.indices) {
            if (qIdx < query.length && target[i] == query[qIdx]) {
                qIdx++
                matches++
                consecutive++
                if (consecutive > maxConsecutive) maxConsecutive = consecutive
            } else {
                consecutive = 0
            }
        }

        if (matches == query.length) {
            return 0.5 + (0.3 * maxConsecutive / query.length)
        }

        return (matches.toDouble() / query.length) * 0.4
    }
}
