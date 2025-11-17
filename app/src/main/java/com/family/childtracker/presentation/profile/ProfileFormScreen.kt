package com.family.childtracker.presentation.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.family.childtracker.domain.model.ChildProfile
import com.family.childtracker.domain.model.Gender
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileFormScreen(
    profile: ChildProfile? = null,
    onSave: (String, LocalDate, Gender) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf(profile?.name ?: "") }
    var dateOfBirth by remember { mutableStateOf(profile?.dateOfBirth ?: LocalDate.now()) }
    var gender by remember { mutableStateOf(profile?.gender ?: Gender.OTHER) }
    var showDatePicker by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var dateError by remember { mutableStateOf<String?>(null) }

    val isEditMode = profile != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEditMode) "Edit Profile" else "Add Profile") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    nameError = null
                },
                label = { Text("Name") },
                isError = nameError != null,
                supportingText = nameError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = dateOfBirth.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                onValueChange = { },
                label = { Text("Date of Birth") },
                readOnly = true,
                isError = dateError != null,
                supportingText = dateError?.let { { Text(it) } },
                modifier = Modifier
                    .fillMaxWidth(),
                trailingIcon = {
                    TextButton(onClick = { showDatePicker = true }) {
                        Text("Select")
                    }
                }
            )

            Text(
                text = "Gender",
                style = MaterialTheme.typography.labelLarge
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Gender.values().forEach { genderOption ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RadioButton(
                            selected = gender == genderOption,
                            onClick = { gender = genderOption }
                        )
                        Text(
                            text = genderOption.name.lowercase().replaceFirstChar { it.uppercase() },
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    var hasError = false
                    
                    if (name.isBlank()) {
                        nameError = "Name cannot be empty"
                        hasError = true
                    }
                    
                    if (dateOfBirth.isAfter(LocalDate.now())) {
                        dateError = "Date of birth cannot be in the future"
                        hasError = true
                    }
                    
                    if (!hasError) {
                        onSave(name, dateOfBirth, gender)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (isEditMode) "Update Profile" else "Create Profile")
            }
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            initialDate = dateOfBirth,
            onDateSelected = { selectedDate ->
                dateOfBirth = selectedDate
                dateError = null
                showDatePicker = false
            },
            onDismiss = { showDatePicker = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerDialog(
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
                        val selectedDate = LocalDate.ofEpochDay(millis / (24 * 60 * 60 * 1000))
                        onDateSelected(selectedDate)
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
