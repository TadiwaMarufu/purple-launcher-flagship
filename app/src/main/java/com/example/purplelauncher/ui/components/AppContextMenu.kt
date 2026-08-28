package com.example.purplelauncher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.purplelauncher.core.model.AppInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppContextMenu(
    app: AppInfo,
    onDismiss: () -> Unit,
    onOpen: () -> Unit,
    onAddToHome: () -> Unit,
    onToggleFavorite: () -> Unit,
    onToggleHidden: () -> Unit,
    onAppInfo: () -> Unit,
    onUninstall: () -> Unit,
    onInspectPackage: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                AppIconView(
                    app = app,
                    iconSizeDp = 48,
                    showLabel = false
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = app.displayTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = app.packageName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    if (app.appSizeFormatted.isNotBlank()) {
                        Text(
                            text = "v${app.versionName} · ${app.appSizeFormatted} · Target SDK ${app.targetSdk}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(8.dp))

            // Actions list
            MenuActionItem(
                title = "Open Application",
                subtitle = "Launch activity",
                onClick = { onDismiss(); onOpen() }
            )

            MenuActionItem(
                title = "Add to Home Screen",
                subtitle = "Place shortcut on current profile",
                onClick = { onDismiss(); onAddToHome() }
            )

            MenuActionItem(
                title = if (app.isFavorite) "Remove from Favorites" else "Add to Favorites",
                subtitle = "Pin to top of drawer & search",
                onClick = { onDismiss(); onToggleFavorite() }
            )

            MenuActionItem(
                title = if (app.isHidden) "Unhide Application" else "Hide from Drawer",
                subtitle = "Privacy & focus management",
                onClick = { onDismiss(); onToggleHidden() }
            )

            MenuActionItem(
                title = "Package Inspector",
                subtitle = "Inspect SDK, permissions, APK and signature",
                onClick = { onDismiss(); onInspectPackage() }
            )

            MenuActionItem(
                title = "System App Info",
                subtitle = "Open Android settings",
                onClick = { onDismiss(); onAppInfo() }
            )

            if (!app.isSystemApp) {
                MenuActionItem(
                    title = "Uninstall",
                    subtitle = "Remove app from device",
                    isDestructive = true,
                    onClick = { onDismiss(); onUninstall() }
                )
            }
        }
    }
}

@Composable
private fun MenuActionItem(
    title: String,
    subtitle: String,
    isDestructive: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}
