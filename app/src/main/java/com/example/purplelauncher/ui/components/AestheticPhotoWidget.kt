package com.example.purplelauncher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Refresh
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

data class AestheticCardPreset(
    val title: String,
    val subtitle: String,
    val gradient: Brush
)

val AestheticCardPresets = listOf(
    AestheticCardPreset(
        "NIGHT PAGODA",
        "Kyoto • Noir Atmosphere",
        Brush.verticalGradient(listOf(Color(0xFF0F172A), Color(0xFF1E293B), Color(0xFF020617)))
    ),
    AestheticCardPreset(
        "SATURN RINGS",
        "Deep Cosmos • 11:39 PM",
        Brush.verticalGradient(listOf(Color(0xFF1E1B4B), Color(0xFF09090B), Color(0xFF000000)))
    ),
    AestheticCardPreset(
        "MIST FOREST",
        "Nordic Pine • 18:54",
        Brush.verticalGradient(listOf(Color(0xFF064E3B), Color(0xFF022C22), Color(0xFF0A0A0A)))
    ),
    AestheticCardPreset(
        "MONOCHROME STUDY",
        "Coffee & Notes • Minimal",
        Brush.verticalGradient(listOf(Color(0xFF27272A), Color(0xFF18181B), Color(0xFF09090B)))
    ),
    AestheticCardPreset(
        "CYBER AESTHETIC",
        "Tokyo Neon • Electric Wave",
        Brush.verticalGradient(listOf(Color(0xFF581C87), Color(0xFF3B0764), Color(0xFF09090B)))
    )
)

@Composable
fun AestheticPhotoWidget(
    modifier: Modifier = Modifier
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    val currentPreset = AestheticCardPresets[currentIndex % AestheticCardPresets.size]

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(130.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(currentPreset.gradient)
            .clickable {
                currentIndex = (currentIndex + 1) % AestheticCardPresets.size
            }
            .padding(14.dp)
    ) {
        Column(
            modifier = Modifier.align(Alignment.BottomStart)
        ) {
            Text(
                text = currentPreset.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = currentPreset.subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.75f)
            )
        }

        // Tap to cycle hint
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.Black.copy(alpha = 0.4f),
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Tap Photo",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}
