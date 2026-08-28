package com.example.purplelauncher.ui.screens

import android.content.Context
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.purplelauncher.core.model.*
import com.example.purplelauncher.ui.components.*

@Composable
fun HomeScreen(
    activeProfile: Profile,
    installedApps: List<AppInfo>,
    themeConfig: ThemeConfig,
    activeSpaces: List<SpaceConfig>,
    onLaunchApp: (AppInfo) -> Unit,
    onOpenDrawer: () -> Unit,
    onOpenSearch: () -> Unit,
    onOpenProfileSwitcher: () -> Unit,
    onOpenWallpaperStudio: () -> Unit,
    onOpenDeveloperHub: (String?) -> Unit,
    onOpenSettings: () -> Unit,
    onAppLongClick: (AppInfo) -> Unit,
    onCustomizeHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isEditMode by remember { mutableStateOf(false) }

    // Map dock apps
    val dockAppList = remember(activeProfile.dockApps, installedApps) {
        val appMap = installedApps.associateBy { it.packageName }
        activeProfile.dockApps.mapNotNull { appMap[it] }.take(themeConfig.dockSize)
    }

    // Map home items
    val appMap = remember(installedApps) { installedApps.associateBy { it.packageName } }
    val homeItems = activeProfile.homeItems

    Box(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        // Double tap action: Search or Lock
                        onOpenSearch()
                    },
                    onLongPress = {
                        isEditMode = !isEditMode
                    }
                )
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Section: Clock & Smart Context Bar
            Column(modifier = Modifier.fillMaxWidth()) {
                ClockBannerView(
                    profileName = activeProfile.name,
                    onClockClick = onOpenSearch
                )

                Spacer(modifier = Modifier.height(10.dp))

                SmartBarView(
                    activeProfile = activeProfile,
                    onSearchClick = onOpenSearch,
                    onProfileClick = onOpenProfileSwitcher,
                    onDevSpaceClick = { onOpenDeveloperHub(null) }
                )
            }

            // Middle Section: Dynamic Grid / Widgets / Spaces
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(vertical = 12.dp),
                verticalArrangement = Arrangement.Center
            ) {
                // Active space cards or pinned items
                val activeSpace = activeSpaces.find { activeProfile.activeSpaceIds.contains(it.id) }
                    ?: activeSpaces.firstOrNull()

                if (activeSpace != null) {
                    val pinnedApps = remember(activeSpace.pinnedApps, installedApps) {
                        activeSpace.pinnedApps.mapNotNull { appMap[it] }
                    }
                    SpaceCardView(
                        space = activeSpace,
                        pinnedApps = pinnedApps,
                        onAppClick = onLaunchApp,
                        onCardClick = { onOpenDeveloperHub(activeSpace.id) }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Favorite / Quick Apps Grid
                val homeApps = remember(activeProfile.favoriteApps, installedApps) {
                    val favs = activeProfile.favoriteApps.mapNotNull { appMap[it] }
                    if (favs.isNotEmpty()) favs else installedApps.filter { !it.isHidden }.take(themeConfig.gridCols * 2)
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(themeConfig.gridCols),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(homeApps) { app ->
                        AppIconView(
                            app = app,
                            iconShape = themeConfig.iconShape,
                            iconSizeDp = themeConfig.iconSizeDp,
                            isMonochrome = themeConfig.iconMonochrome,
                            showLabel = themeConfig.showLabels,
                            onClick = { onLaunchApp(app) },
                            onLongClick = { onAppLongClick(app) }
                        )
                    }
                }
            }

            // Bottom Section: Dock & Quick Drawer Trigger
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (themeConfig.dockVisible && dockAppList.isNotEmpty()) {
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        shape = RoundedCornerShape(28.dp)
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
                                    iconShape = themeConfig.iconShape,
                                    iconSizeDp = (themeConfig.iconSizeDp - 4).coerceAtLeast(44),
                                    isMonochrome = themeConfig.iconMonochrome,
                                    showLabel = false,
                                    onClick = { onLaunchApp(app) },
                                    onLongClick = { onAppLongClick(app) }
                                )
                            }
                        }
                    }
                }

                // Drawer Pill Swipe Trigger
                Box(
                    modifier = Modifier
                        .padding(bottom = 6.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .pointerInput(Unit) {
                            detectDragGestures { _, dragAmount ->
                                if (dragAmount.y < -20) {
                                    onOpenDrawer()
                                }
                            }
                        }
                        .padding(horizontal = 24.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(36.dp)
                            .height(4.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.8f))
                    )
                }
            }
        }

        // Customization Overlay FAB (when in edit mode)
        if (isEditMode) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f))
                    .statusBarsPadding()
                    .padding(16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter)
                ) {
                    Text(
                        text = "Customize Home",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Button(
                        onClick = { isEditMode = false },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Done")
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 40.dp)
                ) {
                    Button(onClick = onOpenWallpaperStudio) {
                        Text("Wallpaper Studio")
                    }
                    Button(onClick = onOpenSettings) {
                        Text("Settings")
                    }
                }
            }
        }
    }
}
