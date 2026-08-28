package com.example.purplelauncher.core.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.purplelauncher.core.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "purple_launcher_prefs")

class SettingsRepository(private val context: Context) {

    private object PreferencesKeys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val PRIMARY_ACCENT = stringPreferencesKey("primary_accent")
        val SURFACE_OPACITY = floatPreferencesKey("surface_opacity")
        val GRID_ROWS = intPreferencesKey("grid_rows")
        val GRID_COLS = intPreferencesKey("grid_cols")
        val ICON_SHAPE = stringPreferencesKey("icon_shape")
        val ICON_SIZE = intPreferencesKey("icon_size")
        val SHOW_LABELS = booleanPreferencesKey("show_labels")
        val ICON_MONOCHROME = booleanPreferencesKey("icon_monochrome")
        val DOCK_SIZE = intPreferencesKey("dock_size")
        val DOCK_VISIBLE = booleanPreferencesKey("dock_visible")
        val REDUCED_MOTION = booleanPreferencesKey("reduced_motion")
        val FEED_ENABLED = booleanPreferencesKey("feed_enabled")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")

        val GESTURE_SWIPE_UP = stringPreferencesKey("gesture_swipe_up")
        val GESTURE_SWIPE_DOWN = stringPreferencesKey("gesture_swipe_down")
        val GESTURE_DOUBLE_TAP = stringPreferencesKey("gesture_double_tap")
        val GESTURE_LONG_PRESS = stringPreferencesKey("gesture_long_press")
        val GESTURE_TWO_FINGER = stringPreferencesKey("gesture_two_finger")

