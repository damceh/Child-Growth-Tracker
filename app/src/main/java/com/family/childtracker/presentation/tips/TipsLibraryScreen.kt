package com.family.childtracker.presentation.tips

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.family.childtracker.domain.model.AgeRange
import com.family.childtracker.domain.model.ParentingTip
import com.family.childtracker.domain.model.TipCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TipsLibraryScreen(
    uiState: ParentingTipsUiState,
    onSearchQueryChange: (String) -> Unit,
    onCategorySelected: (TipCategory?) -> Unit,
    onAgeRangeSelected: (AgeRange?) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onClearFilters: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var selectedTip by remember { mutableStateOf<ParentingTip?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Parenting Tips") },
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
        ) {
            // Search bar
            SearchBar(
                query = uiState.searchQuery,
                onQueryChange = onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            // Category filters
            CategoryFilterRow(
                selectedCategory = uiState.selectedCategory,
                onCategorySelected = onCategorySelected,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Age range filters
            AgeRangeFilterRow(
                selectedAgeRange = uiState.selectedAgeRange,
                onAgeRangeSelected = onAgeRangeSelected,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            // Clear filters button
            if (uiState.selectedCategory != null || uiState.selectedAgeRange != null || uiState.searchQuery.isNotBlank()) {
                TextButton(
                    onClick = onClearFilters,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Clear Filters")
                }
            }

            // Tips list
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.filteredTips.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (uiState.tips.isEmpty()) "No tips available" else "No tips match your filters",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.filteredTips) { tip ->
                        TipCard(
                            tip = tip,
                            isFavorite = uiState.favoriteTipIds.contains(tip.id),
                            onToggleFavorite = { onToggleFavorite(tip.id) },
                            onClick = { selectedTip = tip }
                        )
                    }
                }
            }
        }
    }

    // Tip detail dialog
    selectedTip?.let { tip ->
        TipDetailDialog(
            tip = tip,
            isFavorite = uiState.favoriteTipIds.contains(tip.id),
            onToggleFavorite = { onToggleFavorite(tip.id) },
            onDismiss = { selectedTip = null }
        )
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text("Search tips...") },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null)
        },
        trailingIcon = {
            if (query.isNotBlank()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear search")
                }
            }
        },
        singleLine = true
    )
}

@Composable
fun CategoryFilterRow(
    selectedCategory: TipCategory?,
    onCategorySelected: (TipCategory?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                selected = selectedCategory == null,
                onClick = { onCategorySelected(null) },
                label = { Text("All") }
            )
        }
        items(TipCategory.values()) { category ->
            FilterChip(
                selected = selectedCategory == category,
                onClick = { onCategorySelected(category) },
                label = { Text(category.name.lowercase().replaceFirstChar { it.uppercase() }) }
            )
        }
    }
}

@Composable
fun AgeRangeFilterRow(
    selectedAgeRange: AgeRange?,
    onAgeRangeSelected: (AgeRange?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                selected = selectedAgeRange == null,
                onClick = { onAgeRangeSelected(null) },
                label = { Text("All Ages") }
            )
        }
        items(AgeRange.values()) { ageRange ->
            FilterChip(
                selected = selectedAgeRange == ageRange,
                onClick = { onAgeRangeSelected(ageRange) },
                label = { Text(formatAgeRange(ageRange)) }
            )
        }
    }
}

@Composable
fun TipCard(
    tip: ParentingTip,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = getCategoryIcon(tip.category) + " " + tip.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AssistChip(
                            onClick = {},
                            label = { Text(tip.category.name.lowercase().replaceFirstChar { it.uppercase() }) },
                            modifier = Modifier.height(24.dp)
                        )
                        AssistChip(
                            onClick = {},
                            label = { Text(formatAgeRange(tip.ageRange)) },
                            modifier = Modifier.height(24.dp)
                        )
                    }
                }
                IconButton(onClick = onToggleFavorite) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                        tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = tip.content,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun TipDetailDialog(
    tip: ParentingTip,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = getCategoryIcon(tip.category) + " " + tip.title,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onToggleFavorite) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                        tint = if (isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        text = {
            Column {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    AssistChip(
                        onClick = {},
                        label = { Text(tip.category.name.lowercase().replaceFirstChar { it.uppercase() }) }
                    )
                    AssistChip(
                        onClick = {},
                        label = { Text(formatAgeRange(tip.ageRange)) }
                    )
                }
                Text(
                    text = tip.content,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

fun getCategoryIcon(category: TipCategory): String {
    return when (category) {
        TipCategory.NUTRITION -> "🍎"
        TipCategory.SLEEP -> "💤"
        TipCategory.DEVELOPMENT -> "🎯"
        TipCategory.BEHAVIOR -> "😊"
        TipCategory.SAFETY -> "🛡️"
        TipCategory.HEALTH -> "❤️"
    }
}

fun formatAgeRange(ageRange: AgeRange): String {
    return when (ageRange) {
        AgeRange.NEWBORN -> "Newborn"
        AgeRange.INFANT -> "Infant"
        AgeRange.TODDLER -> "Toddler"
        AgeRange.PRESCHOOL -> "Preschool"
        AgeRange.SCHOOL_AGE -> "School Age"
    }
}
