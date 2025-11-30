package com.family.childtracker.presentation.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showApiKey by remember { mutableStateOf(false) }
    
    // Show snackbar for validation messages
    val snackbarHostState = remember { SnackbarHostState() }
    
    LaunchedEffect(uiState.validationMessage) {
        uiState.validationMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearValidationMessage()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // API Configuration Section
                Text(
                    text = "OpenRouter API Configuration",
                    style = MaterialTheme.typography.titleLarge
                )
                
                Text(
                    text = "Configure your OpenRouter API key to enable AI features like chat assistant and weekly summaries.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                // API Key Input
                OutlinedTextField(
                    value = uiState.apiKey,
                    onValueChange = viewModel::onApiKeyChanged,
                    label = { Text("API Key") },
                    placeholder = { Text("sk-or-v1-...") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (showApiKey) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    trailingIcon = {
                        IconButton(onClick = { showApiKey = !showApiKey }) {
                            Icon(
                                imageVector = if (showApiKey) {
                                    Icons.Default.VisibilityOff
                                } else {
                                    Icons.Default.Visibility
                                },
                                contentDescription = if (showApiKey) "Hide API key" else "Show API key"
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true
                )
                
                // Show masked API key if exists
                if (uiState.maskedApiKey != null && uiState.apiKey.isBlank()) {
                    Text(
                        text = "Current: ${uiState.maskedApiKey}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Test Connection Button
                Button(
                    onClick = viewModel::testConnection,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isTesting && uiState.apiKey.isNotBlank()
                ) {
                    if (uiState.isTesting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(if (uiState.isTesting) "Testing..." else "Test Connection")
                }
                
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                // AI Model Selection Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "AI Model Selection",
                        style = MaterialTheme.typography.titleMedium
                    )
                    if (uiState.isLoadingModels) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                    } else {
                        TextButton(onClick = { viewModel.refreshModels() }) {
                            Text("Refresh")
                        }
                    }
                }
                
                if (uiState.modelsLoadError != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = "Failed to load models: ${uiState.modelsLoadError}",
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
                
                if (uiState.modelsAreStale) {
                    Text(
                        text = "⚠️ Showing cached models (may be outdated)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                }
                
                // Model Selector Dropdown
                var expandedModelDropdown by remember { mutableStateOf(false) }
                
                ExposedDropdownMenuBox(
                    expanded = expandedModelDropdown,
                    onExpandedChange = { expandedModelDropdown = it }
                ) {
                    OutlinedTextField(
                        value = uiState.selectedModel.ifEmpty { "Default Model" },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Default AI Model") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedModelDropdown) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                    )
                    
                    ExposedDropdownMenu(
                        expanded = expandedModelDropdown,
                        onDismissRequest = { expandedModelDropdown = false }
                    ) {
                        // Default option
                        DropdownMenuItem(
                            text = { Text("Default Model") },
                            onClick = {
                                viewModel.onSelectedModelChanged("")
                                expandedModelDropdown = false
                            }
                        )
                        
                        // Available models
                        uiState.availableModels.forEach { model ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(
                                            text = model.name ?: model.id,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        if (model.description != null) {
                                            Text(
                                                text = model.description,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                maxLines = 2
                                            )
                                        }
                                        if (model.pricing != null) {
                                            Text(
                                                text = "Prompt: ${model.pricing.prompt} | Completion: ${model.pricing.completion}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.tertiary
                                            )
                                        }
                                    }
                                },
                                onClick = {
                                    viewModel.onSelectedModelChanged(model.id)
                                    expandedModelDropdown = false
                                }
                            )
                        }
                    }
                }
                
                Text(
                    text = "Select a model for AI chat and weekly summaries. Leave as default to use the recommended model.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                // Weekly Summary Section
                Text(
                    text = "Weekly Summary",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Auto-generate weekly summaries",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Automatically create AI summaries every Sunday at 8 PM",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = uiState.autoGenerateWeeklySummary,
                        onCheckedChange = viewModel::onAutoGenerateWeeklySummaryChanged
                    )
                }
                
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                // Security Section
                Text(
                    text = "Security",
                    style = MaterialTheme.typography.titleLarge
                )
                
                // App Lock Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Enable App Lock",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = "Require authentication to access the app",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = uiState.appLockEnabled,
                        onCheckedChange = viewModel::onAppLockChanged
                    )
                }
                
                // PIN Code Input (shown when app lock is enabled)
                if (uiState.appLockEnabled) {
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    var showPin by remember { mutableStateOf(false) }
                    
                    OutlinedTextField(
                        value = uiState.pinCode,
                        onValueChange = viewModel::onPinCodeChanged,
                        label = { Text("PIN Code") },
                        placeholder = { Text("Enter 4-6 digit PIN") },
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (showPin) {
                            VisualTransformation.None
                        } else {
                            PasswordVisualTransformation()
                        },
                        trailingIcon = {
                            IconButton(onClick = { showPin = !showPin }) {
                                Icon(
                                    imageVector = if (showPin) {
                                        Icons.Default.VisibilityOff
                                    } else {
                                        Icons.Default.Visibility
                                    },
                                    contentDescription = if (showPin) "Hide PIN" else "Show PIN"
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPad),
                        singleLine = true,
                        supportingText = {
                            Text("PIN must be at least 4 digits")
                        }
                    )
                    
                    // Biometric Authentication Toggle
                    val context = androidx.compose.ui.platform.LocalContext.current
                    val biometricManager = remember { 
                        com.family.childtracker.data.local.security.BiometricAuthManager(context) 
                    }
                    val biometricStatus = remember { biometricManager.getBiometricStatus() }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Enable Biometric Authentication",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = when (biometricStatus) {
                                    com.family.childtracker.data.local.security.BiometricStatus.AVAILABLE -> 
                                        "Use fingerprint or face recognition"
                                    com.family.childtracker.data.local.security.BiometricStatus.NO_HARDWARE -> 
                                        "Biometric hardware not available"
                                    com.family.childtracker.data.local.security.BiometricStatus.NONE_ENROLLED -> 
                                        "No biometric credentials enrolled"
                                    else -> "Biometric authentication unavailable"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = if (biometricStatus == com.family.childtracker.data.local.security.BiometricStatus.AVAILABLE) {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                } else {
                                    MaterialTheme.colorScheme.error
                                }
                            )
                        }
                        Switch(
                            checked = uiState.biometricAuthEnabled,
                            onCheckedChange = viewModel::onBiometricAuthChanged,
                            enabled = biometricStatus == com.family.childtracker.data.local.security.BiometricStatus.AVAILABLE
                        )
                    }
                    
                    // Auto-lock timeout selector
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    var expandedTimeoutDropdown by remember { mutableStateOf(false) }
                    val timeoutOptions = listOf(1, 2, 5, 10, 15, 30)
                    
                    ExposedDropdownMenuBox(
                        expanded = expandedTimeoutDropdown,
                        onExpandedChange = { expandedTimeoutDropdown = it }
                    ) {
                        OutlinedTextField(
                            value = "${uiState.autoLockTimeoutMinutes} minutes",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Auto-lock timeout") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTimeoutDropdown) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                        )
                        
                        ExposedDropdownMenu(
                            expanded = expandedTimeoutDropdown,
                            onDismissRequest = { expandedTimeoutDropdown = false }
                        ) {
                            timeoutOptions.forEach { minutes ->
                                DropdownMenuItem(
                                    text = { Text("$minutes minutes") },
                                    onClick = {
                                        viewModel.onAutoLockTimeoutChanged(minutes)
                                        expandedTimeoutDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }
                
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                // Backup & Restore Section
                Text(
                    text = "Backup & Restore",
                    style = MaterialTheme.typography.titleLarge
                )
                
                Text(
                    text = "Export your data to a backup file or restore from a previous backup. Backups are stored in app-specific storage.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                // Export options
                var showEncryptionDialog by remember { mutableStateOf(false) }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.exportData(encrypt = false) },
                        modifier = Modifier.weight(1f),
                        enabled = !uiState.isExporting && !uiState.isImporting
                    ) {
                        if (uiState.isExporting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Export Data")
                        }
                    }
                    
                    OutlinedButton(
                        onClick = { viewModel.exportData(encrypt = true) },
                        modifier = Modifier.weight(1f),
                        enabled = !uiState.isExporting && !uiState.isImporting
                    ) {
                        if (uiState.isExporting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Export (Encrypted)")
                        }
                    }
                }
                
                // Show backup message
                LaunchedEffect(uiState.backupMessage) {
                    uiState.backupMessage?.let { message ->
                        snackbarHostState.showSnackbar(message)
                        viewModel.clearBackupMessage()
                    }
                }
                
                // Import section
                var showImportDialog by remember { mutableStateOf(false) }
                
                LaunchedEffect(Unit) {
                    viewModel.loadBackupFiles()
                }
                
                if (uiState.backupFiles.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Available Backups",
                        style = MaterialTheme.typography.titleSmall
                    )
                    
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            uiState.backupFiles.take(5).forEach { file ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = file.name,
                                            style = MaterialTheme.typography.bodySmall,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = "Size: ${file.length() / 1024} KB",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    
                                    TextButton(
                                        onClick = { 
                                            viewModel.importData(file, clearExisting = false)
                                        },
                                        enabled = !uiState.isImporting && !uiState.isExporting
                                    ) {
                                        Text("Import")
                                    }
                                }
                                
                                if (file != uiState.backupFiles.take(5).last()) {
                                    Divider()
                                }
                            }
                            
                            if (uiState.backupFiles.size > 5) {
                                Text(
                                    text = "... and ${uiState.backupFiles.size - 5} more",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Save Button
                Button(
                    onClick = viewModel::saveSettings,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isSaving
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(if (uiState.isSaving) "Saving..." else "Save Settings")
                }
                
                // Help Text
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "How to get an API key:",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Text(
                            text = "1. Visit openrouter.ai\n" +
                                    "2. Sign up or log in\n" +
                                    "3. Go to Keys section\n" +
                                    "4. Create a new API key\n" +
                                    "5. Copy and paste it here",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}
