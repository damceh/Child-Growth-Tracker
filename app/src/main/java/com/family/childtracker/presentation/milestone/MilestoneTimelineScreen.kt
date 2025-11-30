package com.family.childtracker.presentation.milestone

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.family.childtracker.domain.model.Milestone
import com.family.childtracker.domain.model.MilestoneCategory
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MilestoneTimelineScreen(
    milestones: List<Milestone>,
    isLoading: Boolean,
    selectedCategory: MilestoneCategory?,
    onCategorySelected: (MilestoneCategory?) -> Unit,
    onAddMilestone: () -> Unit,
    onEditMilestone: (String) -> Unit,
    onDeleteMilestone: (String) -> Unit,
    onNavigateBack: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Milestones") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddMilestone) {
                Icon(Icons.Default.Add, contentDescription = "Add Milestone")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Category Filter Chips
            CategoryFilterRow(
                selectedCategory = selectedCategory,
                onCategorySelected = onCategorySelected
            )

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (milestones.isEmpty()) {
                EmptyMilestonesView(
                    selectedCategory = selectedCategory,
                    onAddMilestone = onAddMilestone
                )
            } else {
                MilestoneTimeline(
                    milestones = milestones,
                    onEditMilestone = onEditMilestone,
                    onDeleteMilestone = { showDeleteDialog = it }
                )
            }
        }
    }

    // Delete Confirmation Dialog
    showDeleteDialog?.let { milestoneId ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Delete Milestone") },
            text = { Text("Are you sure you want to delete this milestone? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteMilestone(milestoneId)
                        showDeleteDialog = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
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

@Composable
private fun CategoryFilterRow(
    selectedCategory: MilestoneCategory?,
    onCategorySelected: (MilestoneCategory?) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = { onCategorySelected(null) },
                    label = { Text("All") }
                )
                FilterChip(
                    selected = selectedCategory == MilestoneCategory.PHYSICAL,
                    onClick = { onCategorySelected(MilestoneCategory.PHYSICAL) },
                    label = { Text("Physical") }
                )
                FilterChip(
                    selected = selectedCategory == MilestoneCategory.COGNITIVE,
                    onClick = { onCategorySelected(MilestoneCategory.COGNITIVE) },
                    label = { Text("Cognitive") }
                )
            }
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedCategory == MilestoneCategory.SOCIAL,
                    onClick = { onCategorySelected(MilestoneCategory.SOCIAL) },
                    label = { Text("Social") }
                )
                FilterChip(
                    selected = selectedCategory == MilestoneCategory.LANGUAGE,
                    onClick = { onCategorySelected(MilestoneCategory.LANGUAGE) },
                    label = { Text("Language") }
                )
                FilterChip(
                    selected = selectedCategory == MilestoneCategory.CUSTOM,
                    onClick = { onCategorySelected(MilestoneCategory.CUSTOM) },
                    label = { Text("Custom") }
                )
            }
        }
    }
}

@Composable
private fun MilestoneTimeline(
    milestones: List<Milestone>,
    onEditMilestone: (String) -> Unit,
    onDeleteMilestone: (String) -> Unit
) {
    val groupedMilestones = milestones.groupBy { YearMonth.from(it.achievementDate) }
        .toSortedMap(compareByDescending { it })

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        groupedMilestones.forEach { (yearMonth, monthMilestones) ->
            item {
                Text(
                    text = yearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(monthMilestones) { milestone ->
                MilestoneCard(
                    milestone = milestone,
                    onEdit = { onEditMilestone(milestone.id) },
                    onDelete = { onDeleteMilestone(milestone.id) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MilestoneCard(
    milestone: Milestone,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    val dateFormatter = remember { DateTimeFormatter.ofPattern("MMM dd") }
    val categoryIcon = remember(milestone.category) {
        when (milestone.category) {
            MilestoneCategory.PHYSICAL -> "🚶"
            MilestoneCategory.COGNITIVE -> "🧸"
            MilestoneCategory.SOCIAL -> "👋"
            MilestoneCategory.LANGUAGE -> "🗣️"
            MilestoneCategory.CUSTOM -> "⭐"
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = categoryIcon,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Column {
                        Text(
                            text = milestone.description,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = milestone.achievementDate.format(dateFormatter),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "•",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = milestone.category.name.lowercase().replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More options")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit") },
                            onClick = {
                                showMenu = false
                                onEdit()
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Edit, contentDescription = null)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            onClick = {
                                showMenu = false
                                onDelete()
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        )
                    }
                }
            }

            if (!milestone.notes.isNullOrBlank()) {
                Text(
                    text = milestone.notes,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (milestone.photoUri != null) {
                AsyncImage(
                    model = milestone.photoUri,
                    contentDescription = "Milestone photo",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
private fun EmptyMilestonesView(
    selectedCategory: MilestoneCategory?,
    onAddMilestone: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            )
            Text(
                text = if (selectedCategory != null) {
                    "No ${selectedCategory.name.lowercase()} milestones yet"
                } else {
                    "No milestones yet"
                },
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Start tracking your child's achievements",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(onClick = onAddMilestone) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add First Milestone")
            }
        }
    }
}
