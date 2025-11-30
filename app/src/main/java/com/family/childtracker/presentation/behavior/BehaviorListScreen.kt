package com.family.childtracker.presentation.behavior

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.family.childtracker.domain.model.BehaviorEntry
import com.family.childtracker.domain.model.EatingHabits
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BehaviorListScreen(
    entries: List<BehaviorEntry>,
    onNavigateBack: () -> Unit,
    onAddEntry: () -> Unit,
    onEditEntry: (BehaviorEntry) -> Unit,
    onDeleteEntry: (BehaviorEntry) -> Unit,
    onViewCalendar: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf<BehaviorEntry?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Daily Behavior") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onViewCalendar) {
                        Icon(Icons.Default.CalendarToday, contentDescription = "Calendar view")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddEntry) {
                Icon(Icons.Default.Add, contentDescription = "Add entry")
            }
        }
    ) { paddingValues ->
        if (entries.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "No behavior entries yet",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Tap + to add your first entry",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(entries, key = { it.id }) { entry ->
                    BehaviorEntryCard(
                        entry = entry,
                        onEdit = { onEditEntry(entry) },
                        onDelete = { showDeleteDialog = entry }
                    )
                }
            }
        }
    }

    // Delete confirmation dialog
    showDeleteDialog?.let { entry ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Delete Entry") },
            text = { Text("Are you sure you want to delete this behavior entry?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteEntry(entry)
                        showDeleteDialog = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BehaviorEntryCard(
    entry: BehaviorEntry,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = { expanded = !expanded }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header with date and mood
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = entry.entryDate.format(DateTimeFormatter.ofPattern("EEEE, MMM dd, yyyy")),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (entry.mood != null) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = getMoodEmoji(entry.mood),
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = entry.mood.name.lowercase().replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                Row {
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            }

            // Summary info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (entry.sleepQuality != null) {
                    Column {
                        Text(
                            text = "Sleep",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "⭐".repeat(entry.sleepQuality),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                if (entry.eatingHabits != null) {
                    Column {
                        Text(
                            text = "Eating",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = formatEatingHabits(entry.eatingHabits),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // Expanded notes
            if (expanded && !entry.notes.isNullOrBlank()) {
                Divider()
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Notes",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = entry.notes,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

fun formatEatingHabits(habits: EatingHabits): String {
    return habits.name.lowercase().replaceFirstChar { it.uppercase() }
}
