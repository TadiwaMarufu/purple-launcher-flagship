package com.example.purplelauncher.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.purplelauncher.core.model.WidgetSpan

private data class HourlyForecast(
    val time: String,
    val temp: String,
    val iconName: String,
    val isNow: Boolean = false
)

@Composable
fun LiveWeatherWidget(
    span: WidgetSpan = WidgetSpan.WIDE,
    modifier: Modifier = Modifier
) {
    var isFahrenheit by remember { mutableStateOf(false) }
    var selectedCity by remember { mutableStateOf("San Francisco") }
    var condition by remember { mutableStateOf("Partly Cloudy") }
    var currentTempC by remember { mutableStateOf(21) }

    val currentTemp = if (isFahrenheit) "${(currentTempC * 9 / 5) + 32}°F" else "${currentTempC}°C"

    val hourlyList = remember(isFahrenheit) {
        listOf(
            HourlyForecast("Now", if (isFahrenheit) "70°" else "21°", "cloud", true),
            HourlyForecast("12 PM", if (isFahrenheit) "73°" else "23°", "sun"),
            HourlyForecast("1 PM", if (isFahrenheit) "75°" else "24°", "sun"),
            HourlyForecast("2 PM", if (isFahrenheit) "74°" else "23°", "cloud"),
            HourlyForecast("3 PM", if (isFahrenheit) "70°" else "21°", "rain"),
            HourlyForecast("4 PM", if (isFahrenheit) "66°" else "19°", "rain"),
            HourlyForecast("5 PM", if (isFahrenheit) "64°" else "18°", "cloud")
        )
    }

    GlassCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF0284C7).copy(alpha = 0.35f),
                            Color(0xFF0369A1).copy(alpha = 0.15f),
                            Color(0xFF0F172A).copy(alpha = 0.45f)
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Header: Location & Unit Toggle
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF38BDF8).copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = selectedCity,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = condition,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }

                    // Unit toggle chip
                    Surface(
                        onClick = { isFahrenheit = !isFahrenheit },
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = if (isFahrenheit) "°F" else "°C",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF38BDF8),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Main Temp & Stats Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = currentTemp,
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isFahrenheit) "H: 75° L: 58°" else "H: 24° L: 14°",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                    }

                    // Key Weather Metrics
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        WeatherMetricBadge(icon = Icons.Outlined.WaterDrop, label = "68%", sub = "Humidity")
                        WeatherMetricBadge(icon = Icons.Outlined.Air, label = "12 km/h", sub = "Wind")
                        WeatherMetricBadge(icon = Icons.Outlined.WbSunny, label = "UV 4", sub = "Moderate")
                    }
                }

                if (span == WidgetSpan.WIDE || span == WidgetSpan.FULL) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Divider(color = Color.White.copy(alpha = 0.1f))
                    Spacer(modifier = Modifier.height(10.dp))

                    // Hourly Forecast Timeline
                    LazyRow(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(hourlyList) { item ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (item.isNow) Color.White.copy(alpha = 0.18f)
                                        else Color.Transparent
                                    )
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = item.time,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (item.isNow) Color(0xFF38BDF8) else Color.White.copy(alpha = 0.7f),
                                    fontWeight = if (item.isNow) FontWeight.Bold else FontWeight.Normal
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Icon(
                                    imageVector = when (item.iconName) {
                                        "sun" -> Icons.Default.WbSunny
                                        "rain" -> Icons.Default.WaterDrop
                                        else -> Icons.Default.Cloud
                                    },
                                    contentDescription = null,
                                    tint = if (item.iconName == "sun") Color(0xFFFBBF24) else Color(0xFF38BDF8),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = item.temp,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WeatherMetricBadge(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    sub: String
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White.copy(alpha = 0.08f)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF38BDF8),
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = sub,
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp),
                color = Color.White.copy(alpha = 0.5f)
            )
        }
    }
}
