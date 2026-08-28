package com.example.purplelauncher.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.purplelauncher.core.model.AppCategory
import com.example.purplelauncher.core.model.AppInfo
import com.example.purplelauncher.core.model.ThemeConfig
import com.example.purplelauncher.ui.components.AppIconView
import com.example.purplelauncher.ui.components.GlassCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDrawerScreen(
    installedApps: List<AppInfo>,
    themeConfig: ThemeConfig,
    onLaunchApp: (AppInfo) -> Unit,
    onAppLongClick: (AppInfo) -> Unit,
    onCloseDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(AppCategory.ALL) }
    val focusManager = LocalFocusManager.current

    val visibleApps = remember(installedApps, searchQuery, selectedCategory) {
        installedApps.filter { app ->
            if (app.isHidden) return@filter false

            val matchesSearch = if (searchQuery.isBlank()) true else {
                app.displayTitle.contains(searchQuery, ignoreCase = true) ||
                        app.packageName.contains(searchQuery, ignoreCase = true)
            }

            val matchesCategory = when (selectedCategory) {
                AppCategory.ALL -> true
                AppCategory.FAVORITES -> app.isFavorite
                else -> app.category == selectedCategory
            }

            matchesSearch && matchesCategory
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF090A0F).copy(alpha = 0.97f))
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        // Drawer Header / Search Input
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        "Search all apps & tools...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        focusManager.clearFocus()
                        visibleApps.firstOrNull()?.let { onLaunchApp(it) }
                    }
                ),
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White.copy(alpha = 0.12f),
                    unfocusedContainerColor = Color.White.copy(alpha = 0.08f),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            TextButton(onClick = onCloseDrawer) {
                Text(
                    "Close",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Horizontal Categories Filter
        val categories = remember { AppCategory.values().toList() }
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(categories) { category ->
                val isSelected = selectedCategory == category
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary
                            else Color.White.copy(alpha = 0.1f)
                        )
                        .clickable { selectedCategory = category }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = category.displayName,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Apps Grid
        if (visibleApps.isEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Text(
                    text = "No applications found",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.5f)
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(themeConfig.gridCols),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(visibleApps, key = { it.packageName }) { app ->
                    AppIconView(
                        app = app,
                        iconStyle = themeConfig.iconStyle,
                        iconShape = themeConfig.iconShape,
                        iconSizeDp = themeConfig.iconSizeDp,
                        showLabel = themeConfig.showLabels,
                        showFavoriteBadge = true,
                        onClick = { onLaunchApp(app) },
                        onLongClick = { onAppLongClick(app) }
                    )
                }
            }
        }
    }
}
