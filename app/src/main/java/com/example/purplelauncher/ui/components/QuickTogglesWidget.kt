package com.example.purplelauncher.ui.components

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.purplelauncher.core.model.WidgetSpan

@Composable
fun QuickTogglesWidget(
    span: WidgetSpan = WidgetSpan.WIDE,
    modifier: Modifier = Modifier
) {
    var isWifiOn by remember { mutableStateOf(true) }
    var isBluetoothOn by remember { mutableStateOf(true) }
    var isFlashlightOn by remember { mutableStateOf(false) }
    var isDarkModeOn by remember { mutableStateOf(true) }
    var isHotspotOn by remember { mutableStateOf(false) }
    var isDndOn by remember { mutableStateOf(false) }

    GlassCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Quick Controls",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Active Profile: Default",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                ToggleIconTile(
                    icon = Icons.Default.Wifi,
                    label = "Wi-Fi",
                    sub = if (isWifiOn) "Galaxy_5G" else "Off",
                    isActive = isWifiOn,
                    onClick = { isWifiOn = !isWifiOn },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                ToggleIconTile(
                    icon = Icons.Default.Bluetooth,
                    label = "Bluetooth",
                    sub = if (isBluetoothOn) "Buds Pro" else "Off",
                    isActive = isBluetoothOn,
                    onClick = { isBluetoothOn = !isBluetoothOn },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                ToggleIconTile(
                    icon = Icons.Default.FlashlightOn,
                    label = "Flashlight",
                    sub = if (isFlashlightOn) "On" else "Off",
                    isActive = isFlashlightOn,
                    onClick = { isFlashlightOn = !isFlashlightOn },
                    modifier = Modifier.weight(1f)
                )
            }

            if (span == WidgetSpan.WIDE || span == WidgetSpan.FULL) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ToggleIconTile(
                        icon = Icons.Default.DarkMode,
                        label = "Dark Mode",
                        sub = if (isDarkModeOn) "OLED Black" else "Off",
                        isActive = isDarkModeOn,
                        onClick = { isDarkModeOn = !isDarkModeOn },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    ToggleIconTile(
                        icon = Icons.Default.WifiTethering,
                        label = "Hotspot",
                        sub = if (isHotspotOn) "Active" else "Off",
                        isActive = isHotspotOn,
                        onClick = { isHotspotOn = !isHotspotOn },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    ToggleIconTile(
                        icon = Icons.Default.DoNotDisturbOn,
                        label = "DND",
                        sub = if (isDndOn) "Silent" else "Off",
                        isActive = isDndOn,
                        onClick = { isDndOn = !isDndOn },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun ToggleIconTile(
    icon: ImageVector,
    label: String,
    sub: String,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (isActive) MaterialTheme.colorScheme.primary.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.08f),
        border = if (isActive) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)) else null,
        modifier = modifier.height(68.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isActive) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.6f),
                modifier = Modifier.size(20.dp)
            )
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = sub,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                    color = if (isActive) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.5f)
                )
            }
        }
    }
}
