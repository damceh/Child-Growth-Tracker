package com.family.childtracker.presentation.lock

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.family.childtracker.data.local.security.BiometricAuthManager
import com.family.childtracker.data.local.security.BiometricStatus

@Composable
fun AppLockScreen(
    biometricEnabled: Boolean,
    pinCode: String?,
    onUnlock: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val biometricManager = remember { BiometricAuthManager(context) }
    
    var pinInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showPinInput by remember { mutableStateOf(!biometricEnabled) }
    
    // Attempt biometric authentication on first composition if enabled
    LaunchedEffect(biometricEnabled) {
        if (biometricEnabled && context is FragmentActivity) {
            val status = biometricManager.getBiometricStatus()
            if (status == BiometricStatus.AVAILABLE) {
                biometricManager.authenticate(
                    activity = context,
                    title = "Unlock App",
                    subtitle = "Use biometric authentication to unlock",
                    negativeButtonText = "Use PIN",
                    onSuccess = {
                        onUnlock()
                    },
                    onError = { errorCode, errorMsg ->
                        // User cancelled or chose to use PIN
                        if (errorCode == androidx.biometric.BiometricPrompt.ERROR_NEGATIVE_BUTTON ||
                            errorCode == androidx.biometric.BiometricPrompt.ERROR_USER_CANCELED) {
                            showPinInput = true
                        } else {
                            errorMessage = errorMsg
                            showPinInput = true
                        }
                    },
                    onFailed = {
                        errorMessage = "Authentication failed. Please try again."
                    }
                )
            } else {
                // Biometric not available, show PIN input
                showPinInput = true
            }
        }
    }
    
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Lock",
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "App Locked",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Authenticate to continue",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            if (showPinInput && pinCode != null) {
                OutlinedTextField(
                    value = pinInput,
                    onValueChange = { 
                        pinInput = it
                        errorMessage = null
                    },
                    label = { Text("Enter PIN") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPad),
                    singleLine = true,
                    isError = errorMessage != null,
                    supportingText = errorMessage?.let { { Text(it) } },
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = {
                        if (pinInput == pinCode) {
                            onUnlock()
                        } else {
                            errorMessage = "Incorrect PIN"
                            pinInput = ""
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = pinInput.isNotEmpty()
                ) {
                    Text("Unlock")
                }
                
                if (biometricEnabled && context is FragmentActivity) {
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    TextButton(
                        onClick = {
                            biometricManager.authenticate(
                                activity = context,
                                title = "Unlock App",
                                subtitle = "Use biometric authentication to unlock",
                                negativeButtonText = "Cancel",
                                onSuccess = {
                                    onUnlock()
                                },
                                onError = { _, errorMsg ->
                                    errorMessage = errorMsg
                                },
                                onFailed = {
                                    errorMessage = "Authentication failed"
                                }
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Use Biometric Authentication")
                    }
                }
            } else if (pinCode == null) {
                Text(
                    text = "No PIN configured. Please configure a PIN in Settings.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
