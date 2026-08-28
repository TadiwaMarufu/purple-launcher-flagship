package com.example.purplelauncher.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.purplelauncher.core.model.*
import com.example.purplelauncher.core.repository.SearchRepository
import com.example.purplelauncher.ui.components.GlassCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UniversalSearchScreen(
    searchRepository: SearchRepository,
    profiles: List<Profile>,
    spaces: List<SpaceConfig>,
    onLaunchApp: (String, String?) -> Unit,
    onSwitchProfile: (String) -> Unit,
    onOpenSpace: (String) -> Unit,
    onOpenDevTool: (String) -> Unit,
    onCloseSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var query by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<SearchResult>>(emptyList()) }
    val scope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(query) {
        scope.launch {
            searchResults = searchRepository.search(query, profiles, spaces)
        }
    }

    LaunchedEffect(Unit) {
        try {
            focusRequester.requestFocus()
        } catch (_: Exception) {}
    }

    fun handleResultClick(result: SearchResult) {
        when (result.type) {
            SearchResultType.APP -> {
                if (result.packageName != null) {
                    onLaunchApp(result.packageName, result.activityName)
                    onCloseSearch()
                }
            }
            SearchResultType.SETTING, SearchResultType.ACTION -> {
                if (result.actionIntent != null) {
                    try {
                        val intent = Intent(result.actionIntent).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(intent)
                        onCloseSearch()
                    } catch (_: Exception) {}
                }
            }
            SearchResultType.DEV_TOOL -> {
                if (result.actionIntent != null) {
                    onOpenDevTool(result.actionIntent)
                    onCloseSearch()
                }
            }
            SearchResultType.SHORTCUT -> {
                if (result.actionIntent?.startsWith("switch_profile:") == true) {
                    val pId = result.actionIntent.removePrefix("switch_profile:")
                    onSwitchProfile(pId)
                    onCloseSearch()
                }
            }
            SearchResultType.SPACE -> {
                if (result.actionIntent?.startsWith("open_space:") == true) {
                    val sId = result.actionIntent.removePrefix("open_space:")
                    onOpenSpace(sId)
                    onCloseSearch()
                }
            }
            SearchResultType.CONTACT -> {
                if (result.actionIntent != null) {
                    try {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse(result.actionIntent)).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(intent)
                        onCloseSearch()
                    } catch (_: Exception) {}
                }
            }
            SearchResultType.WEB -> {
                if (result.actionIntent != null) {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(result.actionIntent)).apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                        context.startActivity(intent)
                        onCloseSearch()
                    } catch (_: Exception) {}
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.98f))
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Search bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                value = query,
                onValueChange = { query = it },
                placeholder = {
                    Text(
                        "Search apps, settings, tools, web...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        searchResults.firstOrNull()?.let { handleResultClick(it) }
                    }
                ),
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
                    .focusRequester(focusRequester)
            )

            Spacer(modifier = Modifier.width(8.dp))

            TextButton(onClick = onCloseSearch) {
                Text(
                    "Cancel",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Results list
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(searchResults, key = { it.id }) { item ->
                SearchResultRow(
                    result = item,
                    onClick = { handleResultClick(item) }
                )
            }
        }
    }
}

@Composable
fun SearchResultRow(
    result: SearchResult,
    onClick: () -> Unit
) {
    val tagColor = when (result.type) {
        SearchResultType.APP -> MaterialTheme.colorScheme.primary
        SearchResultType.DEV_TOOL -> Color(0xFFA855F7)
        SearchResultType.SETTING -> Color(0xFF38BDF8)
        SearchResultType.SPACE -> Color(0xFFFB923C)
        SearchResultType.CONTACT -> Color(0xFF4ADE80)
        SearchResultType.WEB -> Color(0xFF94A3B8)
        else -> MaterialTheme.colorScheme.secondary
    }

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        backgroundColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = result.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = result.subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(tagColor.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = result.type.name,
                    style = MaterialTheme.typography.labelSmall,
                    color = tagColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
