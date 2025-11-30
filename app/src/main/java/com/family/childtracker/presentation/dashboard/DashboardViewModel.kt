package com.family.childtracker.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.family.childtracker.domain.model.*
import com.family.childtracker.domain.repository.*
import com.family.childtracker.domain.usecase.GetRandomParentingTipUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class DashboardUiState(
    val selectedProfile: ChildProfile? = null,
    val profiles: List<ChildProfile> = emptyList(),
    val latestGrowth: GrowthRecord? = null,
    val latestMilestone: Milestone? = null,
    val recentBehaviorSummary: String? = null,
    val dailyTip: ParentingTip? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class DashboardViewModel(
    private val childProfileRepository: ChildProfileRepository,
    private val growthRecordRepository: GrowthRecordRepository,
    private val milestoneRepository: MilestoneRepository,
    private val behaviorEntryRepository: BehaviorEntryRepository,
    private val getRandomParentingTipUseCase: GetRandomParentingTipUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val _selectedProfileId = MutableStateFlow<String?>(null)

    init {
        loadProfiles()
        loadDailyTip()
    }

    private fun loadProfiles() {
        viewModelScope.launch {
            childProfileRepository.getAllProfiles()
                .catch { e ->
                    _uiState.update { it.copy(error = e.message) }
                }
                .collect { profiles ->
                    _uiState.update { it.copy(profiles = profiles) }
                    
                    // Auto-select first profile if none selected
                    if (_selectedProfileId.value == null && profiles.isNotEmpty()) {
                        selectProfile(profiles.first())
                    }
                }
        }
    }

    fun selectProfile(profile: ChildProfile) {
        _selectedProfileId.value = profile.id
        _uiState.update { it.copy(selectedProfile = profile) }
        loadProfileData(profile.id)
    }

    private fun loadProfileData(profileId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                // Load latest growth record
                growthRecordRepository.getGrowthRecordsByChildId(profileId)
                    .catch { e ->
                        _uiState.update { it.copy(error = e.message) }
                    }
                    .collect { records ->
                        val latest = records.maxByOrNull { it.recordDate }
                        _uiState.update { it.copy(latestGrowth = latest) }
                    }

                // Load latest milestone
                milestoneRepository.getMilestonesByChildId(profileId)
                    .catch { e ->
                        _uiState.update { it.copy(error = e.message) }
                    }
                    .collect { milestones ->
                        val latest = milestones.maxByOrNull { it.achievementDate }
                        _uiState.update { it.copy(latestMilestone = latest) }
                    }

                // Load recent behavior summary
                behaviorEntryRepository.getBehaviorEntriesByChildId(profileId)
                    .catch { e ->
                        _uiState.update { it.copy(error = e.message) }
                    }
                    .collect { entries ->
                        val summary = generateBehaviorSummary(entries)
                        _uiState.update { it.copy(recentBehaviorSummary = summary) }
                    }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun loadDailyTip() {
        viewModelScope.launch {
            try {
                val tip = getRandomParentingTipUseCase()
                _uiState.update { it.copy(dailyTip = tip) }
            } catch (e: Exception) {
                // Silently fail for tips - not critical
            }
        }
    }

    private fun generateBehaviorSummary(entries: List<BehaviorEntry>): String {
        if (entries.isEmpty()) return "No behavior entries this week"

        // Get entries from the last 7 days
        val now = java.time.LocalDate.now()
        val weekAgo = now.minusDays(7)
        val recentEntries = entries.filter { it.entryDate.isAfter(weekAgo) || it.entryDate.isEqual(weekAgo) }

        if (recentEntries.isEmpty()) return "No behavior entries this week"

        // Calculate mood summary
        val moodCounts = recentEntries.mapNotNull { it.mood }.groupingBy { it }.eachCount()
        val dominantMood = moodCounts.maxByOrNull { it.value }?.key

        // Calculate average sleep quality
        val sleepScores = recentEntries.mapNotNull { it.sleepQuality }
        val avgSleep = if (sleepScores.isNotEmpty()) {
            sleepScores.average()
        } else null

        val moodText = when (dominantMood) {
            Mood.HAPPY -> "Mostly happy"
            Mood.CALM -> "Mostly calm"
            Mood.FUSSY -> "A bit fussy"
            Mood.CRANKY -> "Somewhat cranky"
            Mood.ENERGETIC -> "Very energetic"
            null -> "Mixed moods"
        }

        val sleepText = avgSleep?.let { avg ->
            when {
                avg >= 4.0 -> "Good sleep"
                avg >= 3.0 -> "Fair sleep"
                else -> "Poor sleep"
            }
        } ?: ""

        return if (sleepText.isNotEmpty()) {
            "This week: $moodText\n$sleepText"
        } else {
            "This week: $moodText"
        }
    }

    fun refreshData() {
        _selectedProfileId.value?.let { profileId ->
            loadProfileData(profileId)
        }
        loadDailyTip()
    }
}
