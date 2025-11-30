package com.family.childtracker.presentation.reports

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.family.childtracker.domain.model.MonthlySummary
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthlySummaryScreen(
    childId: String,
    childName: String,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: MonthlySummaryViewModel = viewModel(
        factory = MonthlySummaryViewModelFactory(context)
    )
    val uiState by viewModel.uiState.collectAsState()

    var selectedMonth by remember { mutableStateOf(YearMonth.now().minusMonths(1)) }
    var showMonthPicker by remember { mutableStateOf(false) }

    LaunchedEffect(childId, selectedMonth) {
        viewModel.generateSummary(childId, selectedMonth)
    }

    // Handle share intent
    LaunchedEffect(uiState) {
        if (uiState is MonthlySummaryUiState.ReadyToShare) {
            val intent = (uiState as MonthlySummaryUiState.ReadyToShare).intent
            context.startActivity(intent)
            viewModel.resetToSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Monthly Summary") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Month selector
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = { showMonthPicker = true }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Icon(Icons.Default.CalendarMonth, "Select month")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (val state = uiState) {
                is MonthlySummaryUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is MonthlySummaryUiState.Success -> {
                    SummaryContent(
                        summary = state.summary,
                        onExportPdf = { viewModel.exportAsPdf() },
                        onExportImage = { viewModel.exportAsImage() }
                    )
                }

                is MonthlySummaryUiState.ExportSuccess -> {
                    SummaryContent(
                        summary = state.summary,
                        onExportPdf = { viewModel.exportAsPdf() },
                        onExportImage = { viewModel.exportAsImage() }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Export successful!",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "File saved to: ${state.file.name}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { viewModel.shareSummary(state.file, state.exportType) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Share, "Share")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Share")
                            }
                        }
                    }
                }

                is MonthlySummaryUiState.Exporting -> {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Exporting...")
                        }
                    }
                }

                is MonthlySummaryUiState.Error -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = state.message,
                            modifier = Modifier.padding(16.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }

                else -> {}
            }
        }
    }

    if (showMonthPicker) {
        MonthPickerDialog(
            currentMonth = selectedMonth,
            onMonthSelected = { month ->
                selectedMonth = month
                showMonthPicker = false
            },
            onDismiss = { showMonthPicker = false }
        )
    }
}

@Composable
fun SummaryContent(
    summary: MonthlySummary,
    onExportPdf: () -> Unit,
    onExportImage: () -> Unit
) {
    val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")

    // Child info
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = summary.childName,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${summary.monthStart.format(dateFormatter)} - ${summary.monthEnd.format(dateFormatter)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Growth summary
    summary.growthSummary?.let { growth ->
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.TrendingUp, "Growth", tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Growth Metrics",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                growth.endHeight?.let { height ->
                    GrowthMetricRow(
                        label = "Height",
                        value = "%.1f cm".format(height),
                        change = growth.heightChange,
                        percentile = growth.heightPercentile
                    )
                }

                growth.endWeight?.let { weight ->
                    GrowthMetricRow(
                        label = "Weight",
                        value = "%.2f kg".format(weight),
                        change = growth.weightChange,
                        percentile = growth.weightPercentile
                    )
                }

                growth.endHeadCircumference?.let { headCirc ->
                    GrowthMetricRow(
                        label = "Head Circumference",
                        value = "%.1f cm".format(headCirc),
                        change = growth.headCircumferenceChange,
                        percentile = growth.headCircumferencePercentile
                    )
                }

                if (growth.hasSignificantChange) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                "Warning",
                                tint = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Significant growth change detected",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    // Milestones
    if (summary.milestones.isNotEmpty()) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.EmojiEvents, "Milestones", tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Milestones (${summary.milestones.size})",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                summary.milestones.forEach { milestone ->
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Text("• ", style = MaterialTheme.typography.bodyLarge)
                        Column {
                            Text(
                                text = milestone.description,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = "${milestone.achievementDate.format(dateFormatter)} • ${milestone.category.name}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    // Behavior summary
    summary.behaviorSummary?.let { behavior ->
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Mood, "Behavior", tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Behavior Summary",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Total entries: ${behavior.totalEntries}",
                    style = MaterialTheme.typography.bodyLarge
                )

                behavior.averageSleepQuality?.let { sleep ->
                    Text(
                        text = "Average sleep quality: %.1f/5".format(sleep),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                behavior.dominantMood?.let { mood ->
                    Text(
                        text = "Most common mood: ${mood.name.lowercase().replaceFirstChar { it.uppercase() }}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                behavior.dominantEatingHabits?.let { eating ->
                    Text(
                        text = "Eating habits: ${eating.name.lowercase().replaceFirstChar { it.uppercase() }}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    // Export buttons
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(
            onClick = onExportPdf,
            modifier = Modifier.weight(1f)
        ) {
            Icon(Icons.Default.PictureAsPdf, "PDF")
            Spacer(modifier = Modifier.width(4.dp))
            Text("Export PDF")
        }

        OutlinedButton(
            onClick = onExportImage,
            modifier = Modifier.weight(1f)
        ) {
            Icon(Icons.Default.Image, "Image")
            Spacer(modifier = Modifier.width(4.dp))
            Text("Export Image")
        }
    }
}

@Composable
fun GrowthMetricRow(
    label: String,
    value: String,
    change: Float?,
    percentile: Int?
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            change?.let {
                Text(
                    text = "Change: ${if (it >= 0) "+" else ""}%.2f".format(it),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (it >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }

            percentile?.let {
                Text(
                    text = "${it}th percentile",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun MonthPickerDialog(
    currentMonth: YearMonth,
    onMonthSelected: (YearMonth) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedYear by remember { mutableStateOf(currentMonth.year) }
    var selectedMonthValue by remember { mutableStateOf(currentMonth.monthValue) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Month") },
        text = {
            Column {
                // Year selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { selectedYear-- }) {
                        Icon(Icons.Default.ChevronLeft, "Previous year")
                    }
                    Text(
                        text = selectedYear.toString(),
                        style = MaterialTheme.typography.titleLarge
                    )
                    IconButton(onClick = { selectedYear++ }) {
                        Icon(Icons.Default.ChevronRight, "Next year")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Month grid
                Column {
                    for (row in 0..2) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            for (col in 1..4) {
                                val month = row * 4 + col
                                FilterChip(
                                    selected = month == selectedMonthValue,
                                    onClick = { selectedMonthValue = month },
                                    label = {
                                        Text(
                                            YearMonth.of(selectedYear, month)
                                                .format(DateTimeFormatter.ofPattern("MMM"))
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onMonthSelected(YearMonth.of(selectedYear, selectedMonthValue))
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
