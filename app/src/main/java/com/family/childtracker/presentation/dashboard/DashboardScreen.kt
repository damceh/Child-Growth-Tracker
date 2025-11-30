package com.family.childtracker.presentation.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.family.childtracker.domain.model.*
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    selectedProfile: ChildProfile?,
    profiles: List<ChildProfile>,
    latestGrowth: GrowthRecord?,
    latestMilestone: Milestone?,
    recentBehaviorSummary: String?,
    dailyTip: ParentingTip?,
    onProfileSelected: (ChildProfile) -> Unit,
    onNavigateToProfiles: () -> Unit,
    onNavigateToGrowth: () -> Unit,
    onNavigateToMilestones: () -> Unit,
    onNavigateToBehavior: () -> Unit,
    onNavigateToTips: () -> Unit,
    onNavigateToChat: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToWeeklySummary: () -> Unit = {},
    onNavigateToMonthlySummary: () -> Unit = {}
) {
    var showProfileSelector by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header with child selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Child Growth Tracker",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Row {
                IconButton(onClick = onNavigateToChat) {
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = "AI Assistant"
                    )
                }
                IconButton(onClick = onNavigateToSettings) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings"
                    )
                }
            }
        }

        // Child Profile Selector
        if (profiles.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                onClick = { showProfileSelector = true }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = selectedProfile?.name ?: "Select a child",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            selectedProfile?.let {
                                Text(
                                    text = "Age: ${calculateAge(it.dateOfBirth)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Select child"
                    )
                }
            }
        } else {
            // No profiles yet
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                onClick = onNavigateToProfiles
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Add your first child profile",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }

        // Daily Parenting Tip
        dailyTip?.let { tip ->
            DailyTipCard(
                tip = tip,
                onReadMore = onNavigateToTips,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Summary Cards (only show if profile is selected)
        selectedProfile?.let {
            // Latest Growth Card
            SummaryCard(
                title = "Latest Growth",
                icon = Icons.Default.TrendingUp,
                content = {
                    if (latestGrowth != null) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                latestGrowth.height?.let { height ->
                                    Text("Height: ${String.format("%.1f", height)} cm")
                                }
                                latestGrowth.weight?.let { weight ->
                                    Text("Weight: ${String.format("%.1f", weight)} kg")
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Recorded: ${formatDate(latestGrowth.recordDate)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        Text(
                            text = "No growth records yet",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                onClick = onNavigateToGrowth,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Recent Milestone Card
            SummaryCard(
                title = "Recent Milestone",
                icon = Icons.Default.EmojiEvents,
                content = {
                    if (latestMilestone != null) {
                        Column {
                            Text(
                                text = latestMilestone.description,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Achieved: ${formatDate(latestMilestone.achievementDate)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        Text(
                            text = "No milestones logged yet",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                onClick = onNavigateToMilestones,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Behavior Trends Card
            SummaryCard(
                title = "Behavior Trends",
                icon = Icons.Default.Mood,
                content = {
                    Text(
                        text = recentBehaviorSummary ?: "No behavior entries this week",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                onClick = onNavigateToBehavior,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            // Weekly Summary Card
            SummaryCard(
                title = "Weekly Summaries",
                icon = Icons.Default.DateRange,
                content = {
                    Text(
                        text = "View AI-generated weekly summaries of your child's progress",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                onClick = onNavigateToWeeklySummary,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            // Monthly Reports Card
            SummaryCard(
                title = "Monthly Reports",
                icon = Icons.Default.Assessment,
                content = {
                    Text(
                        text = "Generate and share detailed monthly summary reports",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                onClick = onNavigateToMonthlySummary,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }
    }

    // Profile Selector Dialog
    if (showProfileSelector) {
        AlertDialog(
            onDismissRequest = { showProfileSelector = false },
            title = { Text("Select Child") },
            text = {
                Column {
                    profiles.forEach { profile ->
                        TextButton(
                            onClick = {
                                onProfileSelected(profile)
                                showProfileSelector = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(horizontalAlignment = Alignment.Start) {
                                    Text(
                                        text = profile.name,
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Text(
                                        text = "Age: ${calculateAge(profile.dateOfBirth)}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    TextButton(
                        onClick = {
                            showProfileSelector = false
                            onNavigateToProfiles()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Manage Profiles")
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showProfileSelector = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
private fun DailyTipCard(
    tip: ParentingTip,
    onReadMore: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Daily Parenting Tip",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Text(
                text = tip.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = tip.content.take(100) + if (tip.content.length > 100) "..." else "",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = onReadMore) {
                Text("Read More")
            }
        }
    }
}

@Composable
private fun SummaryCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            content()
        }
    }
}

private fun calculateAge(dateOfBirth: java.time.LocalDate): String {
    val now = java.time.LocalDate.now()
    val years = java.time.Period.between(dateOfBirth, now).years
    val months = java.time.Period.between(dateOfBirth, now).months
    
    return when {
        years > 0 -> "$years year${if (years > 1) "s" else ""}"
        months > 0 -> "$months month${if (months > 1) "s" else ""}"
        else -> {
            val days = java.time.Period.between(dateOfBirth, now).days
            "$days day${if (days > 1) "s" else ""}"
        }
    }
}

private fun formatDate(date: java.time.LocalDate): String {
    val now = java.time.LocalDate.now()
    val daysAgo = java.time.Period.between(date, now).days
    
    return when {
        daysAgo == 0 -> "Today"
        daysAgo == 1 -> "Yesterday"
        daysAgo < 7 -> "$daysAgo days ago"
        daysAgo < 14 -> "1 week ago"
        daysAgo < 30 -> "${daysAgo / 7} weeks ago"
        else -> date.format(DateTimeFormatter.ofPattern("MMM d, yyyy"))
    }
}