        val ACTIVE_WIDGETS_JSON = stringPreferencesKey("active_widgets_json")
    }

    private fun getDefaultWidgets(): List<ActiveHomeWidget> {
        return listOf(
            ActiveHomeWidget(type = WidgetType.NOW_PLAYING, span = WidgetSpan.WIDE),
            ActiveHomeWidget(type = WidgetType.LIVE_WEATHER, span = WidgetSpan.WIDE),
            ActiveHomeWidget(type = WidgetType.MULTI_BATTERY, span = WidgetSpan.WIDE),
            ActiveHomeWidget(type = WidgetType.DEVICE_CARE, span = WidgetSpan.MEDIUM),
            ActiveHomeWidget(type = WidgetType.FITNESS_TRACKER, span = WidgetSpan.MEDIUM),
            ActiveHomeWidget(type = WidgetType.QUICK_TOGGLES, span = WidgetSpan.WIDE),
            ActiveHomeWidget(type = WidgetType.DIGITAL_WELLBEING, span = WidgetSpan.WIDE),
            ActiveHomeWidget(type = WidgetType.AGENDA_CALENDAR, span = WidgetSpan.WIDE),
            ActiveHomeWidget(type = WidgetType.QUICK_NOTES, span = WidgetSpan.MEDIUM),
            ActiveHomeWidget(type = WidgetType.GEMINI_ASSISTANT, span = WidgetSpan.WIDE),
            ActiveHomeWidget(type = WidgetType.PHOTO_FRAME, span = WidgetSpan.MEDIUM),
            ActiveHomeWidget(type = WidgetType.CRYPTO_MARKET, span = WidgetSpan.MEDIUM)
        )
    }

    val activeWidgets: Flow<List<ActiveHomeWidget>> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { prefs ->
            val jsonStr = prefs[PreferencesKeys.ACTIVE_WIDGETS_JSON]
            if (jsonStr.isNullOrBlank()) {
                getDefaultWidgets()
            } else {
                try {
                    val array = JSONArray(jsonStr)
                    val list = mutableListOf<ActiveHomeWidget>()
                    for (i in 0 until array.length()) {
                        val obj = array.getJSONObject(i)
                        val id = obj.optString("id", java.util.UUID.randomUUID().toString())
                        val typeName = obj.getString("type")
                        val type = WidgetType.valueOf(typeName)
                        val spanName = obj.optString("span", type.defaultSpan.name)
                        val span = try { WidgetSpan.valueOf(spanName) } catch (_: Exception) { type.defaultSpan }
                        val customTitle = if (obj.has("customTitle")) obj.getString("customTitle") else null
                        val isEnabled = obj.optBoolean("isEnabled", true)
                        list.add(ActiveHomeWidget(id, type, span, customTitle, isEnabled))
                    }
                    if (list.isEmpty()) getDefaultWidgets() else list
                } catch (_: Exception) {
                    getDefaultWidgets()
                }
            }
        }

    suspend fun saveActiveWidgets(widgets: List<ActiveHomeWidget>) {
        val array = JSONArray()
        for (w in widgets) {
            val obj = JSONObject()
            obj.put("id", w.id)
            obj.put("type", w.type.name)
            obj.put("span", w.span.name)
            if (w.customTitle != null) obj.put("customTitle", w.customTitle)
            obj.put("isEnabled", w.isEnabled)
            array.put(obj)
        }
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.ACTIVE_WIDGETS_JSON] = array.toString()
        }
    }

    suspend fun addWidget(type: WidgetType, span: WidgetSpan = type.defaultSpan) {
        val current = activeWidgets.first().toMutableList()
        current.add(ActiveHomeWidget(type = type, span = span))
        saveActiveWidgets(current)
    }

    suspend fun removeWidget(widgetId: String) {
        val current = activeWidgets.first().filter { it.id != widgetId }
        saveActiveWidgets(current)
    }

    suspend fun updateWidgetSpan(widgetId: String, span: WidgetSpan) {
        val current = activeWidgets.first().map {
            if (it.id == widgetId) it.copy(span = span) else it
        }
        saveActiveWidgets(current)
    }

    suspend fun moveWidget(widgetId: String, direction: Int) {
        val current = activeWidgets.first().toMutableList()
        val index = current.indexOfFirst { it.id == widgetId }
        if (index != -1) {
            val newIndex = index + direction
            if (newIndex in 0 until current.size) {
                val item = current.removeAt(index)
                current.add(newIndex, item)
                saveActiveWidgets(current)
            }
        }
    }

    val themeConfig: Flow<ThemeConfig> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { prefs ->
            val themeModeStr = prefs[PreferencesKeys.THEME_MODE] ?: ThemeMode.DYNAMIC.name
            val themeMode = try { ThemeMode.valueOf(themeModeStr) } catch (_: Exception) { ThemeMode.DYNAMIC }
            val shapeStr = prefs[PreferencesKeys.ICON_SHAPE] ?: IconShape.SQUIRCLE.name
            val iconShape = try { IconShape.valueOf(shapeStr) } catch (_: Exception) { IconShape.SQUIRCLE }

            ThemeConfig(
                themeMode = themeMode,
                primaryAccentHex = prefs[PreferencesKeys.PRIMARY_ACCENT] ?: "#D0BCFF",
                surfaceOpacity = prefs[PreferencesKeys.SURFACE_OPACITY] ?: 0.85f,
                gridRows = prefs[PreferencesKeys.GRID_ROWS] ?: 5,
                gridCols = prefs[PreferencesKeys.GRID_COLS] ?: 4,
                iconShape = iconShape,
                iconSizeDp = prefs[PreferencesKeys.ICON_SIZE] ?: 54,
                showLabels = prefs[PreferencesKeys.SHOW_LABELS] ?: true,
                iconMonochrome = prefs[PreferencesKeys.ICON_MONOCHROME] ?: true,
                dockSize = prefs[PreferencesKeys.DOCK_SIZE] ?: 4,
                dockVisible = prefs[PreferencesKeys.DOCK_VISIBLE] ?: true,
                reducedMotion = prefs[PreferencesKeys.REDUCED_MOTION] ?: false,
                feedEnabled = prefs[PreferencesKeys.FEED_ENABLED] ?: false
            )
        }

    val gestureConfig: Flow<GestureConfig> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { prefs ->
            fun parseGesture(key: Preferences.Key<String>, default: GestureAction): GestureAction {
                val name = prefs[key] ?: return default
                return try { GestureAction.valueOf(name) } catch (_: Exception) { default }
            }

            GestureConfig(
                swipeUp = parseGesture(PreferencesKeys.GESTURE_SWIPE_UP, GestureAction.APP_DRAWER),
                swipeDown = parseGesture(PreferencesKeys.GESTURE_SWIPE_DOWN, GestureAction.UNIVERSAL_SEARCH),
                doubleTap = parseGesture(PreferencesKeys.GESTURE_DOUBLE_TAP, GestureAction.LOCK_SCREEN),
                longPress = parseGesture(PreferencesKeys.GESTURE_LONG_PRESS, GestureAction.CUSTOMIZE_HOME),
                twoFingerSwipe = parseGesture(PreferencesKeys.GESTURE_TWO_FINGER, GestureAction.QUICK_SETTINGS)
            )
        }

    val isOnboardingCompleted: Flow<Boolean> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { prefs -> prefs[PreferencesKeys.ONBOARDING_COMPLETED] ?: false }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.ONBOARDING_COMPLETED] = completed
        }
    }

    suspend fun updateThemeConfig(config: ThemeConfig) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.THEME_MODE] = config.themeMode.name
            prefs[PreferencesKeys.PRIMARY_ACCENT] = config.primaryAccentHex
            prefs[PreferencesKeys.SURFACE_OPACITY] = config.surfaceOpacity
            prefs[PreferencesKeys.GRID_ROWS] = config.gridRows
            prefs[PreferencesKeys.GRID_COLS] = config.gridCols
            prefs[PreferencesKeys.ICON_SHAPE] = config.iconShape.name
            prefs[PreferencesKeys.ICON_SIZE] = config.iconSizeDp
            prefs[PreferencesKeys.SHOW_LABELS] = config.showLabels
            prefs[PreferencesKeys.ICON_MONOCHROME] = config.iconMonochrome
            prefs[PreferencesKeys.DOCK_SIZE] = config.dockSize
            prefs[PreferencesKeys.DOCK_VISIBLE] = config.dockVisible
            prefs[PreferencesKeys.REDUCED_MOTION] = config.reducedMotion
            prefs[PreferencesKeys.FEED_ENABLED] = config.feedEnabled
        }
    }

    suspend fun updateGestureConfig(config: GestureConfig) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.GESTURE_SWIPE_UP] = config.swipeUp.name
            prefs[PreferencesKeys.GESTURE_SWIPE_DOWN] = config.swipeDown.name
            prefs[PreferencesKeys.GESTURE_DOUBLE_TAP] = config.doubleTap.name
            prefs[PreferencesKeys.GESTURE_LONG_PRESS] = config.longPress.name
            prefs[PreferencesKeys.GESTURE_TWO_FINGER] = config.twoFingerSwipe.name
        }
    }

    // Backup & Restore
    fun generateBackupJson(
        profiles: List<Profile>,
        spaces: List<SpaceConfig>,
        themeConfig: ThemeConfig,
        gestureConfig: GestureConfig
    ): String {
        val root = JSONObject()
        root.put("version", "0.1")
        root.put("timestamp", System.currentTimeMillis())
        root.put("appName", "The Purple Launcher")

        // Profiles
        val profArray = JSONArray()
        for (p in profiles) {
            val pObj = JSONObject()
            pObj.put("id", p.id)
            pObj.put("name", p.name)
            pObj.put("iconName", p.iconName)
            pObj.put("accentColorHex", p.accentColorHex)
            pObj.put("themeMode", p.themeMode.name)
            pObj.put("dockApps", JSONArray(p.dockApps))
            pObj.put("hiddenApps", JSONArray(p.hiddenApps))
            pObj.put("favoriteApps", JSONArray(p.favoriteApps))
            profArray.put(pObj)
        }
        root.put("profiles", profArray)

        // Spaces
        val spaceArray = JSONArray()
        for (s in spaces) {
            val sObj = JSONObject()
            sObj.put("id", s.id)
            sObj.put("title", s.title)
            sObj.put("subtitle", s.subtitle)
            sObj.put("description", s.description)
            sObj.put("type", s.type.name)
            sObj.put("accentColorHex", s.accentColorHex)
            spaceArray.put(sObj)
        }
        root.put("spaces", spaceArray)

        // Theme
        val tObj = JSONObject().apply {
            put("themeMode", themeConfig.themeMode.name)
            put("primaryAccentHex", themeConfig.primaryAccentHex)
            put("gridRows", themeConfig.gridRows)
            put("gridCols", themeConfig.gridCols)
            put("iconShape", themeConfig.iconShape.name)
            put("iconMonochrome", themeConfig.iconMonochrome)
            put("dockVisible", themeConfig.dockVisible)
        }
        root.put("themeConfig", tObj)

        return root.toString(2)
    }

    fun parseBackupJson(jsonString: String): LauncherBackup? {
        return try {
            val root = JSONObject(jsonString)
            val profArray = root.optJSONArray("profiles") ?: JSONArray()
            val profiles = mutableListOf<Profile>()
            for (i in 0 until profArray.length()) {
                val p = profArray.getJSONObject(i)
                val dockApps = mutableListOf<String>()
                val dArr = p.optJSONArray("dockApps")
                if (dArr != null) {
                    for (j in 0 until dArr.length()) dockApps.add(dArr.getString(j))
                }
                profiles.add(
                    Profile(
                        id = p.optString("id", java.util.UUID.randomUUID().toString()),
                        name = p.optString("name", "Restored Profile"),
                        iconName = p.optString("iconName", "home"),
                        accentColorHex = p.optString("accentColorHex", "#D0BCFF"),
                        dockApps = dockApps
                    )
                )
            }

            val spacesArray = root.optJSONArray("spaces") ?: JSONArray()
            val spaces = mutableListOf<SpaceConfig>()
            for (i in 0 until spacesArray.length()) {
                val s = spacesArray.getJSONObject(i)
                spaces.add(
                    SpaceConfig(
                        id = s.optString("id", java.util.UUID.randomUUID().toString()),
                        title = s.optString("title", "Restored Space"),
                        subtitle = s.optString("subtitle", ""),
                        description = s.optString("description", "")
                    )
                )
            }

            LauncherBackup(
                version = root.optString("version", "0.1"),
                exportTimestamp = root.optLong("timestamp", System.currentTimeMillis()),
                profiles = profiles,
                spaces = spaces
            )
        } catch (_: Exception) {
            null
        }
    }
}
