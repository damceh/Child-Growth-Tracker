package com.family.childtracker.presentation.tips

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.family.childtracker.domain.model.AgeRange
import com.family.childtracker.domain.model.ParentingTip
import com.family.childtracker.domain.model.TipCategory
import com.family.childtracker.domain.usecase.GetAllParentingTipsUseCase
import com.family.childtracker.domain.usecase.GetRandomParentingTipUseCase
import com.family.childtracker.domain.usecase.GetTipsByAgeRangeUseCase
import com.family.childtracker.domain.usecase.GetTipsByCategoryUseCase
import com.family.childtracker.domain.usecase.InitializeParentingTipsUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ParentingTipsUiState(
    val tips: List<ParentingTip> = emptyList(),
    val filteredTips: List<ParentingTip> = emptyList(),
    val selectedCategory: TipCategory? = null,
    val selectedAgeRange: AgeRange? = null,
    val searchQuery: String = "",
    val dailyTip: ParentingTip? = null,
    val favoriteTipIds: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class ParentingTipsViewModel(
    private val getAllTipsUseCase: GetAllParentingTipsUseCase,
    private val getTipsByCategoryUseCase: GetTipsByCategoryUseCase,
    private val getTipsByAgeRangeUseCase: GetTipsByAgeRangeUseCase,
    private val getRandomTipUseCase: GetRandomParentingTipUseCase,
    private val initializeTipsUseCase: InitializeParentingTipsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ParentingTipsUiState())
    val uiState: StateFlow<ParentingTipsUiState> = _uiState.asStateFlow()

    init {
        loadTips()
        loadDailyTip()
    }

    private fun loadTips() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                getAllTipsUseCase().collect { tips ->
                    _uiState.update { state ->
                        state.copy(
                            tips = tips,
                            filteredTips = applyFilters(tips, state.selectedCategory, state.selectedAgeRange, state.searchQuery),
                            isLoading = false,
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun initializeSampleTips() {
        viewModelScope.launch {
            try {
                initializeTipsUseCase()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    private fun loadDailyTip() {
        viewModelScope.launch {
            try {
                val tip = getRandomTipUseCase()
                _uiState.update { it.copy(dailyTip = tip) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun refreshDailyTip() {
        loadDailyTip()
    }

    fun filterByCategory(category: TipCategory?) {
        _uiState.update { state ->
            state.copy(
                selectedCategory = category,
                filteredTips = applyFilters(state.tips, category, state.selectedAgeRange, state.searchQuery)
            )
        }
    }

    fun filterByAgeRange(ageRange: AgeRange?) {
        _uiState.update { state ->
            state.copy(
                selectedAgeRange = ageRange,
                filteredTips = applyFilters(state.tips, state.selectedCategory, ageRange, state.searchQuery)
            )
        }
    }

    fun searchTips(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery = query,
                filteredTips = applyFilters(state.tips, state.selectedCategory, state.selectedAgeRange, query)
            )
        }
    }

    fun toggleFavorite(tipId: String) {
        _uiState.update { state ->
            val newFavorites = if (state.favoriteTipIds.contains(tipId)) {
                state.favoriteTipIds - tipId
            } else {
                state.favoriteTipIds + tipId
            }
            state.copy(favoriteTipIds = newFavorites)
        }
    }

    fun clearFilters() {
        _uiState.update { state ->
            state.copy(
                selectedCategory = null,
                selectedAgeRange = null,
                searchQuery = "",
                filteredTips = state.tips
            )
        }
    }

    private fun applyFilters(
        tips: List<ParentingTip>,
        category: TipCategory?,
        ageRange: AgeRange?,
        searchQuery: String
    ): List<ParentingTip> {
        var filtered = tips

        // Apply category filter
        if (category != null) {
            filtered = filtered.filter { it.category == category }
        }

        // Apply age range filter
        if (ageRange != null) {
            filtered = filtered.filter { it.ageRange == ageRange }
        }

        // Apply search filter
        if (searchQuery.isNotBlank()) {
            val query = searchQuery.lowercase()
            filtered = filtered.filter {
                it.title.lowercase().contains(query) ||
                        it.content.lowercase().contains(query)
            }
        }

        return filtered
    }
}
