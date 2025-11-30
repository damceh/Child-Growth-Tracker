package com.family.childtracker.presentation.milestone

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.family.childtracker.domain.model.MilestoneCategory
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MilestoneEntryScreen(
    childId: String,
    milestoneId: String?,
    formState: MilestoneFormState,
    isSaving: Boolean,
    onFormStateChange: (MilestoneFormState) -> Unit,
    onSave: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val dateFormatter = remember { DateTimeFormatter.ofPattern("MMM dd, yyyy") }
    var showDatePicker by remember { mutableStateOf(false) }
    var showCategoryMenu by remember { mutableStateOf(false) }

    // Photo picker launcher
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            onFormStateChange(formState.copy(photoUri = it.toString()))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (milestoneId != null) "Edit Milestone" else "Add Milestone") },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Category Selector
            ExposedDropdownMenuBox(
                expanded = showCategoryMenu,
                onExpandedChange = { showCategoryMenu = it }
            ) {
                OutlinedTextField(
                    value = formState.category.name.lowercase().replaceFirstChar { it.uppercase() },
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showCategoryMenu) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = showCategoryMenu,
                    onDismissRequest = { showCategoryMenu = false }
                ) {
                    MilestoneCategory.values().forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.name.lowercase().replaceFirstChar { it.uppercase() }) },
                            onClick = {
                                onFormStateChange(formState.copy(category = category))
                                showCategoryMenu = false
                            }
                        )
                    }
                }
            }

            // Description
            OutlinedTextField(
                value = formState.description,
                onValueChange = { onFormStateChange(formState.copy(description = it)) },
                label = { Text("Description *") },
                placeholder = { Text("e.g., First steps, Said first word") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4
            )

            // Achievement Date
            OutlinedTextField(
                value = formState.achievementDate.format(dateFormatter),
                onValueChange = {},
                label = { Text("Achievement Date") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Select date"
                        )
                    }
                }
            )

            // Notes
            OutlinedTextField(
                value = formState.notes,
                onValueChange = { onFormStateChange(formState.copy(notes = it)) },
                label = { Text("Notes (optional)") },
                placeholder = { Text("Add any additional details...") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 6
            )

            // Photo Attachment
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Photo (optional)",
                        style = MaterialTheme.typography.titleSmall
                    )

                    if (formState.photoUri != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        ) {
                            AsyncImage(
                                model = formState.photoUri,
                                contentDescription = "Milestone photo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            IconButton(
                                onClick = { onFormStateChange(formState.copy(photoUri = null)) },
                                modifier = Modifier.align(Alignment.TopEnd)
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Remove photo",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    } else {
                        OutlinedButton(
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddAPhoto,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Add Photo")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.weight(1f),
                    enabled = !isSaving
                ) {
                    Text("Cancel")
                }
                Button(
                    onClick = onSave,
                    modifier = Modifier.weight(1f),
                    enabled = !isSaving && formState.description.isNotBlank()
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    } else {
                        Text("Save")
                    }
                }
            }
        }
    }

    // Date Picker Dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = formState.achievementDate.toEpochDay() * 24 * 60 * 60 * 1000
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selectedDate = LocalDate.ofEpochDay(millis / (24 * 60 * 60 * 1000))
                            onFormStateChange(formState.copy(achievementDate = selectedDate))
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
