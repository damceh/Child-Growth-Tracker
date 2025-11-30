package com.family.childtracker.presentation.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.family.childtracker.presentation.theme.Spacing

/**
 * Standard card component with consistent styling
 */
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = if (onClick != null) {
            modifier.clickable(onClick = onClick)
        } else {
            modifier
        },
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp
        )
    ) {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.padding(Spacing.cardPadding),
            content = content
        )
    }
}

/**
 * Elevated card component for emphasis
 */
@Composable
fun ElevatedAppCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    ElevatedCard(
        modifier = if (onClick != null) {
            modifier.clickable(onClick = onClick)
        } else {
            modifier
        },
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        )
    ) {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.padding(Spacing.cardPadding),
            content = content
        )
    }
}

/**
 * Outlined card component for subtle emphasis
 */
@Composable
fun OutlinedAppCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    OutlinedCard(
        modifier = if (onClick != null) {
            modifier.clickable(onClick = onClick)
        } else {
            modifier
        },
        shape = MaterialTheme.shapes.medium,
        border = CardDefaults.outlinedCardBorder()
    ) {
        androidx.compose.foundation.layout.Column(
            modifier = Modifier.padding(Spacing.cardPadding),
            content = content
        )
    }
}
