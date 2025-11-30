package com.family.childtracker.presentation.behavior

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.family.childtracker.domain.model.BehaviorEntry
import com.family.childtracker.domain.model.Mood
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BehaviorCalendarScreen(
    entries: List<BehaviorEntry>,
    onNavigateBack: () -> Unit,
    onAddEntry: () -> Unit,
    onEntryClick: (BehaviorEntry) -> Unit
) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    val entriesByDate = remember(entries) {
        entries.groupBy { it.entryDate }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Behavior Calendar") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Month Navigation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Previous month")
                }
                Text(
                    text = currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "Next month")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Day of week headers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { day ->
                    Text(
                        text = day,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Calendar Grid
            CalendarGrid(
                yearMonth = currentMonth,
                entriesByDate = entriesByDate,
                onDateClick = { date ->
                    entriesByDate[date]?.firstOrNull()?.let { onEntryClick(it) }
                }
            )
        }
    }
}

@Composable
fun CalendarGrid(
    yearMonth: YearMonth,
    entriesByDate: Map<LocalDate, List<BehaviorEntry>>,
    onDateClick: (LocalDate) -> Unit
) {
    val firstDayOfMonth = yearMonth.atDay(1)
    val lastDayOfMonth = yearMonth.atEndOfMonth()
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7 // Sunday = 0
    val daysInMonth = yearMonth.lengthOfMonth()

    // Create list of dates including empty cells for alignment
    val calendarDates = mutableListOf<LocalDate?>()
    repeat(firstDayOfWeek) { calendarDates.add(null) }
    for (day in 1..daysInMonth) {
        calendarDates.add(yearMonth.atDay(day))
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        contentPadding = PaddingValues(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(calendarDates) { date ->
            if (date != null) {
                CalendarDayCell(
                    date = date,
                    entry = entriesByDate[date]?.firstOrNull(),
                    isToday = date == LocalDate.now(),
                    onClick = { onDateClick(date) }
                )
            } else {
                Spacer(modifier = Modifier.aspectRatio(1f))
            }
        }
    }
}

@Composable
fun CalendarDayCell(
    date: LocalDate,
    entry: BehaviorEntry?,
    isToday: Boolean,
    onClick: () -> Unit
) {
    OutlinedCard(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable(enabled = entry != null, onClick = onClick),
        border = BorderStroke(
            width = if (isToday) 2.dp else 1.dp,
            color = if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
        ),
        colors = CardDefaults.outlinedCardColors(
            containerColor = when {
                entry != null -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                else -> MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
            )
            if (entry != null) {
                Text(
                    text = getMoodEmoji(entry.mood),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

fun getMoodEmoji(mood: Mood?): String {
    return when (mood) {
        Mood.HAPPY -> "😊"
        Mood.CALM -> "😌"
        Mood.FUSSY -> "😐"
        Mood.CRANKY -> "😠"
        Mood.ENERGETIC -> "⚡"
        null -> ""
    }
}
