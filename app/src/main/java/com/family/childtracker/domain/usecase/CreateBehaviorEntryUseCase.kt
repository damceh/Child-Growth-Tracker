package com.family.childtracker.domain.usecase

import com.family.childtracker.domain.model.BehaviorEntry
import com.family.childtracker.domain.model.EatingHabits
import com.family.childtracker.domain.model.Mood
import com.family.childtracker.domain.repository.BehaviorEntryRepository
import java.time.Instant
import java.time.LocalDate
import java.util.UUID

class CreateBehaviorEntryUseCase(
    private val repository: BehaviorEntryRepository
) {
    suspend operator fun invoke(
        childId: String,
        entryDate: LocalDate,
        mood: Mood?,
        sleepQuality: Int?,
        eatingHabits: EatingHabits?,
        notes: String?
    ): Result<BehaviorEntry> {
        return try {
            // Validate sleep quality if provided
            if (sleepQuality != null && (sleepQuality < 1 || sleepQuality > 5)) {
                return Result.failure(IllegalArgumentException("Sleep quality must be between 1 and 5"))
            }

            val entry = BehaviorEntry(
                id = UUID.randomUUID().toString(),
                childId = childId,
                entryDate = entryDate,
                mood = mood,
                sleepQuality = sleepQuality,
                eatingHabits = eatingHabits,
                notes = notes?.takeIf { it.isNotBlank() },
                createdAt = Instant.now()
            )

            repository.insertBehaviorEntry(entry)
            Result.success(entry)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
