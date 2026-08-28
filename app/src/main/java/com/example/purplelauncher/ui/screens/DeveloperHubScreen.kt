package com.example.purplelauncher.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.purplelauncher.core.model.AppInfo
import com.example.purplelauncher.core.model.SpaceConfig
import com.example.purplelauncher.core.model.SpaceTask
import com.example.purplelauncher.core.repository.DetailedPackageInfo
import com.example.purplelauncher.core.repository.DeveloperRepository
import com.example.purplelauncher.core.repository.SystemDeviceInfo
import com.example.purplelauncher.ui.components.GlassCard
import com.example.purplelauncher.ui.components.SpaceCardView

enum class DevHubTab(val title: String) {
    SPACE("Space"),
    INSPECTOR("Inspector"),
    TOOLS("Tools"),
    SYSTEM("System")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeveloperHubScreen(
    developerRepository: DeveloperRepository,
    installedApps: List<AppInfo>,
    activeSpaces: List<SpaceConfig>,
    initialSpaceId: String? = null,
    initialPackageName: String? = null,
    onLaunchApp: (AppInfo) -> Unit,
    onCloseHub: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember {
        mutableStateOf(if (initialPackageName != null) DevHubTab.INSPECTOR else DevHubTab.SPACE)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
                    text = "Developer Hub",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Engineering workspace & system telemetry",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            IconButton(onClick = onCloseHub) {
                Text(
                    "✕",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Tabs Row
        TabRow(
            selectedTabIndex = selectedTab.ordinal,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            divider = { Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)) }
        ) {
            DevHubTab.values().forEach { tab ->
                Tab(
                    selected = selectedTab == tab,
                    onClick = { selectedTab = tab },
                    text = {
                        Text(
                            text = tab.title,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedTab) {
            DevHubTab.SPACE -> {
                DevSpaceContent(
                    spaces = activeSpaces,
                    installedApps = installedApps,
                    onLaunchApp = onLaunchApp
                )
            }
            DevHubTab.INSPECTOR -> {
                PackageInspectorContent(
                    developerRepository = developerRepository,
                    installedApps = installedApps,
                    initialPackage = initialPackageName
                )
            }
            DevHubTab.TOOLS -> {
                DevToolsContent(developerRepository = developerRepository)
            }
            DevHubTab.SYSTEM -> {
                SystemInfoContent(developerRepository = developerRepository)
            }
        }
    }
}

@Composable
private fun DevSpaceContent(
    spaces: List<SpaceConfig>,
    installedApps: List<AppInfo>,
    onLaunchApp: (AppInfo) -> Unit
) {
    val appMap = remember(installedApps) { installedApps.associateBy { it.packageName } }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(spaces, key = { it.id }) { space ->
            val pinnedApps = remember(space.pinnedApps, installedApps) {
                space.pinnedApps.mapNotNull { appMap[it] }
            }
            SpaceCardView(
                space = space,
                pinnedApps = pinnedApps,
                onAppClick = onLaunchApp
            )
        }
    }
}

@Composable
private fun PackageInspectorContent(
    developerRepository: DeveloperRepository,
    installedApps: List<AppInfo>,
    initialPackage: String?
) {
    var selectedPackage by remember {
        mutableStateOf(initialPackage ?: installedApps.firstOrNull()?.packageName ?: "")
    }
    var pkgInfo by remember(selectedPackage) {
        mutableStateOf(developerRepository.getDetailedPackageInfo(selectedPackage))
    }
    var appFilter by remember { mutableStateOf("") }

    val filteredApps = remember(installedApps, appFilter) {
        if (appFilter.isBlank()) installedApps.take(15)
        else installedApps.filter { it.displayTitle.contains(appFilter, ignoreCase = true) || it.packageName.contains(appFilter, ignoreCase = true) }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // App selector dropdown / search
        OutlinedTextField(
            value = appFilter,
            onValueChange = { appFilter = it },
            label = { Text("Filter package to inspect...") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Horizontal pill selector for quick apps
        ScrollableTabRow(
            selectedTabIndex = filteredApps.indexOfFirst { it.packageName == selectedPackage }.coerceAtLeast(0),
            edgePadding = 0.dp,
            containerColor = Color.Transparent
        ) {
            filteredApps.forEach { app ->
                Tab(
                    selected = app.packageName == selectedPackage,
                    onClick = { selectedPackage = app.packageName },
                    text = { Text(app.displayTitle, fontSize = 12.sp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (pkgInfo != null) {
            val info = pkgInfo!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = info.appName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = info.packageName,
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        DetailRow("Version Name", info.versionName)
                        DetailRow("Version Code", info.versionCode.toString())
                        DetailRow("Target SDK", info.targetSdk.toString())
                        DetailRow("Min SDK", info.minSdk.toString())
                        DetailRow("APK Size", String.format("%.2f MB", info.apkSizeMb))
                        DetailRow("Installed", info.installDate)
                        DetailRow("Updated", info.updateDate)
                        DetailRow("System App", if (info.isSystemApp) "Yes" else "No")
                        DetailRow("Activities", info.activitiesCount.toString())
                        DetailRow("Services", info.servicesCount.toString())
                        DetailRow("Receivers", info.receiversCount.toString())
                    }
                }

                // Signature Card
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Certificate SHA-256",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = info.signatureSha256,
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                    }
                }

                // Declared Permissions
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Declared Permissions (${info.permissions.size})",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        info.permissions.forEach { perm ->
                            Text(
                                text = "• ${perm.removePrefix("android.permission.")}",
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                modifier = Modifier.padding(vertical = 1.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DevToolsContent(developerRepository: DeveloperRepository) {
    var toolMode by remember { mutableStateOf("JSON") }
    var inputText by remember { mutableStateOf("") }
    var outputText by remember { mutableStateOf("") }
    var regexPattern by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Tool switcher row
        val tools = listOf("JSON", "Base64", "SHA-256", "MD5", "Regex")
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            tools.forEach { t ->
                val isSelected = toolMode == t
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .clickable {
                            toolMode = t
                            outputText = ""
                        }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = t,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        if (toolMode == "Regex") {
            OutlinedTextField(
                value = regexPattern,
                onValueChange = { regexPattern = it },
                label = { Text("Regex Pattern (e.g. \\b[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}\\b)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            label = { Text("Input Text / Payload") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        )

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Button(
                onClick = {
                    when (toolMode) {
                        "JSON" -> {
                            val (success, res) = developerRepository.formatJson(inputText)
                            outputText = res
                        }
                        "Base64" -> {
                            outputText = developerRepository.encodeBase64(inputText)
                        }
                        "SHA-256" -> {
                            outputText = developerRepository.computeHash(inputText, "SHA-256")
                        }
                        "MD5" -> {
                            outputText = developerRepository.computeHash(inputText, "MD5")
                        }
                        "Regex" -> {
                            val (_, res) = developerRepository.testRegex(regexPattern, inputText)
                            outputText = res
                        }
                    }
                }
            ) {
                Text(if (toolMode == "Base64") "Encode" else "Execute")
            }

            if (toolMode == "Base64") {
                FilledTonalButton(
                    onClick = {
                        val (_, res) = developerRepository.decodeBase64(inputText)
                        outputText = res
                    }
                ) {
                    Text("Decode")
                }
            }

            if (outputText.isNotBlank()) {
                OutlinedButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Dev Output", outputText))
                    }
                ) {
                    Text("Copy")
                }
            }
        }

        if (outputText.isNotBlank()) {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Result Output",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = outputText,
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun SystemInfoContent(developerRepository: DeveloperRepository) {
    val info = remember { developerRepository.getSystemDeviceInfo() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Device & Hardware Telemetry",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(10.dp))

                DetailRow("Device Model", info.deviceModel)
                DetailRow("Manufacturer", info.manufacturer)
                DetailRow("Android Version", "${info.androidVersion} (API ${info.apiLevel})")
                DetailRow("Build ID", info.buildId)
                DetailRow("Architecture", info.supportedAbis)
                DetailRow("Total RAM", "${info.totalRamMb} MB")
                DetailRow("Available RAM", "${info.availableRamMb} MB")
                DetailRow("Display Resolution", info.screenResolution)
                DetailRow("Screen Density", "${info.screenDensityDpi} DPI")
                DetailRow("Battery Level", "${info.batteryPercentage}% (${if (info.isCharging) "Charging" else "Discharging"})")
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Monospace,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
