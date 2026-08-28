package com.example.purplelauncher.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.purplelauncher.core.model.ActiveHomeWidget
import com.example.purplelauncher.core.model.WidgetSpan
import com.example.purplelauncher.core.model.WidgetType

@Composable
fun WidgetHostContainer(
    widget: ActiveHomeWidget,
    isEditMode: Boolean,
    onRemoveWidget: () -> Unit,
    onCycleSpan: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onOpenSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Subtle breathing pulse in edit mode
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val editPulse by infiniteTransition.animateFloat(
        initialValue = 0.99f,
        targetValue = 1.01f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "editPulse"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (isEditMode) {
                    Modifier
                        .scale(editPulse)
                        .border(
                            BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)),
                            RoundedCornerShape(26.dp)
                        )
                } else Modifier
            )
    ) {
        // Widget Content
        when (widget.type) {
            WidgetType.NOW_PLAYING -> NowPlayingWidget(span = widget.span)
            WidgetType.LIVE_WEATHER -> LiveWeatherWidget(span = widget.span)
            WidgetType.MULTI_BATTERY -> MultiDeviceBatteryWidget()
            WidgetType.DEVICE_CARE -> DeviceCareWidget()
            WidgetType.DIGITAL_WELLBEING -> DigitalWellbeingWidget()
            WidgetType.AGENDA_CALENDAR -> AgendaCalendarWidget(span = widget.span)
            WidgetType.QUICK_NOTES -> QuickNotesWidget(span = widget.span)
            WidgetType.PHOTO_FRAME -> AestheticPhotoWidget()
            WidgetType.QUICK_TOGGLES -> QuickTogglesWidget(span = widget.span)
            WidgetType.GEMINI_ASSISTANT -> GeminiAssistantWidget(span = widget.span, onPromptSelected = { onOpenSearch() })
            WidgetType.FITNESS_TRACKER -> FitnessTrackerWidget(span = widget.span)
            WidgetType.CRYPTO_MARKET -> CryptoMarketWidget(span = widget.span)
            WidgetType.WORLD_CLOCK -> WorldClockWidget(span = widget.span)
            WidgetType.VIP_CONTACTS -> VipContactsWidget(span = widget.span)
            WidgetType.QUOTE_OF_THE_DAY -> MotivationalQuoteWidget()
        }

        // Edit Mode Overlay Controls (Remove, Resize, Move)
        if (isEditMode) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.Black.copy(alpha = 0.88f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    // Move Up
                    IconButton(
                        onClick = onMoveUp,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowUpward,
                            contentDescription = "Move Up",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Move Down
                    IconButton(
                        onClick = onMoveDown,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowDownward,
                            contentDescription = "Move Down",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    // Span / Resize Cycle
                    Surface(
                        onClick = onCycleSpan,
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Text(
                            text = widget.span.displayName.split(" ").first(),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                        )
                    }

                    // Remove / Delete Button
                    IconButton(
                        onClick = onRemoveWidget,
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEF4444))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remove Widget",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
