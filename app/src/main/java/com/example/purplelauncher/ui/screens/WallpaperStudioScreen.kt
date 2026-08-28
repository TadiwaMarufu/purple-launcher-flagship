package com.example.purplelauncher.ui.screens

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.purplelauncher.core.model.WallpaperConfig
import com.example.purplelauncher.core.model.WallpaperPreset
import com.example.purplelauncher.core.repository.WallpaperAnalysis
import com.example.purplelauncher.core.repository.WallpaperRepository
import com.example.purplelauncher.ui.components.GlassCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WallpaperStudioScreen(
    currentConfig: WallpaperConfig,
    wallpaperRepository: WallpaperRepository,
    onSaveConfig: (WallpaperConfig, Bitmap) -> Unit,
    onCloseStudio: () -> Unit,
    modifier: Modifier = Modifier
) {
    var config by remember { mutableStateOf(currentConfig) }
    var previewBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var analysis by remember { mutableStateOf(WallpaperAnalysis()) }
    var isProcessing by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    val galleryPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            config = config.copy(sourceUri = uri.toString())
        }
    }

    LaunchedEffect(config) {
        isProcessing = true
        withContext(Dispatchers.IO) {
            val (bmp, ana) = wallpaperRepository.processWallpaperBitmap(
                sourceUriString = config.sourceUri,
                config = config,
                targetWidth = 540,
                targetHeight = 960
            )
            previewBitmap = bmp
            analysis = ana
            isProcessing = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Top Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            TextButton(onClick = onCloseStudio) {
                Text("Cancel")
            }
            Text(
                text = "Wallpaper Studio",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Button(
                onClick = {
                    previewBitmap?.let { bmp ->
                        onSaveConfig(config, bmp)
                        onCloseStudio()
                    }
                },
                enabled = previewBitmap != null
            ) {
                Text("Apply")
            }
        }

        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp)
        ) {
            // Live Preview Thumbnail
            Box(
                modifier = Modifier
                    .weight(0.45f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.Black)
            ) {
                if (previewBitmap != null) {
                    Image(
                        bitmap = previewBitmap!!.asImageBitmap(),
                        contentDescription = "Live Preview",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                if (isProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(32.dp)
                            .align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // Luminance / Mode Pill
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.7f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (analysis.isDark) "Dark Tone (${(analysis.averageLuminance * 100).toInt()}%)" else "Light Tone (${(analysis.averageLuminance * 100).toInt()}%)",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Sliders & Controls Column
            Column(
                modifier = Modifier
                    .weight(0.55f)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
            ) {
                Button(
                    onClick = { galleryPicker.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Select Image")
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Presets",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Presets horizontal row
                val presets = remember { WallpaperPreset.values().toList() }
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(presets) { preset ->
                        val isSelected = config.preset == preset
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .clickable {
                                    config = wallpaperRepository.applyPreset(preset, config)
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = preset.displayName,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Control Sliders
                SliderControl(
                    label = "Grayscale",
                    value = config.grayscaleIntensity,
                    onValueChange = { config = config.copy(grayscaleIntensity = it) },
                    valueRange = 0f..1f
                )

                SliderControl(
                    label = "Contrast",
                    value = config.contrast,
                    onValueChange = { config = config.copy(contrast = it) },
                    valueRange = 0.5f..2.5f
                )

                SliderControl(
                    label = "Brightness",
                    value = config.brightness,
                    onValueChange = { config = config.copy(brightness = it) },
                    valueRange = -0.5f..0.5f
                )

                SliderControl(
                    label = "Blur",
                    value = config.blurRadius,
                    onValueChange = { config = config.copy(blurRadius = it) },
                    valueRange = 0f..25f
                )

                SliderControl(
                    label = "Film Grain",
                    value = config.grainAmount,
                    onValueChange = { config = config.copy(grainAmount = it) },
                    valueRange = 0f..0.25f
                )

                SliderControl(
                    label = "Vignette",
                    value = config.vignetteAmount,
                    onValueChange = { config = config.copy(vignetteAmount = it) },
                    valueRange = 0f..0.8f
                )

                SliderControl(
                    label = "Darkening",
                    value = config.darkening,
                    onValueChange = { config = config.copy(darkening = it) },
                    valueRange = 0f..0.85f
                )
            }
        }
    }
}

@Composable
private fun SliderControl(
    label: String,
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )
            Text(
                text = String.format("%.2f", value),
                style = MaterialTheme.typography.labelSmall,
                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colorScheme.primary,
                activeTrackColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}
