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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.purplelauncher.core.model.WidgetSpan

private data class QuickNoteItem(
    val id: String,
    val text: String,
    val isDone: Boolean
)

@Composable
fun QuickNotesWidget(
    span: WidgetSpan = WidgetSpan.MEDIUM,
    modifier: Modifier = Modifier
) {
    var notes by remember {
        mutableStateOf(
            listOf(
                QuickNoteItem("1", "Review quarterly design tokens", false),
                QuickNoteItem("2", "Ship launcher v2 update", true),
                QuickNoteItem("3", "Buy oat milk & cold brew", false)
            )
        )
    }

    var showAddDialog by remember { mutableStateOf(false) }
    var newNoteText by remember { mutableStateOf("") }

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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF59E0B).copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EditNote,
                            contentDescription = null,
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Quick Tasks",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                IconButton(
                    onClick = { showAddDialog = true },
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.1f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Task",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            notes.forEach { note ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable {
                            notes = notes.map {
                                if (it.id == note.id) it.copy(isDone = !it.isDone) else it
                            }
                        }
                        .padding(vertical = 4.dp, horizontal = 4.dp)
                ) {
                    Checkbox(
                        checked = note.isDone,
                        onCheckedChange = { isChecked ->
                            notes = notes.map {
                                if (it.id == note.id) it.copy(isDone = isChecked) else it
                            }
                        },
                        colors = CheckboxDefaults.colors(
                            checkedColor = Color(0xFFF59E0B),
                            uncheckedColor = Color.White.copy(alpha = 0.4f),
                            checkmarkColor = Color.Black
                        ),
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = note.text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (note.isDone) Color.White.copy(alpha = 0.4f) else Color.White,
                        textDecoration = if (note.isDone) TextDecoration.LineThrough else TextDecoration.None,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = { notes = notes.filter { it.id != note.id } },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Delete",
                            tint = Color.White.copy(alpha = 0.3f),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Quick Task") },
            text = {
                OutlinedTextField(
                    value = newNoteText,
                    onValueChange = { newNoteText = it },
                    placeholder = { Text("Enter task description...") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newNoteText.isNotBlank()) {
                            notes = notes + QuickNoteItem(
                                id = java.util.UUID.randomUUID().toString(),
                                text = newNoteText.trim(),
                                isDone = false
                            )
                            newNoteText = ""
                            showAddDialog = false
                        }
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
