package com.example.purplelauncher.ui.screens

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.purplelauncher.core.model.*
import com.example.purplelauncher.ui.components.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    activeProfile: Profile,
    installedApps: List<AppInfo>,
    themeConfig: ThemeConfig,
    activeWidgets: List<ActiveHomeWidget>,
    activeSpaces: List<SpaceConfig>,
    onLaunchApp: (AppInfo) -> Unit,
    onOpenDrawer: () -> Unit,
    onOpenSearch: () -> Unit,
    onOpenProfileSwitcher: () -> Unit,
    onOpenWallpaperStudio: () -> Unit,
    onOpenDeveloperHub: (String?) -> Unit,
    onOpenSettings: () -> Unit,
    onUpdateThemeConfig: (ThemeConfig) -> Unit,
    onAddWidget: (WidgetType, WidgetSpan) -> Unit,
    onRemoveWidget: (String) -> Unit,
    onUpdateWidgetSpan: (String, WidgetSpan) -> Unit,
    onMoveWidget: (String, Int) -> Unit,
    onAppLongClick: (AppInfo) -> Unit,
    onCustomizeHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Interactive Sheets state
    var showControlCenter by remember { mutableStateOf(false) }
    var showCustomizerSheet by remember { mutableStateOf(false) }
    var showWidgetPickerSheet by remember { mutableStateOf(false) }
    var isEditHomeMode by remember { mutableStateOf(false) }
    var isSimulatingLock by remember { mutableStateOf(false) }

    // Multi-page setup: Page 0 = Feed, Page 1 = Main Home & Widgets
    val pagerState = rememberPagerState(initialPage = 1) { 2 }

    // Dock apps mapping
    val dockAppList = remember(activeProfile.dockApps, installedApps) {
        val appMap = installedApps.associateBy { it.packageName }
        activeProfile.dockApps.mapNotNull { appMap[it] }.take(themeConfig.dockSize)
    }

    val appMap = remember(installedApps) { installedApps.associateBy { it.packageName } }

    val homeApps = remember(activeProfile.favoriteApps, installedApps) {
        val favs = activeProfile.favoriteApps.mapNotNull { appMap[it] }
        if (favs.isNotEmpty()) favs else installedApps.filter { !it.isHidden }.take(themeConfig.gridCols * 2)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        when (themeConfig.doubleTapAction) {
                            GestureAction.LOCK_SCREEN -> {
                                scope.launch {
                                    isSimulatingLock = true
                                    delay(2000)
                                    isSimulatingLock = false
                                }
                            }
                            GestureAction.UNIVERSAL_SEARCH -> onOpenSearch()
                            GestureAction.OPEN_CONTROL_CENTER -> showControlCenter = true
                            GestureAction.APP_DRAWER -> onOpenDrawer()
                            else -> {}
                        }
                    },
                    onLongPress = {
                        isEditHomeMode = true
                    }
                )
            }
            .pointerInput(Unit) {
                detectDragGestures { _, dragAmount ->
                    if (dragAmount.y > 40) {
                        showControlCenter = true
                    } else if (dragAmount.y < -40) {
                        onOpenDrawer()
                    }
                }
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Edit Mode Floating Banner
            AnimatedVisibility(
                visible = isEditHomeMode,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF1E1B4B).copy(alpha = 0.92f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.DashboardCustomize,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Home Screen Editor",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Add Widget Button
                            FilledTonalButton(
                                onClick = { showWidgetPickerSheet = true },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Add Widget", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            // Done Button
                            Button(
                                onClick = { isEditHomeMode = false },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White.copy(alpha = 0.2f),
                                    contentColor = Color.White
                                ),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("Done", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Main Multi-Page Pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) { page ->
                when (page) {
                    0 -> {
                        // Page 0: Smart Now & News Feed (Left Swipe)
                        NowFeedView(
                            onSearchClick = onOpenSearch,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    1 -> {
                        // Page 1: Main Home Screen & Fluid Widgets Stream
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp)
                        ) {
                            item {
                                Spacer(modifier = Modifier.height(2.dp))
                                // 1. Clock Banner
                                ClockBannerView(
                                    profileName = activeProfile.name,
                                    clockStyle = themeConfig.clockStyle,
                                    onClockClick = onOpenSearch
                                )
                            }

                            item {
                                // 2. Google Search Capsule with Voice/Lens/Gemini
                                GoogleSearchCapsule(
                                    onSearchClick = onOpenSearch
                                )
                            }

                            // Quick Widget Add Bar when NOT in edit mode
                            if (!isEditHomeMode && activeWidgets.isEmpty()) {
                                item {
                                    Surface(
                                        onClick = { showWidgetPickerSheet = true },
                                        shape = RoundedCornerShape(20.dp),
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center,
                                            modifier = Modifier.padding(16.dp)
                                        ) {
                                            Icon(Icons.Default.AddCircleOutline, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Add Widgets to Home Screen",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }

                            // 3. User's Customized Active Home Widgets
                            itemsIndexed(activeWidgets, key = { _, widget -> widget.id }) { index, widget ->
                                WidgetHostContainer(
                                    widget = widget,
                                    isEditMode = isEditHomeMode,
                                    onRemoveWidget = { onRemoveWidget(widget.id) },
                                    onCycleSpan = {
                                        val nextSpan = when (widget.span) {
                                            WidgetSpan.COMPACT -> WidgetSpan.MEDIUM
                                            WidgetSpan.MEDIUM -> WidgetSpan.WIDE
                                            WidgetSpan.WIDE -> WidgetSpan.FULL
                                            WidgetSpan.FULL -> WidgetSpan.COMPACT
                                        }
                                        onUpdateWidgetSpan(widget.id, nextSpan)
                                    },
                                    onMoveUp = { onMoveWidget(widget.id, -1) },
                                    onMoveDown = { onMoveWidget(widget.id, 1) },
                                    onOpenSearch = onOpenSearch
                                )
                            }

                            // 4. Pinned Quick Apps Section
                            if (homeApps.isNotEmpty()) {
                                item {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(top = 4.dp, bottom = 8.dp)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 4.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                text = "Quick Applications",
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White.copy(alpha = 0.7f)
                                            )
                                            Text(
                                                text = "All Apps >",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.primary,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.clickable { onOpenDrawer() }
                                            )
                                        }

                                        LazyVerticalGrid(
                                            columns = GridCells.Fixed(themeConfig.gridCols),
                                            verticalArrangement = Arrangement.spacedBy(8.dp),
                                            horizontalArrangement = Arrangement.SpaceAround,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .heightIn(max = 240.dp)
                                        ) {
                                            items(homeApps) { app ->
                                                AppIconView(
                                                    app = app,
                                                    iconStyle = themeConfig.iconStyle,
                                                    iconShape = themeConfig.iconShape,
                                                    iconSizeDp = themeConfig.iconSizeDp,
                                                    showLabel = themeConfig.showLabels,
                                                    onClick = { onLaunchApp(app) },
                                                    onLongClick = { onAppLongClick(app) }
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            item {
                                // Add More Widgets Floating Trigger in normal mode
                                Row(
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                ) {
                                    TextButton(
                                        onClick = { showWidgetPickerSheet = true }
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Customize & Add Widgets", fontWeight = FontWeight.Bold)
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }
                }
            }

            // Bottom Section: Glass Dock & App Drawer Handle
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                if (themeConfig.dockVisible && dockAppList.isNotEmpty()) {
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp),
                        shape = RoundedCornerShape(30.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp, horizontal = 12.dp)
                        ) {
                            dockAppList.forEach { app ->
                                AppIconView(
                                    app = app,
                                    iconStyle = themeConfig.iconStyle,
                                    iconShape = themeConfig.iconShape,
                                    iconSizeDp = (themeConfig.iconSizeDp - 2).coerceAtLeast(46),
                                    showLabel = false,
                                    onClick = { onLaunchApp(app) },
                                    onLongClick = { onAppLongClick(app) }
                                )
                            }

                            // Widget Picker & Customizer Trigger in Dock
                            IconButton(
                                onClick = { showWidgetPickerSheet = true },
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.12f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Widgets,
                                    contentDescription = "Widgets",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Quick Customizer Trigger in Dock
                            IconButton(
                                onClick = { showCustomizerSheet = true },
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.12f))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = "Customize",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                // Drawer Pill Swipe Trigger
                Box(
                    modifier = Modifier
                        .padding(bottom = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.15f))
                        .clickable { onOpenDrawer() }
                        .padding(horizontal = 32.dp, vertical = 5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(36.dp)
                            .height(4.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.9f))
                    )
                }
            }
        }

        // Widget Picker Catalog Sheet
        if (showWidgetPickerSheet) {
            WidgetPickerSheet(
                onAddWidget = { type, span ->
                    onAddWidget(type, span)
                },
                onDismiss = { showWidgetPickerSheet = false }
            )
        }

        // Control Center Quick Panel Modal
        if (showControlCenter) {
            ControlCenterSheet(
                onDismiss = { showControlCenter = false }
            )
        }

        // Real-Time Aesthetic Customizer Sheet
        if (showCustomizerSheet) {
            CustomizationPanelSheet(
                themeConfig = themeConfig,
                onUpdateTheme = onUpdateThemeConfig,
                onOpenWallpaperStudio = onOpenWallpaperStudio,
                onOpenFullSettings = onOpenSettings,
                onDismiss = { showCustomizerSheet = false }
            )
        }

        // Simulated Double-Tap Lock / Sleep Overlay
        if (isSimulatingLock) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .clickable { isSimulatingLock = false },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Screen Locked",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tap anywhere to wake",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.4f)
                    )
                }
            }
        }
    }
}
