package com.family.childtracker.presentation.common

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Button with loading state support
 */
@Composable
fun LoadingButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.buttonColors()
) {
    Button(
        onClick = onClick,
        modifier = modifier.minTouchTarget(),
        enabled = enabled && !isLoading,
        colors = colors
    ) {
        if (isLoading) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(text)
            }
        } else {
            Text(text)
        }
    }
}

/**
 * Outlined button with loading state support
 */
@Composable
fun LoadingOutlinedButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.minTouchTarget(),
        enabled = enabled && !isLoading
    ) {
        if (isLoading) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(text)
            }
        } else {
            Text(text)
        }
    }
}

/**
 * Text button with loading state support
 */
@Composable
fun LoadingTextButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = true
) {
    TextButton(
        onClick = onClick,
        modifier = modifier.minTouchTarget(),
        enabled = enabled && !isLoading
    ) {
        if (isLoading) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(text)
            }
        } else {
            Text(text)
        }
    }
}
