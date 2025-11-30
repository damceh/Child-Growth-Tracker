package com.family.childtracker.domain.usecase

import com.family.childtracker.data.remote.model.Message
import com.family.childtracker.data.remote.util.ApiResult
import com.family.childtracker.domain.model.WeeklySummary
import com.family.childtracker.domain.repository.*
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.util.UUID

/**
 * Use case for generating weekly summaries using AI
 */
class GenerateWeeklySummaryUseCase(
    private val weeklySummaryRepository: WeeklySummaryRepository,
    private val openRouterRepository: OpenRouterRepository,
    private val appSettingsRepository: AppSettingsRepository,
    private val childProfileRepository: ChildProfileRepository,
    private val growthRecordRepository: GrowthRecordRepository,
    private val milestoneRepository: MilestoneRepository,
    private val behaviorEntryRepository: BehaviorEntryRepository
) {
    
    companion object {
        private const val DEFAULT_MODEL = "openai/gpt-3.5-turbo"
        private const val SUMMARY_MAX_TOKENS = 500
    }
    
    suspend operator fun invoke(childId: String, weekStartDate: LocalDate? = null): Result {
        // Check if API key is configured
        val settings = appSettingsRepository.getSettingsOnce()
        if (settings?.openRouterApiKey.isNullOrBlank()) {
            return Result.Error("API key not configured. Please configure your OpenRouter API key in Settings.")
        }
        
        // Get child profile
        val child = childProfileRepository.getProfileById(childId)
            ?: return Result.Error("Child profile not found")
        
        // Calculate week range (default to last 7 days)
        val endDate = weekStartDate?.plusDays(6) ?: LocalDate.now()
        val startDate = weekStartDate ?: endDate.minusDays(6)
        
        // Check if summary already exists for this week
        val existingSummary = weeklySummaryRepository.getSummaryByWeek(childId, startDate)
        if (existingSummary != null) {
            return Result.AlreadyExists(existingSummary)
        }
        
        // Aggregate data for the week
        val growthRecords = growthRecordRepository.getRecordsByDateRange(childId, startDate, endDate)
        val milestones = milestoneRepository.getMilestonesByDateRange(childId, startDate, endDate)
        val behaviorEntries = behaviorEntryRepository.getEntriesByDateRange(childId, startDate, endDate)
        
        // Build prompt
        val prompt = buildSummaryPrompt(child.name, child.dateOfBirth, growthRecords, milestones, behaviorEntries, startDate, endDate)
        
        // Get selected model or use default
        val model = settings.selectedModel?.takeIf { it.isNotBlank() } ?: DEFAULT_MODEL
        
        // Generate summary using OpenRouter API
        return when (val apiResult = openRouterRepository.chatCompletion(
            model = model,
            messages = listOf(Message(role = "user", content = prompt)),
            temperature = 0.7f,
            maxTokens = SUMMARY_MAX_TOKENS
        )) {
            is ApiResult.Success -> {
                // Save summary
                val summary = WeeklySummary(
                    id = UUID.randomUUID().toString(),
                    childId = childId,
                    weekStartDate = startDate,
                    weekEndDate = endDate,
                    summaryContent = apiResult.data,
                    generatedAt = Instant.now()
                )
                weeklySummaryRepository.insertSummary(summary)
                
                Result.Success(summary)
            }
            is ApiResult.Error -> {
                Result.Error(apiResult.exception.message)
            }
        }
    }
    
    private fun buildSummaryPrompt(
        childName: String,
        dateOfBirth: LocalDate,
        growthRecords: List<com.family.childtracker.domain.model.GrowthRecord>,
        milestones: List<com.family.childtracker.domain.model.Milestone>,
        behaviorEntries: List<com.family.childtracker.domain.model.BehaviorEntry>,
        startDate: LocalDate,
        endDate: LocalDate
    ): String {
        // Calculate child's age
        val age = Period.between(dateOfBirth, endDate)
        val ageDescription = when {
            age.years > 0 -> "${age.years} year${if (age.years > 1) "s" else ""} and ${age.months} month${if (age.months != 1) "s" else ""}"
            age.months > 0 -> "${age.months} month${if (age.months != 1) "s" else ""}"
            else -> "${age.days} day${if (age.days != 1) "s" else ""}"
        }
        
        val promptBuilder = StringBuilder()
        promptBuilder.append("Generate a warm, encouraging weekly summary for $childName ($ageDescription old).\n\n")
        promptBuilder.append("Week: ${startDate} to ${endDate}\n\n")
        
        // Growth data
        if (growthRecords.isNotEmpty()) {
            promptBuilder.append("Growth Records:\n")
            growthRecords.forEach { record ->
                promptBuilder.append("- ${record.recordDate}: ")
                val measurements = mutableListOf<String>()
                record.height?.let { measurements.add("Height: ${it}cm") }
                record.weight?.let { measurements.add("Weight: ${it}kg") }
                record.headCircumference?.let { measurements.add("Head: ${it}cm") }
                promptBuilder.append(measurements.joinToString(", "))
                record.notes?.let { promptBuilder.append(" (Note: $it)") }
                promptBuilder.append("\n")
            }
            promptBuilder.append("\n")
        } else {
            promptBuilder.append("Growth Records: No measurements recorded this week\n\n")
        }
        
        // Milestones
        if (milestones.isNotEmpty()) {
            promptBuilder.append("New Milestones:\n")
            milestones.forEach { milestone ->
                promptBuilder.append("- ${milestone.description} (${milestone.category}, ${milestone.achievementDate})")
                milestone.notes?.let { promptBuilder.append(" - $it") }
                promptBuilder.append("\n")
            }
            promptBuilder.append("\n")
        } else {
            promptBuilder.append("New Milestones: No new milestones recorded this week\n\n")
        }
        
        // Positive behaviors
        val positiveBehaviors = behaviorEntries.filter { entry ->
            entry.notes?.isNotBlank() == true || 
            entry.sleepQuality?.let { it >= 4 } == true
        }
        
        if (positiveBehaviors.isNotEmpty()) {
            promptBuilder.append("Positive Behaviors:\n")
            positiveBehaviors.forEach { entry ->
                promptBuilder.append("- ${entry.entryDate}: ")
                val details = mutableListOf<String>()
                entry.mood?.let { details.add("Mood: ${it.name.lowercase()}") }
                entry.sleepQuality?.let { details.add("Sleep: $it/5") }
                entry.eatingHabits?.let { details.add("Eating: ${it.name.lowercase()}") }
                promptBuilder.append(details.joinToString(", "))
                entry.notes?.let { promptBuilder.append(" - $it") }
                promptBuilder.append("\n")
            }
            promptBuilder.append("\n")
        }
        
        promptBuilder.append("""
            Create a summary that:
            1. Highlights key developments and changes
            2. Celebrates achievements with enthusiasm
            3. Provides 1-2 actionable, age-appropriate parenting tips
            4. Maintains an encouraging, supportive tone
            5. Acknowledges if it was a quiet week with no major changes
            
            Keep it concise (200-300 words). Write in a warm, conversational style as if speaking directly to the parent.
        """.trimIndent())
        
        return promptBuilder.toString()
    }
    
    sealed class Result {
        data class Success(val summary: WeeklySummary) : Result()
        data class AlreadyExists(val summary: WeeklySummary) : Result()
        data class Error(val message: String) : Result()
    }
}
