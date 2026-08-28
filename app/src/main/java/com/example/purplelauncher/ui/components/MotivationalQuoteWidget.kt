package com.example.purplelauncher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val QuotesList = listOf(
    Pair("Act boldly. Small steps every day lead to big results.", "Marcus Aurelius"),
    Pair("Simplicity is the ultimate sophistication.", "Leonardo da Vinci"),
    Pair("Design is not just what it looks like. Design is how it works.", "Steve Jobs"),
    Pair("Focus is a muscle. Train it with intention.", "Seneca"),
    Pair("The future belongs to those who build it today.", "Alan Kay")
)

@Composable
fun MotivationalQuoteWidget(
    modifier: Modifier = Modifier
) {
    var quoteIndex by remember { mutableIntStateOf(0) }
    val (quoteText, author) = QuotesList[quoteIndex % QuotesList.size]

    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable { quoteIndex = (quoteIndex + 1) % QuotesList.size },
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.FormatQuote,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = quoteText,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                fontStyle = FontStyle.Italic,
                color = Color.White,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "— $author",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}
