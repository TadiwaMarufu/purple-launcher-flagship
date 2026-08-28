package com.example.purplelauncher.core.repository

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetHostView
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

const val LAUNCHER_APP_WIDGET_HOST_ID = 2048

class WidgetRepository(private val context: Context) {

    val appWidgetManager: AppWidgetManager = AppWidgetManager.getInstance(context)
    val appWidgetHost: AppWidgetHost = AppWidgetHost(context, LAUNCHER_APP_WIDGET_HOST_ID)

    fun startListening() {
        try {
            appWidgetHost.startListening()
        } catch (_: Exception) {}
    }

    fun stopListening() {
        try {
            appWidgetHost.stopListening()
        } catch (_: Exception) {}
    }

    fun allocateAppWidgetId(): Int {
        return appWidgetHost.allocateAppWidgetId()
    }

    fun deleteAppWidgetId(appWidgetId: Int) {
        try {
            appWidgetHost.deleteAppWidgetId(appWidgetId)
        } catch (_: Exception) {}
    }

    suspend fun getAvailableWidgetProviders(): List<AppWidgetProviderInfo> = withContext(Dispatchers.IO) {
        try {
            appWidgetManager.installedProviders ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun createHostView(appWidgetId: Int, providerInfo: AppWidgetProviderInfo?): AppWidgetHostView? {
        return try {
            val info = providerInfo ?: appWidgetManager.getAppWidgetInfo(appWidgetId) ?: return null
            appWidgetHost.createView(context, appWidgetId, info)
        } catch (_: Exception) {
            null
        }
    }
}
