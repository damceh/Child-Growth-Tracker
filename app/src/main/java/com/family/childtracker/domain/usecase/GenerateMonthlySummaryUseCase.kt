package com.family.childtracker.domain.usecase

import com.family.childtracker.domain.model.*
import com.family.childtracker.domain.repository.BehaviorEntryRepository
import com.family.childtracker.domain.repository.ChildProfileRepository
import com.family.childtracker.domain.repository.GrowthRecordRepository
import com.family.childtracker.domain.repository.MilestoneRepository
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import kotlin.math.abs

/**
 * Generates a comprehensive monthly summary report for a child
 * including growth metrics, milestones, and behavior patterns
 */
class GenerateMonthlySummaryUseCase(
    private val childProfileRepository: ChildProfileRepository,
    private val growthRecordRepository: GrowthRecordRepository,
    private val milestoneRepository: MilestoneRepository,
    private val behaviorEntryRepository: BehaviorEntryRepository,
    private val calculatePercentilesUseCase: CalculateGrowthPercentilesUseCase
) {

    suspend operator fun invoke(
        childId: String,
        yearMonth: YearMonth = YearMonth.now().minusMonths(1)
    ): Result<MonthlySummary> {
        return try {
            val childProfile = childProfileRepository.getChildProfileById(childId)
                ?: return Result.failure(Exception("Child profile not found"))

            val monthStart = yearMonth.atDay(1)
            val monthEnd = yearMonth.atEndOfMonth()

            // Fetch data for the month
            val growthRecords = growthRecordRepository.getRecordsByDateRange(childId, monthStart, monthEnd)
            val milestones = milestoneRepository.getMilestonesByDateRange(childId, monthStart, monthEnd)
            val behaviorEntries = behaviorEntryRepository.getEntriesByDateRange(childId, monthStart, monthEnd)

            // Generate growth summary
            val growthSummary = generateGrowthSummary(
                childProfile,
                growthRecords,
                monthEnd
            )

            // Generate behavior summary
            val behaviorSummary = generateBehaviorSummary(behaviorEntries)

            val summary = MonthlySummary(
                childId = childId,
                childName = childProfile.name,
                monthStart = monthStart,
                monthEnd = monthEnd,
                growthSummary = growthSummary,
                milestones = milestones,
                behaviorSummary = behaviorSummary,
                generatedAt = Instant.now()
            )

            Result.success(summary)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun generateGrowthSummary(
        childProfile: ChildProfile,
        records: List<GrowthRecord>,
        measurementDate: LocalDate
    ): GrowthSummary? {
        if (records.isEmpty()) return null

        val sortedRecords = records.sortedBy { it.recordDate }
        val firstRecord = sortedRecords.first()
        val lastRecord = sortedRecords.last()

        // Calculate percentiles for the latest record
        val percentiles = calculatePercentilesUseCase(
            childProfile = childProfile,
            height = lastRecord.height,
            weight = lastRecord.weight,
            headCircumference = lastRecord.headCircumference,
            measurementDate = measurementDate
        )

        // Calculate changes
        val heightChange = if (firstRecord.height != null && lastRecord.height != null) {
            lastRecord.height!! - firstRecord.height!!
        } else null

        val weightChange = if (firstRecord.weight != null && lastRecord.weight != null) {
            lastRecord.weight!! - firstRecord.weight!!
        } else null

        val headCircChange = if (firstRecord.headCircumference != null && lastRecord.headCircumference != null) {
            lastRecord.headCircumference!! - firstRecord.headCircumference!!
        } else null

        // Determine if there's a significant change (more than 2 standard deviations)
        val hasSignificantChange = (heightChange != null && abs(heightChange) > 2.0) ||
                (weightChange != null && abs(weightChange) > 0.5) ||
                (headCircChange != null && abs(headCircChange) > 1.0)

        return GrowthSummary(
            startHeight = firstRecord.height,
            endHeight = lastRecord.height,
            heightChange = heightChange,
            heightPercentile = percentiles.heightPercentile,
            startWeight = firstRecord.weight,
            endWeight = lastRecord.weight,
            weightChange = weightChange,
            weightPercentile = percentiles.weightPercentile,
            startHeadCircumference = firstRecord.headCircumference,
            endHeadCircumference = lastRecord.headCircumference,
            headCircumferenceChange = headCircChange,
            headCircumferencePercentile = percentiles.headCircumferencePercentile,
            hasSignificantChange = hasSignificantChange
        )
    }

    private fun generateBehaviorSummary(entries: List<BehaviorEntry>): BehaviorSummary? {
        if (entries.isEmpty()) return null

        val averageSleep = entries.mapNotNull { it.sleepQuality }.average().toFloat()
        
        val dominantMood = entries.mapNotNull { it.mood }
            .groupingBy { it }
            .eachCount()
            .maxByOrNull { it.value }?.key

        val dominantEating = entries.mapNotNull { it.eatingHabits }
            .groupingBy { it }
            .eachCount()
            .maxByOrNull { it.value }?.key

        val positiveNotes = entries
            .mapNotNull { it.notes }
            .filter { it.isNotBlank() }
            .take(5) // Limit to 5 most recent notes

        return BehaviorSummary(
            totalEntries = entries.size,
            averageSleepQuality = if (averageSleep.isNaN()) null else averageSleep,
            dominantMood = dominantMood,
            dominantEatingHabits = dominantEating,
            positiveNotes = positiveNotes
        )
    }
}
