package com.example.purplelauncher.core.database

import androidx.room.TypeConverter
import com.example.purplelauncher.core.model.*
import org.json.JSONArray
import org.json.JSONObject

class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        if (value == null) return "[]"
        val array = JSONArray()
        value.forEach { array.put(it) }
        return array.toString()
    }

    @TypeConverter
    fun toStringList(value: String?): List<String> {
        if (value.isNullOrBlank()) return emptyList()
        val list = mutableListOf<String>()
        try {
            val array = JSONArray(value)
            for (i in 0 until array.length()) {
                list.add(array.getString(i))
            }
        } catch (_: Exception) {}
        return list
    }

    @TypeConverter
    fun fromHomeItemList(items: List<HomeItem>?): String {
        if (items == null) return "[]"
        val array = JSONArray()
        for (item in items) {
            val obj = JSONObject().apply {
                put("id", item.id)
                put("type", item.type.name)
                put("pageIndex", item.pageIndex)
                put("gridX", item.gridX)
                put("gridY", item.gridY)
                put("spanX", item.spanX)
                put("spanY", item.spanY)
                put("packageName", item.packageName ?: "")
                put("activityName", item.activityName ?: "")
                put("widgetId", item.widgetId ?: -1)
                put("widgetProvider", item.widgetProvider ?: "")
                put("folderId", item.folderId ?: "")
                put("stackId", item.stackId ?: "")
                put("spaceId", item.spaceId ?: "")
                put("customTitle", item.customTitle ?: "")
            }
            array.put(obj)
        }
        return array.toString()
    }

    @TypeConverter
    fun toHomeItemList(json: String?): List<HomeItem> {
        if (json.isNullOrBlank()) return emptyList()
        val list = mutableListOf<HomeItem>()
        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val typeName = obj.optString("type", HomeItemType.APP.name)
                val type = try { HomeItemType.valueOf(typeName) } catch (_: Exception) { HomeItemType.APP }
                list.add(
                    HomeItem(
                        id = obj.optString("id", java.util.UUID.randomUUID().toString()),
                        type = type,
                        pageIndex = obj.optInt("pageIndex", 0),
                        gridX = obj.optInt("gridX", 0),
                        gridY = obj.optInt("gridY", 0),
                        spanX = obj.optInt("spanX", 1),
                        spanY = obj.optInt("spanY", 1),
                        packageName = obj.optString("packageName").takeIf { it.isNotBlank() },
                        activityName = obj.optString("activityName").takeIf { it.isNotBlank() },
                        widgetId = obj.optInt("widgetId", -1).takeIf { it != -1 },
                        widgetProvider = obj.optString("widgetProvider").takeIf { it.isNotBlank() },
                        folderId = obj.optString("folderId").takeIf { it.isNotBlank() },
                        stackId = obj.optString("stackId").takeIf { it.isNotBlank() },
                        spaceId = obj.optString("spaceId").takeIf { it.isNotBlank() },
                        customTitle = obj.optString("customTitle").takeIf { it.isNotBlank() }
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    @TypeConverter
    fun fromWallpaperConfig(config: WallpaperConfig?): String {
        val c = config ?: WallpaperConfig()
        return JSONObject().apply {
            put("sourceUri", c.sourceUri ?: "")
            put("preset", c.preset.name)
            put("brightness", c.brightness.toDouble())
            put("contrast", c.contrast.toDouble())
            put("grayscaleIntensity", c.grayscaleIntensity.toDouble())
            put("blurRadius", c.blurRadius.toDouble())
            put("grainAmount", c.grainAmount.toDouble())
            put("vignetteAmount", c.vignetteAmount.toDouble())
            put("darkening", c.darkening.toDouble())
            put("zoom", c.zoom.toDouble())
            put("offsetX", c.offsetX.toDouble())
            put("offsetY", c.offsetY.toDouble())
            put("isParallaxEnabled", c.isParallaxEnabled)
        }.toString()
    }

    @TypeConverter
    fun toWallpaperConfig(json: String?): WallpaperConfig {
        if (json.isNullOrBlank()) return WallpaperConfig()
        return try {
            val obj = JSONObject(json)
            val presetStr = obj.optString("preset", WallpaperPreset.PURE.name)
            val preset = try { WallpaperPreset.valueOf(presetStr) } catch (_: Exception) { WallpaperPreset.PURE }
            WallpaperConfig(
                sourceUri = obj.optString("sourceUri").takeIf { it.isNotBlank() },
                preset = preset,
                brightness = obj.optDouble("brightness", 0.0).toFloat(),
                contrast = obj.optDouble("contrast", 1.0).toFloat(),
                grayscaleIntensity = obj.optDouble("grayscaleIntensity", 1.0).toFloat(),
                blurRadius = obj.optDouble("blurRadius", 0.0).toFloat(),
                grainAmount = obj.optDouble("grainAmount", 0.04).toFloat(),
                vignetteAmount = obj.optDouble("vignetteAmount", 0.12).toFloat(),
                darkening = obj.optDouble("darkening", 0.35).toFloat(),
                zoom = obj.optDouble("zoom", 1.0).toFloat(),
                offsetX = obj.optDouble("offsetX", 0.0).toFloat(),
                offsetY = obj.optDouble("offsetY", 0.0).toFloat(),
                isParallaxEnabled = obj.optBoolean("isParallaxEnabled", true)
            )
        } catch (_: Exception) {
            WallpaperConfig()
        }
    }

    @TypeConverter
    fun fromSpaceTaskList(tasks: List<SpaceTask>?): String {
        if (tasks == null) return "[]"
        val array = JSONArray()
        for (task in tasks) {
            array.put(JSONObject().apply {
                put("id", task.id)
                put("title", task.title)
                put("isCompleted", task.isCompleted)
                put("timestamp", task.timestamp)
            })
        }
        return array.toString()
    }

    @TypeConverter
    fun toSpaceTaskList(json: String?): List<SpaceTask> {
        if (json.isNullOrBlank()) return emptyList()
        val list = mutableListOf<SpaceTask>()
        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    SpaceTask(
                        id = obj.optString("id"),
                        title = obj.optString("title"),
                        isCompleted = obj.optBoolean("isCompleted"),
                        timestamp = obj.optLong("timestamp")
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    @TypeConverter
    fun fromWidgetEntries(entries: List<WidgetStackEntry>?): String {
        if (entries == null) return "[]"
        val array = JSONArray()
        for (e in entries) {
            array.put(JSONObject().apply {
                put("id", e.id)
                put("title", e.title)
                put("type", e.type)
                put("appWidgetId", e.appWidgetId ?: -1)
            })
        }
        return array.toString()
    }

    @TypeConverter
    fun toWidgetEntries(json: String?): List<WidgetStackEntry> {
        if (json.isNullOrBlank()) return emptyList()
        val list = mutableListOf<WidgetStackEntry>()
        try {
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    WidgetStackEntry(
                        id = obj.optString("id"),
                        title = obj.optString("title"),
                        type = obj.optString("type"),
                        appWidgetId = obj.optInt("appWidgetId", -1).takeIf { it != -1 }
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }
}
