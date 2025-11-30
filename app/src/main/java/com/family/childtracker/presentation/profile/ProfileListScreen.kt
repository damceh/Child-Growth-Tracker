package com.family.childtracker.presentation.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.family.childtracker.domain.model.ChildProfile
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileListScreen(
    profiles: List<ChildProfile>,
    uiState: ProfileUiState,
    onProfileClick: (ChildProfile) -> Unit,
    onAddProfileClick: () -> Unit,
    onDeleteProfile: (String) -> Unit,
    onViewGrowth: (ChildProfile) -> Unit,
    onViewMilestones: ((ChildProfile) -> Unit)? = null,
    onViewBehavior: ((ChildProfile) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Child Profiles") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddProfileClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Profile")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (uiState) {
                is ProfileUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is ProfileUiState.Empty -> {
                    EmptyProfilesMessage(
                        onAddProfileClick = onAddProfileClick,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is ProfileUiState.Error -> {
                    ErrorMessage(
                        message = uiState.message,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(profiles, key = { it.id }) { profile ->
                            ProfileCard(
                                profile = profile,
                                onClick = { onProfileClick(profile) },
                                onDelete = { onDeleteProfile(profile.id) },
                                onViewGrowth = { onViewGrowth(profile) },
                                onViewMilestones = onViewMilestones?.let { { it(profile) } },
                                onViewBehavior = onViewBehavior?.let { { it(profile) } }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileCard(
    profile: ChildProfile,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onViewGrowth: () -> Unit,
    onViewMilestones: (() -> Unit)? = null,
    onViewBehavior: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Column {
                        Text(
                            text = profile.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = getAgeText(profile.dateOfBirth),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = profile.dateOfBirth.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onViewGrowth,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Growth")
                    }
                    if (onViewMilestones != null) {
                        Button(
                            onClick = onViewMilestones,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Milestones")
                        }
                    }
                }
                if (onViewBehavior != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onViewBehavior,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Behavior")
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onClick,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Edit Profile")
                    }
                    TextButton(onClick = { showDeleteDialog = true }) {
                        Text("Delete")
                    }
                }
            }
        }
    }
    
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Profile") },
            text = { Text("Are you sure you want to delete ${profile.name}'s profile? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun EmptyProfilesMessage(
    onAddProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "No profiles yet",
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = "Create a profile to start tracking your child's growth and development",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Button(onClick = onAddProfileClick) {
            Text("Add Profile")
        }
    }
}

@Composable
private fun ErrorMessage(
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Error",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.error
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

private fun getAgeText(dateOfBirth: LocalDate): String {
    val period = Period.between(dateOfBirth, LocalDate.now())
    return when {
        period.years > 0 -> "${period.years} year${if (period.years > 1) "s" else ""} old"
        period.months > 0 -> "${period.months} month${if (period.months > 1) "s" else ""} old"
        else -> "${period.days} day${if (period.days > 1) "s" else ""} old"
    }
}
