package com.example.purplelauncher.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class NewsArticle(
    val id: String,
    val title: String,
    val source: String,
    val timeAgo: String,
    val category: String,
    val gradient: Brush
)

val SampleNews = listOf(
    NewsArticle(
        "1",
        "Next-Generation Android UI Paradigms: Fluid Spatial Layouts and Adaptive Glass Surfaces",
        "TechCrunch",
        "15m ago",
        "Tech",
        Brush.linearGradient(listOf(Color(0xFF1E1B4B), Color(0xFF312E81)))
    ),
    NewsArticle(
        "2",
        "Design Systems in 2026: Why Restraint and Expressive Typography Lead the Future",
        "UX Collective",
        "42m ago",
        "Design",
        Brush.linearGradient(listOf(Color(0xFF064E3B), Color(0xFF047857)))
    ),
    NewsArticle(
        "3",
        "James Webb Telescope Reveals New Insights into Deep Galaxy Core Formations",
        "NASA Science",
        "1h ago",
        "Science",
        Brush.linearGradient(listOf(Color(0xFF4C0519), Color(0xFF881337)))
    ),
    NewsArticle(
        "4",
        "AI Coding Agents Revolutionize Mobile Engineering Pipelines with Real-Time Iteration",
        "Ars Technica",
        "2h ago",
        "AI",
        Brush.linearGradient(listOf(Color(0xFF3B0764), Color(0xFF581C87)))
    )
)

val NewsCategories = listOf("All", "Tech", "Design", "AI", "Science", "World")

@Composable
fun NowFeedView(
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf("All") }
    val filteredNews = remember(selectedCategory) {
        if (selectedCategory == "All") SampleNews else SampleNews.filter { it.category == selectedCategory }
    }

    val todayFormatted = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d"))

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp)
    ) {
        // Daily Briefing Header Card
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "MORNING BRIEF",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            text = "28° Sunny",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = todayFormatted,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Good morning! You have 3 tasks planned today and zero unread urgent alerts. Daily battery forecast is optimal.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.85f),
                        lineHeight = 20.sp
                    )
                }
            }
        }

        // Category Filter Chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(NewsCategories) { category ->
                    val isSelected = category == selectedCategory
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = category },
                        label = { Text(category) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                            containerColor = Color.White.copy(alpha = 0.08f),
                            labelColor = Color.White
                        )
                    )
                }
            }
        }

        // News Feed Cards
        items(filteredNews) { article ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(article.gradient)
                    .padding(18.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.Black.copy(alpha = 0.4f)
                        ) {
                            Text(
                                text = article.source,
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        Text(
                            text = article.timeAgo,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = article.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        lineHeight = 22.sp
                    )
                }
            }
        }
    }
}
