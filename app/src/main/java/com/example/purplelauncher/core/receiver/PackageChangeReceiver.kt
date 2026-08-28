package com.example.purplelauncher.core.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.purplelauncher.PurpleLauncherApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PackageChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        val app = context.applicationContext as? PurpleLauncherApplication ?: return
        val action = intent.action ?: return
        val packageName = intent.data?.schemeSpecificPart

        CoroutineScope(Dispatchers.IO).launch {
            when (action) {
                Intent.ACTION_PACKAGE_ADDED,
                Intent.ACTION_PACKAGE_REMOVED,
                Intent.ACTION_PACKAGE_REPLACED,
                Intent.ACTION_PACKAGE_CHANGED -> {
                    app.appRepository.refreshInstalledApps()
                }
            }
        }
    }
}
