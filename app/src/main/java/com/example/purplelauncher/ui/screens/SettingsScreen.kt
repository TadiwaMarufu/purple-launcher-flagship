package com.example.purplelauncher.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.purplelauncher.core.model.*
import com.example.purplelauncher.core.repository.IconPackManager
import com.example.purplelauncher.core.repository.InstalledIconPack
import com.example.purplelauncher.core.repository.SettingsRepository
import com.example.purplelauncher.ui.components.GlassCard
import com.example.purplelauncher.ui.theme.parseColorHex
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    themeConfig: ThemeConfig,
    gestureConfig: GestureConfig,
    settingsRepository: SettingsRepository,
    profiles: List<Profile>,
    spaces: List<SpaceConfig>,
    onUpdateTheme: (ThemeConfig) -> Unit,
    onUpdateGestures: (GestureConfig) -> Unit,
    onCloseSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val iconPackManager = remember { IconPackManager(context) }
    var installedPacks by remember { mutableStateOf<List<InstalledIconPack>>(emptyList()) }

    var showImportDialog by remember { mutableStateOf(false) }
    var importJsonText by remember { mutableStateOf("") }
    var importStatusMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        installedPacks = iconPackManager.getInstalledIconPacks()
    }

    val accentColors = listOf(
        "#38BDF8", "#2DD4BF", "#22C55E", "#F59E0B",
        "#EF4444", "#F43F5E", "#EC4899", "#A855F7",
        "#818CF8", "#C084FC", "#E2E8F0", "#18181B"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090A0F))
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            Column {
                Text(
                    text = "Launcher Customization",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Presets, icon engines, gestures & widgets",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }

            IconButton(onClick = onCloseSettings) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White
                )
            }
        }

        Divider(color = Color.White.copy(alpha = 0.15f))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            // Default Home Helper
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        try {
                            val intent = Intent(Settings.ACTION_HOME_SETTINGS).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            context.startActivity(intent)
                        } catch (_: Exception) {
                            try {
                                val intent = Intent(Settings.ACTION_SETTINGS).apply {
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                context.startActivity(intent)
                            } catch (_: Exception) {}
                        }
                    },
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Set as Default Home Launcher",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Tap to open system settings and select this launcher",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // 1. One-Click Complete Theme Presets
            SectionHeader("Theme Presets (Inspired by Top UIs)")

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(ThemePreset.values()) { preset ->
                    val isSelected = preset == themeConfig.activePreset
                    Surface(
                        onClick = {
                            val newConfig = when (preset) {
                                ThemePreset.NEO_OBSIDIAN -> themeConfig.copy(
                                    activePreset = preset,
                                    primaryAccentHex = "#38BDF8",
                                    iconStyle = IconStyle.GLOSSY_SQUIRCLE,
                                    iconShape = IconShape.SQUIRCLE,
                                    clockStyle = ClockStyle.TALL_CONDENSED
                                )
                                ThemePreset.ONEUI_FROSTED_GLASS -> themeConfig.copy(
                                    activePreset = preset,
                                    primaryAccentHex = "#2DD4BF",
                                    iconStyle = IconStyle.ORIGINAL_VIBRANT,
                                    iconShape = IconShape.ROUNDED,
                                    clockStyle = ClockStyle.TALL_CONDENSED
                                )
                                ThemePreset.NOTHING_DOT_MATRIX -> themeConfig.copy(
                                    activePreset = preset,
                                    primaryAccentHex = "#EF4444",
                                    iconStyle = IconStyle.NOTHING_DOT_GLYPH,
                                    iconShape = IconShape.CIRCLE,
                                    clockStyle = ClockStyle.NOTHING_DOT_MATRIX
                                )
                                ThemePreset.EMERALD_FOREST -> themeConfig.copy(
                                    activePreset = preset,
                                    primaryAccentHex = "#22C55E",
                                    iconStyle = IconStyle.THEMED_TINTED,
                                    iconShape = IconShape.SQUIRCLE,
                                    clockStyle = ClockStyle.SPLIT_CAPSULE
                                )
                                ThemePreset.PARISIAN_EDITORIAL -> themeConfig.copy(
                                    activePreset = preset,
                                    primaryAccentHex = "#F43F5E",
                                    iconStyle = IconStyle.EDITORIAL_OUTLINE,
                                    iconShape = IconShape.CIRCLE,
                                    clockStyle = ClockStyle.EDITORIAL_STACK
                                )
                                ThemePreset.WIDGETSMITH_AESTHETIC -> themeConfig.copy(
                                    activePreset = preset,
                                    primaryAccentHex = "#A855F7",
                                    iconStyle = IconStyle.MONOCHROME_DARK,
                                    iconShape = IconShape.SQUIRCLE,
                                    clockStyle = ClockStyle.MINIMAL_SERIF
                                )
                                ThemePreset.CYBER_VIBRANT -> themeConfig.copy(
                                    activePreset = preset,
                                    primaryAccentHex = "#C084FC",
                                    iconStyle = IconStyle.NEON_GLOW,
                                    iconShape = IconShape.HEXAGON,
                                    clockStyle = ClockStyle.TALL_CONDENSED
                                )
                                ThemePreset.MATERIAL_YOU_DYNAMIC -> themeConfig.copy(
                                    activePreset = preset,
                                    primaryAccentHex = "#60A5FA",
                                    iconStyle = IconStyle.THEMED_TINTED,
                                    iconShape = IconShape.PEBBLE,
                                    clockStyle = ClockStyle.DIGITAL_PILL
                                )
                            }
                            onUpdateTheme(newConfig)
                        },
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.08f),
                        border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null,
                        modifier = Modifier.width(160.dp).height(80.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = preset.displayName,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = preset.subtitle,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                color = Color.White.copy(alpha = 0.7f),
                                maxLines = 2
                            )
                        }
                    }
                }
            }

            // 2. Accent Color Palette
            SectionHeader("Accent Tone")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(accentColors) { hex ->
                    val color = parseColorHex(hex, Color.Cyan)
                    val isSelected = themeConfig.primaryAccentHex.equals(hex, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) Color.White else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable {
                                onUpdateTheme(themeConfig.copy(primaryAccentHex = hex))
                            }
                    )
                }
            }

            // 3. Icon Styles & Shapes
            SectionHeader("Icon Customization & Icon Packs")

            Text(
                text = "Icon Render Engine",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.8f)
            )

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(IconStyle.values()) { style ->
                    val isSelected = themeConfig.iconStyle == style
                    FilterChip(
                        selected = isSelected,
                        onClick = { onUpdateTheme(themeConfig.copy(iconStyle = style)) },
                        label = { Text(style.displayName) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            containerColor = Color.White.copy(alpha = 0.08f),
                            labelColor = Color.White
                        )
                    )
                }
            }

            Text(
                text = "Icon Shape Mask",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.8f)
            )

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(IconShape.values()) { shape ->
                    val isSelected = themeConfig.iconShape == shape
                    FilterChip(
                        selected = isSelected,
                        onClick = { onUpdateTheme(themeConfig.copy(iconShape = shape)) },
                        label = { Text(shape.displayName) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            containerColor = Color.White.copy(alpha = 0.08f),
                            labelColor = Color.White
                        )
                    )
                }
            }

            // Third-Party Icon Packs Support
            if (installedPacks.isNotEmpty()) {
                Text(
                    text = "Installed Third-Party Icon Packs",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(installedPacks) { pack ->
                        val isSelected = themeConfig.selectedIconPackPackage == pack.packageName
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                onUpdateTheme(
                                    themeConfig.copy(
                                        selectedIconPackPackage = if (isSelected) null else pack.packageName
                                    )
                                )
                            },
                            label = { Text(pack.name) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                                containerColor = Color.White.copy(alpha = 0.08f),
                                labelColor = Color.White
                            )
                        )
                    }
                }
            }

            SettingToggleRow(
                title = "Show App Labels",
                subtitle = "Display application titles under icons",
                checked = themeConfig.showLabels,
                onCheckedChange = { onUpdateTheme(themeConfig.copy(showLabels = it)) }
            )

            // 4. Gestures Configuration
            SectionHeader("Gestures & Touch Controls")

            SettingToggleRow(
                title = "Double Tap on Empty Space",
                subtitle = "Instantly sleep/lock the device display",
                checked = themeConfig.doubleTapAction == GestureAction.LOCK_SCREEN,
                onCheckedChange = { isChecked ->
                    onUpdateTheme(
                        themeConfig.copy(
                            doubleTapAction = if (isChecked) GestureAction.LOCK_SCREEN else GestureAction.NONE
                        )
                    )
                }
            )

            SettingToggleRow(
                title = "Swipe Down for Control Center",
                subtitle = "Pull down quick settings panel with volume & brightness sliders",
                checked = themeConfig.swipeDownAction == GestureAction.OPEN_CONTROL_CENTER,
                onCheckedChange = { isChecked ->
                    onUpdateTheme(
                        themeConfig.copy(
                            swipeDownAction = if (isChecked) GestureAction.OPEN_CONTROL_CENTER else GestureAction.NONE
                        )
                    )
                }
            )

            SettingToggleRow(
                title = "Swipe Left for News & Now Feed",
                subtitle = "Morning brief, breaking stories & daily glance",
                checked = themeConfig.feedEnabled,
                onCheckedChange = { onUpdateTheme(themeConfig.copy(feedEnabled = it)) }
            )

            SettingToggleRow(
                title = "Now Playing Music Bar",
                subtitle = "Interactive track player pill on home screen",
                checked = themeConfig.nowPlayingBarEnabled,
                onCheckedChange = { onUpdateTheme(themeConfig.copy(nowPlayingBarEnabled = it)) }
            )

            SettingToggleRow(
                title = "Persistent Bottom Dock",
                subtitle = "Display glass dock container on home screen",
                checked = themeConfig.dockVisible,
                onCheckedChange = { onUpdateTheme(themeConfig.copy(dockVisible = it)) }
            )

            // 5. Backup & Restore
            SectionHeader("Backup & Configuration")

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = {
                        val json = settingsRepository.generateBackupJson(profiles, spaces, themeConfig, gestureConfig)
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Purple Launcher Backup", json))
                        importStatusMessage = "Backup JSON copied to clipboard!"
                    }
                ) {
                    Text("Export Backup")
                }

                FilledTonalButton(onClick = { showImportDialog = true }) {
                    Text("Import Backup")
                }
            }

            if (importStatusMessage != null) {
                Text(
                    text = importStatusMessage!!,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Import Dialog
        if (showImportDialog) {
            AlertDialog(
                onDismissRequest = { showImportDialog = false },
                title = { Text("Import JSON Backup") },
                text = {
                    Column {
                        Text("Paste backup configuration JSON:")
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = importJsonText,
                            onValueChange = { importJsonText = it },
                            modifier = Modifier.fillMaxWidth().height(150.dp)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val parsed = settingsRepository.parseBackupJson(importJsonText)
                            if (parsed != null) {
                                importStatusMessage = "Successfully parsed backup (${parsed.profiles.size} profiles)"
                            } else {
                                importStatusMessage = "Invalid JSON format"
                            }
                            showImportDialog = false
                        }
                    ) {
                        Text("Restore")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showImportDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun SettingToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.6f)
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
    }
}
