package com.family.childtracker.presentation.behavior

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.family.childtracker.domain.model.BehaviorEntry
import com.family.childtracker.domain.model.EatingHabits
import com.family.childtracker.domain.model.Mood
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BehaviorEntryScreen(
    childId: String,
    existingEntry: BehaviorEntry? = null,
    onNavigateBack: () -> Unit,
    onSave: (LocalDate, Mood?, Int?, EatingHabits?, String?) -> Unit
) {
    var entryDate by remember { mutableStateOf(existingEntry?.entryDate ?: LocalDate.now()) }
    var selectedMood by remember { mutableStateOf(existingEntry?.mood) }
    var sleepQuality by remember { mutableStateOf(existingEntry?.sleepQuality ?: 0) }
    var selectedEatingHabits by remember { mutableStateOf(existingEntry?.eatingHabits) }
    var notes by remember { mutableStateOf(existingEntry?.notes ?: "") }
    var showDatePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (existingEntry != null) "Edit Behavior Entry" else "Add Behavior Entry") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Date Picker
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = { showDatePicker = true }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Date",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = entryDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Icon(Icons.Default.DateRange, contentDescription = "Select date")
                }
            }

            // Mood Selection
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Mood",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    MoodButton(
                        emoji = "😊",
                        mood = Mood.HAPPY,
                        label = "Happy",
                        isSelected = selectedMood == Mood.HAPPY,
                        onClick = { selectedMood = if (selectedMood == Mood.HAPPY) null else Mood.HAPPY }
                    )
                    MoodButton(
                        emoji = "😌",
                        mood = Mood.CALM,
                        label = "Calm",
                        isSelected = selectedMood == Mood.CALM,
                        onClick = { selectedMood = if (selectedMood == Mood.CALM) null else Mood.CALM }
                    )
                    MoodButton(
                        emoji = "😐",
                        mood = Mood.FUSSY,
                        label = "Fussy",
                        isSelected = selectedMood == Mood.FUSSY,
                        onClick = { selectedMood = if (selectedMood == Mood.FUSSY) null else Mood.FUSSY }
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    MoodButton(
                        emoji = "😠",
                        mood = Mood.CRANKY,
                        label = "Cranky",
                        isSelected = selectedMood == Mood.CRANKY,
                        onClick = { selectedMood = if (selectedMood == Mood.CRANKY) null else Mood.CRANKY }
                    )
                    MoodButton(
                        emoji = "⚡",
                        mood = Mood.ENERGETIC,
                        label = "Energetic",
                        isSelected = selectedMood == Mood.ENERGETIC,
                        onClick = { selectedMood = if (selectedMood == Mood.ENERGETIC) null else Mood.ENERGETIC }
                    )
                    // Empty space for alignment
                    Spacer(modifier = Modifier.width(80.dp))
                }
            }

            // Sleep Quality Rating
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Sleep Quality",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    (1..5).forEach { rating ->
                        IconButton(
                            onClick = { sleepQuality = if (sleepQuality == rating) 0 else rating }
                        ) {
                            Text(
                                text = if (rating <= sleepQuality) "⭐" else "☆",
                                style = MaterialTheme.typography.headlineMedium
                            )
                        }
                    }
                }
                if (sleepQuality > 0) {
                    Text(
                        text = "$sleepQuality/5",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }

            // Eating Habits
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Eating Habits",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    EatingHabits.values().forEach { habit ->
                        EatingHabitOption(
                            habit = habit,
                            isSelected = selectedEatingHabits == habit,
                            onClick = { selectedEatingHabits = if (selectedEatingHabits == habit) null else habit }
                        )
                    }
                }
            }

            // Notes
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Notes (optional)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    placeholder = { Text("Add any additional observations...") },
                    maxLines = 5
                )
            }

            // Save Button
            Button(
                onClick = {
                    onSave(
                        entryDate,
                        selectedMood,
                        if (sleepQuality > 0) sleepQuality else null,
                        selectedEatingHabits,
                        notes.takeIf { it.isNotBlank() }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Entry")
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            initialDate = entryDate,
            onDateSelected = { date ->
                entryDate = date
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}

@Composable
fun MoodButton(
    emoji: String,
    mood: Mood,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    OutlinedCard(
        onClick = onClick,
        modifier = Modifier.size(80.dp),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
        ),
        colors = CardDefaults.outlinedCardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = emoji,
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
fun EatingHabitOption(
    habit: EatingHabits,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    OutlinedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
        ),
        colors = CardDefaults.outlinedCardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = habit.name.lowercase().replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.bodyLarge
            )
            RadioButton(
                selected = isSelected,
                onClick = onClick
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    initialDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate.toEpochDay() * 24 * 60 * 60 * 1000
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = LocalDate.ofEpochDay(millis / (24 * 60 * 60 * 1000))
                        onDateSelected(date)
                    }
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
    ) {
        DatePicker(state = datePickerState)
    }
}
