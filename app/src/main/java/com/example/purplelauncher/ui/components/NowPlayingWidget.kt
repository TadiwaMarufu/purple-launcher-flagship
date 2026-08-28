package com.example.purplelauncher.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.purplelauncher.core.model.WidgetSpan

private data class WidgetTrackInfo(
    val title: String,
    val artist: String,
    val album: String,
    val durationSec: Int,
    val accentColor: Color
)

@Composable
fun NowPlayingWidget(
    span: WidgetSpan = WidgetSpan.WIDE,
    modifier: Modifier = Modifier
) {
    val tracks = remember {
        listOf(
            WidgetTrackInfo("Midnight City", "M83", "Hurry Up, We're Dreaming", 243, Color(0xFFEC4899)),
            WidgetTrackInfo("Starboy", "The Weeknd ft. Daft Punk", "Starboy", 230, Color(0xFFEF4444)),
            WidgetTrackInfo("Resonance", "HOME", "Odyssey", 212, Color(0xFF8B5CF6)),
            WidgetTrackInfo("After Hours", "The Weeknd", "After Hours", 361, Color(0xFFF59E0B))
        )
    }

    var currentTrackIndex by remember { mutableIntStateOf(0) }
    var isPlaying by remember { mutableStateOf(true) }
    var playbackProgress by remember { mutableFloatStateOf(0.42f) }

    val track = tracks[currentTrackIndex]

    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    val waveBar1 by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 0.95f,
        animationSpec = infiniteRepeatable(tween(450, easing = LinearEasing), RepeatMode.Reverse), label = "b1"
    )
    val waveBar2 by infiniteTransition.animateFloat(
        initialValue = 0.9f, targetValue = 0.2f,
        animationSpec = infiniteRepeatable(tween(380, easing = LinearEasing), RepeatMode.Reverse), label = "b2"
    )
    val waveBar3 by infiniteTransition.animateFloat(
        initialValue = 0.4f, targetValue = 0.85f,
        animationSpec = infiniteRepeatable(tween(520, easing = LinearEasing), RepeatMode.Reverse), label = "b3"
    )

    GlassCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            track.accentColor.copy(alpha = 0.35f),
                            Color(0xFF0F172A).copy(alpha = 0.7f)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header & Service
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF1DB954)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Spotify Music Stream",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }

                    // Mini live waveform animation
                    if (isPlaying) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                            verticalAlignment = Alignment.Bottom,
                            modifier = Modifier.height(16.dp)
                        ) {
                            Box(modifier = Modifier.width(3.dp).height((16 * waveBar1).dp).clip(CircleShape).background(track.accentColor))
                            Box(modifier = Modifier.width(3.dp).height((16 * waveBar2).dp).clip(CircleShape).background(track.accentColor))
                            Box(modifier = Modifier.width(3.dp).height((16 * waveBar3).dp).clip(CircleShape).background(track.accentColor))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Track Info & Controls Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Album Art Box with glow
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(track.accentColor, Color(0xFF1E293B))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Headphones,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Title & Artist
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = track.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1
                        )
                        Text(
                            text = "${track.artist} • ${track.album}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f),
                            maxLines = 1
                        )
                    }

                    // Media Playback Controls
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        IconButton(
                            onClick = {
                                currentTrackIndex = if (currentTrackIndex > 0) currentTrackIndex - 1 else tracks.lastIndex
                                playbackProgress = 0f
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SkipPrevious,
                                contentDescription = "Previous",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        IconButton(
                            onClick = { isPlaying = !isPlaying },
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = Color.Black,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        IconButton(
                            onClick = {
                                currentTrackIndex = (currentTrackIndex + 1) % tracks.size
                                playbackProgress = 0f
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SkipNext,
                                contentDescription = "Next",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Interactive Scrubber Slider
                Slider(
                    value = playbackProgress,
                    onValueChange = { playbackProgress = it },
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = track.accentColor,
                        inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                )

                // Time labels
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val currentSec = (track.durationSec * playbackProgress).toInt()
                    val curMin = currentSec / 60
                    val curS = currentSec % 60
                    val totalMin = track.durationSec / 60
                    val totalS = track.durationSec % 60

                    Text(
                        text = String.format("%d:%02d", curMin, curS),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = Color.White.copy(alpha = 0.6f)
                    )
                    Text(
                        text = String.format("%d:%02d", totalMin, totalS),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = Color.White.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}
