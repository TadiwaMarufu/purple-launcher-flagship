package com.example.purplelauncher.ui.components

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.example.purplelauncher.core.model.AppInfo
import com.example.purplelauncher.core.model.IconShape
import com.example.purplelauncher.core.model.IconStyle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppIconView(
    app: AppInfo,
    modifier: Modifier = Modifier,
    iconStyle: IconStyle = IconStyle.GLOSSY_SQUIRCLE,
    iconShape: IconShape = IconShape.SQUIRCLE,
    iconSizeDp: Int = 54,
    showLabel: Boolean = true,
    showFavoriteBadge: Boolean = false,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {}
) {
    val context = LocalContext.current
    var drawableBitmap by remember(app.packageName) { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(app.packageName) {
        withContext(Dispatchers.IO) {
            try {
                val iconDrawable: Drawable = context.packageManager.getApplicationIcon(app.packageName)
                val bmp = iconDrawable.toBitmap(width = 128, height = 128)
                drawableBitmap = bmp
            } catch (_: Exception) {}
        }
    }

    val shape: Shape = when (iconShape) {
        IconShape.CIRCLE -> CircleShape
        IconShape.SQUIRCLE -> RoundedCornerShape(18.dp)
        IconShape.ROUNDED -> RoundedCornerShape(12.dp)
        IconShape.TEARDROP -> RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 4.dp, bottomEnd = 20.dp)
        IconShape.HEXAGON -> RoundedCornerShape(14.dp)
        IconShape.PEBBLE -> RoundedCornerShape(22.dp)
    }

    val colorFilter = remember(iconStyle) {
        when (iconStyle) {
            IconStyle.MONOCHROME_DARK, IconStyle.NOTHING_DOT_GLYPH, IconStyle.EDITORIAL_OUTLINE -> {
                val matrix = ColorMatrix().apply { setToSaturation(0f) }
                ColorFilter.colorMatrix(matrix)
            }
            IconStyle.THEMED_TINTED -> {
                ColorFilter.tint(Color(0xFF38BDF8))
            }
            else -> null // Vibrant original color
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .widthIn(min = 60.dp, max = 84.dp)
            .padding(vertical = 4.dp)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(iconSizeDp.dp)
        ) {
            // Container background styling
            when (iconStyle) {
                IconStyle.GLOSSY_SQUIRCLE -> {
                    // OneUI / iOS inspired 3D glossy container
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(shape)
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color(0xFF262833),
                                        Color(0xFF14151B)
                                    )
                                )
                            )
                            .border(1.dp, Color.White.copy(alpha = 0.15f), shape)
                    ) {
                        if (drawableBitmap != null) {
                            Image(
                                bitmap = drawableBitmap!!.asImageBitmap(),
                                contentDescription = app.displayTitle,
                                colorFilter = colorFilter,
                                modifier = Modifier
                                    .fillMaxSize(0.72f)
                                    .clip(shape)
                            )
                        } else {
                            FallbackLetter(app.displayTitle)
                        }
                    }
                }

                IconStyle.NEON_GLOW -> {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(shape)
                            .background(Color(0xFF0F172A))
                            .border(1.5.dp, MaterialTheme.colorScheme.primary, shape)
                    ) {
                        if (drawableBitmap != null) {
                            Image(
                                bitmap = drawableBitmap!!.asImageBitmap(),
                                contentDescription = app.displayTitle,
                                colorFilter = colorFilter,
                                modifier = Modifier.fillMaxSize(0.75f)
                            )
                        } else {
                            FallbackLetter(app.displayTitle)
                        }
                    }
                }

                IconStyle.ORIGINAL_VIBRANT -> {
                    if (drawableBitmap != null) {
                        Image(
                            bitmap = drawableBitmap!!.asImageBitmap(),
                            contentDescription = app.displayTitle,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(shape)
                        )
                    } else {
                        FallbackLetter(app.displayTitle)
                    }
                }

                else -> {
                    // Clean Frosted Glass Container
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(shape)
                            .background(Color.White.copy(alpha = 0.12f))
                            .border(0.5.dp, Color.White.copy(alpha = 0.2f), shape)
                    ) {
                        if (drawableBitmap != null) {
                            Image(
                                bitmap = drawableBitmap!!.asImageBitmap(),
                                contentDescription = app.displayTitle,
                                colorFilter = colorFilter,
                                modifier = Modifier
                                    .fillMaxSize(0.75f)
                                    .clip(shape)
                            )
                        } else {
                            FallbackLetter(app.displayTitle)
                        }
                    }
                }
            }

            // Optional Favorite badge
            if (showFavoriteBadge && app.isFavorite) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .align(Alignment.TopEnd)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
        }

        if (showLabel) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = app.displayTitle,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                color = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun FallbackLetter(title: String) {
    Text(
        text = title.take(1).uppercase(),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )
}
