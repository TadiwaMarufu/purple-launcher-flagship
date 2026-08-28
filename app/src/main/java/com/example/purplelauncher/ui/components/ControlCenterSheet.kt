package com.example.purplelauncher.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ControlCenterSheet(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isWifiOn by remember { mutableStateOf(true) }
    var isBluetoothOn by remember { mutableStateOf(true) }
    var isTorchOn by remember { mutableStateOf(false) }
    var isAirplaneOn by remember { mutableStateOf(false) }
    var isDataOn by remember { mutableStateOf(true) }
    var isDarkModeOn by remember { mutableStateOf(true) }
    var isAutoRotateOn by remember { mutableStateOf(false) }
    var isDndOn by remember { mutableStateOf(false) }

    var brightnessValue by remember { mutableFloatStateOf(0.75f) }
    var volumeValue by remember { mutableFloatStateOf(0.60f) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.75f))
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(16.dp)
            .clickable { onDismiss() },
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp))
                .background(Color(0xFF14151C).copy(alpha = 0.95f))
                .padding(20.dp)
                .clickable(enabled = false) {}, // Prevent dismiss when tapping card
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Pill Handle
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.3f))
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Primary Connectivity Pills (Wi-Fi & Bluetooth)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ConnectivityPill(
                    title = "Wi-Fi",
                    subtitle = if (isWifiOn) "Alrayis 5G" else "Off",
                    icon = Icons.Default.Wifi,
                    isActive = isWifiOn,
                    onToggle = { isWifiOn = !isWifiOn },
                    modifier = Modifier.weight(1f)
                )

                ConnectivityPill(
                    title = "Bluetooth",
                    subtitle = if (isBluetoothOn) "Buds Pro" else "Off",
                    icon = Icons.Default.Bluetooth,
                    isActive = isBluetoothOn,
                    onToggle = { isBluetoothOn = !isBluetoothOn },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Now Playing Media Pill
            NowPlayingPill(
                isCompact = true
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 4x2 Quick Toggle Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                QuickCircleToggle(
                    icon = Icons.Default.FlashlightOn,
                    label = "Torch",
                    isActive = isTorchOn,
                    onClick = { isTorchOn = !isTorchOn }
                )
                QuickCircleToggle(
                    icon = Icons.Default.Flight,
                    label = "Flight",
                    isActive = isAirplaneOn,
                    onClick = { isAirplaneOn = !isAirplaneOn }
                )
                QuickCircleToggle(
                    icon = Icons.Default.SignalCellularAlt,
                    label = "Data",
                    isActive = isDataOn,
                    onClick = { isDataOn = !isDataOn }
                )
                QuickCircleToggle(
                    icon = Icons.Default.DarkMode,
                    label = "Dark",
                    isActive = isDarkModeOn,
                    onClick = { isDarkModeOn = !isDarkModeOn }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                QuickCircleToggle(
                    icon = Icons.Default.ScreenRotation,
                    label = "Rotate",
                    isActive = isAutoRotateOn,
                    onClick = { isAutoRotateOn = !isAutoRotateOn }
                )
                QuickCircleToggle(
                    icon = Icons.Default.DoNotDisturb,
                    label = "DND",
                    isActive = isDndOn,
                    onClick = { isDndOn = !isDndOn }
                )
                QuickCircleToggle(
                    icon = Icons.Default.Cast,
                    label = "Cast",
                    isActive = false,
                    onClick = {}
                )
                QuickCircleToggle(
                    icon = Icons.Default.QrCodeScanner,
                    label = "Scan",
                    isActive = false,
                    onClick = {}
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Dual Sliders (Brightness & Media Volume)
            ControlSlider(
                icon = Icons.Default.WbSunny,
                value = brightnessValue,
                onValueChange = { brightnessValue = it },
                activeColor = Color(0xFF38BDF8)
            )

            Spacer(modifier = Modifier.height(10.dp))

            ControlSlider(
                icon = Icons.Default.VolumeUp,
                value = volumeValue,
                onValueChange = { volumeValue = it },
                activeColor = Color(0xFF22C55E)
            )
        }
    }
}

@Composable
private fun ConnectivityPill(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isActive: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onToggle,
        shape = RoundedCornerShape(20.dp),
        color = if (isActive) Color(0xFF38BDF8).copy(alpha = 0.25f) else Color.White.copy(alpha = 0.08f),
        modifier = modifier.height(60.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (isActive) Color(0xFF38BDF8) else Color.White.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = if (isActive) Color.Black else Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun QuickCircleToggle(
    icon: ImageVector,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(if (isActive) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.12f))
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isActive) MaterialTheme.colorScheme.onPrimary else Color.White,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}

@Composable
private fun ControlSlider(
    icon: ImageVector,
    value: Float,
    onValueChange: (Float) -> Unit,
    activeColor: Color
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color.White.copy(alpha = 0.1f),
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = activeColor,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Slider(
                value = value,
                onValueChange = onValueChange,
                colors = SliderDefaults.colors(
                    thumbColor = activeColor,
                    activeTrackColor = activeColor,
                    inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                ),
                modifier = Modifier.weight(1f)
            )
        }
    }
}
