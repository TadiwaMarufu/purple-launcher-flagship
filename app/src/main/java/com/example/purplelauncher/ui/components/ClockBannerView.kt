package com.example.purplelauncher.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.purplelauncher.core.model.ClockStyle
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ClockBannerView(
    modifier: Modifier = Modifier,
    profileName: String = "HOME",
    clockStyle: ClockStyle = ClockStyle.TALL_CONDENSED,
    accentColor: Color = MaterialTheme.colorScheme.primary,
    onClockClick: () -> Unit = {}
) {
    var currentTime by remember { mutableStateOf(Date()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = Date()
            delay(1000L)
        }
    }

    val hourFormat = remember { SimpleDateFormat("HH", Locale.getDefault()) }
    val minFormat = remember { SimpleDateFormat("mm", Locale.getDefault()) }
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val dateFormat = remember { SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()) }
    val shortDateFormat = remember { SimpleDateFormat("EEE, d MMM", Locale.getDefault()) }

    val cal = Calendar.getInstance()
    val hour = cal.get(Calendar.HOUR_OF_DAY)
    val greeting = when (hour) {
        in 5..11 -> "Good morning"
        in 12..17 -> "Focus afternoon"
        in 18..22 -> "Good evening"
        else -> "Night owl"
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClockClick)
            .padding(horizontal = 4.dp, vertical = 4.dp)
    ) {
        when (clockStyle) {
            ClockStyle.TALL_CONDENSED -> {
                // Image 1 style: Ultra-tall condensed display typography
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = hourFormat.format(currentTime),
                            style = MaterialTheme.typography.displayLarge.copy(fontSize = 72.sp, lineHeight = 72.sp),
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Text(
                            text = ":",
                            style = MaterialTheme.typography.displayLarge.copy(fontSize = 72.sp, lineHeight = 72.sp),
                            fontWeight = FontWeight.ExtraBold,
                            color = accentColor
                        )
                        Text(
                            text = minFormat.format(currentTime),
                            style = MaterialTheme.typography.displayLarge.copy(fontSize = 72.sp, lineHeight = 72.sp),
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                    Text(
                        text = dateFormat.format(currentTime),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            ClockStyle.EDITORIAL_STACK -> {
                // Image 8 style: Giant stacked numbers
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "28° Sunny • Home",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = accentColor,
                            letterSpacing = 1.5.sp
                        )
                        Text(
                            text = dateFormat.format(currentTime).uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.7f),
                            letterSpacing = 2.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = hourFormat.format(currentTime),
                        style = MaterialTheme.typography.displayLarge.copy(fontSize = 64.sp, lineHeight = 60.sp),
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Serif,
                        color = Color.White
                    )
                    Text(
                        text = minFormat.format(currentTime),
                        style = MaterialTheme.typography.displayLarge.copy(fontSize = 64.sp, lineHeight = 60.sp),
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Serif,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }

            ClockStyle.NOTHING_DOT_MATRIX -> {
                // Image 6 style: Dot matrix font
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = timeFormat.format(currentTime),
                        style = MaterialTheme.typography.displayLarge.copy(fontSize = 48.sp),
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = dateFormat.format(currentTime).uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        fontFamily = FontFamily.Monospace,
                        color = accentColor,
                        letterSpacing = 1.sp
                    )
                }
            }

            ClockStyle.SPLIT_CAPSULE -> {
                // Image 7 style: Split vertical clock inside modern capsule
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White.copy(alpha = 0.12f))
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = hourFormat.format(currentTime),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(
                            text = minFormat.format(currentTime),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Black,
                            color = accentColor
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = shortDateFormat.format(currentTime),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "28° Clear Sky",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            else -> {
                // Modern Clean Default
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = greeting.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = accentColor,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )

                        Text(
                            text = "[$profileName]",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.6f),
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = timeFormat.format(currentTime),
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Light,
                        color = Color.White,
                        letterSpacing = (-1).sp
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = dateFormat.format(currentTime),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
