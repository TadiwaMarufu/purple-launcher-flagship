package com.example.purplelauncher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.purplelauncher.core.model.*

@Composable
fun CustomizationPanelSheet(
    themeConfig: ThemeConfig,
    onUpdateTheme: (ThemeConfig) -> Unit,
    onOpenWallpaperStudio: () -> Unit,
    onOpenFullSettings: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedPreset by remember { mutableStateOf(themeConfig.activePreset) }
    var selectedIconStyle by remember { mutableStateOf(themeConfig.iconStyle) }
    var selectedIconShape by remember { mutableStateOf(themeConfig.iconShape) }
    var selectedClockStyle by remember { mutableStateOf(themeConfig.clockStyle) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .clickable { onDismiss() },
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.75f)
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .background(Color(0xFF14151C))
                .padding(20.dp)
                .clickable(enabled = false) {}
                .verticalScroll(rememberScrollState())
        ) {
            // Header Handle
            Box(
                modifier = Modifier
                    .width(44.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.3f))
                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Aesthetic Customizer",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            Text(
                text = "Switch presets, icon styles, widgets and layouts with live preview",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(18.dp))

            // 1. Theme Presets Horizontal Selector
            Text(
                text = "THEME PRESETS (INSPIRED BY TOP UIs)",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(ThemePreset.values()) { preset ->
                    val isSelected = preset == selectedPreset
                    Surface(
                        onClick = {
                            selectedPreset = preset
                            // Apply presets matching the user's screenshots
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
                            selectedIconStyle = newConfig.iconStyle
                            selectedIconShape = newConfig.iconShape
                            selectedClockStyle = newConfig.clockStyle
                            onUpdateTheme(newConfig)
                        },
                        shape = RoundedCornerShape(18.dp),
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

            Spacer(modifier = Modifier.height(18.dp))

            // 2. Icon Style Selector
            Text(
                text = "ICON STYLING",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(IconStyle.values()) { style ->
                    val isSelected = style == selectedIconStyle
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedIconStyle = style
                            onUpdateTheme(themeConfig.copy(iconStyle = style))
                        },
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

            Spacer(modifier = Modifier.height(14.dp))

            // 3. Icon Shapes Selector
            Text(
                text = "ICON SHAPE",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(IconShape.values()) { shape ->
                    val isSelected = shape == selectedIconShape
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedIconShape = shape
                            onUpdateTheme(themeConfig.copy(iconShape = shape))
                        },
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

            Spacer(modifier = Modifier.height(14.dp))

            // 4. Clock Styles
            Text(
                text = "CLOCK BANNER STYLE",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(ClockStyle.values()) { clock ->
                    val isSelected = clock == selectedClockStyle
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedClockStyle = clock
                            onUpdateTheme(themeConfig.copy(clockStyle = clock))
                        },
                        label = { Text(clock.displayName) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            containerColor = Color.White.copy(alpha = 0.08f),
                            labelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = {
                        onDismiss()
                        onOpenWallpaperStudio()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.15f))
                ) {
                    Text("Wallpaper Studio")
                }

                Button(
                    onClick = {
                        onDismiss()
                        onOpenFullSettings()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("All Settings")
                }
            }
        }
    }
}
