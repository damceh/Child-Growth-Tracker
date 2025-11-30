package com.family.childtracker.presentation.summary

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.family.childtracker.domain.model.WeeklySummary
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklySummaryScreen(
    viewModel: WeeklySummaryViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val generationState by viewModel.generationState.collectAsState()
    
    // Show snackbar for generation results
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(generationState) {
        when (val state = generationState) {
            is WeeklySummaryViewModel.GenerationState.Success -> {
                snackbarHostState.showSnackbar("Summary generated successfully!")
                viewModel.clearGenerationState()
            }
            is WeeklySummaryViewModel.GenerationState.AlreadyExists -> {
                snackbarHostState.showSnackbar("Summary already exists for this week")
                viewModel.clearGenerationState()
            }
            is WeeklySummaryViewModel.GenerationState.Error -> {
                snackbarHostState.showSnackbar(state.message)
                viewModel.clearGenerationState()
            }
            else -> {}
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Weekly Summaries") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            if (uiState !is WeeklySummaryViewModel.UiState.NoChildSelected) {
                FloatingActionButton(
                    onClick = { viewModel.generateSummary() },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Generate Summary")
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is WeeklySummaryViewModel.UiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is WeeklySummaryViewModel.UiState.NoChildSelected -> {
                    EmptyState(
                        message = "No child selected. Please select a child from the dashboard.",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is WeeklySummaryViewModel.UiState.Empty -> {
                    EmptyState(
                        message = "No summaries yet. Tap the + button to generate your first weekly summary!",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is WeeklySummaryViewModel.UiState.Success -> {
                    SummaryList(
                        summaries = state.summaries,
                        onDeleteSummary = { viewModel.deleteSummary(it) },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                is WeeklySummaryViewModel.UiState.Error -> {
                    ErrorState(
                        message = state.message,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
            
            // Show loading overlay when generating
            if (generationState is WeeklySummaryViewModel.GenerationState.Generating) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier.padding(32.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Generating summary...",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryList(
    summaries: List<WeeklySummary>,
    onDeleteSummary: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(summaries, key = { it.id }) { summary ->
            SummaryCard(
                summary = summary,
                onDelete = { onDeleteSummary(summary.id) }
            )
        }
    }
}

@Composable
private fun SummaryCard(
    summary: WeeklySummary,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with date range
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = formatWeekRange(summary.weekStartDate, summary.weekEndDate),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = formatGeneratedTime(summary.generatedAt),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete summary",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Summary content
            Text(
                text = summary.summaryContent,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
    
    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Summary") },
            text = { Text("Are you sure you want to delete this weekly summary?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
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
private fun EmptyState(
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.DateRange,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ErrorState(
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.Warning,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
    }
}

private fun formatWeekRange(startDate: java.time.LocalDate, endDate: java.time.LocalDate): String {
    val formatter = DateTimeFormatter.ofPattern("MMM d")
    val yearFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")
    
    return if (startDate.year == endDate.year) {
        "${startDate.format(formatter)} - ${endDate.format(yearFormatter)}"
    } else {
        "${startDate.format(yearFormatter)} - ${endDate.format(yearFormatter)}"
    }
}

private fun formatGeneratedTime(instant: java.time.Instant): String {
    val now = java.time.Instant.now()
    val hoursAgo = ChronoUnit.HOURS.between(instant, now)
    val daysAgo = ChronoUnit.DAYS.between(instant, now)
    
    return when {
        hoursAgo < 1 -> "Generated just now"
        hoursAgo < 24 -> "Generated $hoursAgo hour${if (hoursAgo > 1) "s" else ""} ago"
        daysAgo < 7 -> "Generated $daysAgo day${if (daysAgo > 1) "s" else ""} ago"
        else -> {
            val formatter = DateTimeFormatter.ofPattern("MMM d, yyyy")
            "Generated ${java.time.LocalDateTime.ofInstant(instant, java.time.ZoneId.systemDefault()).format(formatter)}"
        }
    }
}
