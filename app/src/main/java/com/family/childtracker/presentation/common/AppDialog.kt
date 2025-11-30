package com.family.childtracker.presentation.common

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Standard confirmation dialog
 */
@Composable
fun ConfirmationDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmText: String = "Confirm",
    dismissText: String = "Cancel",
    icon: ImageVector? = null,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        icon = icon?.let { { Icon(it, contentDescription = null) } },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm()
                    onDismiss()
                },
                modifier = Modifier.minTouchTarget()
            ) {
                Text(confirmText)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.minTouchTarget()
            ) {
                Text(dismissText)
            }
        },
        modifier = modifier
    )
}

/**
 * Destructive action dialog (for delete operations)
 */
@Composable
fun DestructiveDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmText: String = "Delete",
    dismissText: String = "Cancel",
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm()
                    onDismiss()
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                ),
                modifier = Modifier.minTouchTarget()
            ) {
                Text(confirmText)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.minTouchTarget()
            ) {
                Text(dismissText)
            }
        },
        modifier = modifier
    )
}

/**
 * Information dialog
 */
@Composable
fun InfoDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    dismissText: String = "OK",
    icon: ImageVector? = null,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        icon = icon?.let { { Icon(it, contentDescription = null) } },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.minTouchTarget()
            ) {
                Text(dismissText)
            }
        },
        modifier = modifier
    )
}
