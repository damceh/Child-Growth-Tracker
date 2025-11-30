package com.family.childtracker.domain.usecase

import com.family.childtracker.domain.model.BehaviorEntry
import com.family.childtracker.domain.model.EatingHabits
import com.family.childtracker.domain.model.Mood
import com.family.childtracker.domain.repository.BehaviorEntryRepository
import java.time.LocalDate

class UpdateBehaviorEntryUseCase(
    private val repository: BehaviorEntryRepository
) {
    suspend operator fun invoke(
        entry: BehaviorEntry,
        entryDate: LocalDate? = null,
        mood: Mood? = null,
        sleepQuality: Int? = null,
        eatingHabits: EatingHabits? = null,
        notes: String? = null
    ): Result<BehaviorEntry> {
        return try {
            // Validate sleep quality if provided
            if (sleepQuality != null && (sleepQuality < 1 || sleepQuality > 5)) {
                return Result.failure(IllegalArgumentException("Sleep quality must be between 1 and 5"))
            }

            val updatedEntry = entry.copy(
                entryDate = entryDate ?: entry.entryDate,
                mood = mood,
                sleepQuality = sleepQuality,
                eatingHabits = eatingHabits,
                notes = notes?.takeIf { it.isNotBlank() }
            )

            repository.updateBehaviorEntry(updatedEntry)
            Result.success(updatedEntry)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
