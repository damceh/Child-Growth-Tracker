package com.family.childtracker.domain.usecase

import com.family.childtracker.domain.model.*
import com.family.childtracker.domain.repository.BehaviorEntryRepository
import com.family.childtracker.domain.repository.ChildProfileRepository
import com.family.childtracker.domain.repository.GrowthRecordRepository
import com.family.childtracker.domain.repository.MilestoneRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth

class GenerateMonthlySummaryUseCaseTest {

    private lateinit var childProfileRepository: ChildProfileRepository
    private lateinit var growthRecordRepository: GrowthRecordRepository
    private lateinit var milestoneRepository: MilestoneRepository
    private lateinit var behaviorEntryRepository: BehaviorEntryRepository
    private lateinit var calculatePercentilesUseCase: CalculateGrowthPercentilesUseCase
    private lateinit var useCase: GenerateMonthlySummaryUseCase

    @Before
    fun setup() {
        childProfileRepository = mock(ChildProfileRepository::class.java)
        growthRecordRepository = mock(GrowthRecordRepository::class.java)
        milestoneRepository = mock(MilestoneRepository::class.java)
        behaviorEntryRepository = mock(BehaviorEntryRepository::class.java)
        calculatePercentilesUseCase = CalculateGrowthPercentilesUseCase()

        useCase = GenerateMonthlySummaryUseCase(
            childProfileRepository,
            growthRecordRepository,
            milestoneRepository,
            behaviorEntryRepository,
            calculatePercentilesUseCase
        )
    }

    @Test
    fun `generate summary with growth data calculates changes correctly`() = runBlocking {
        // Given
        val childId = "child1"
        val yearMonth = YearMonth.of(2024, 11)
        val monthStart = yearMonth.atDay(1)
        val monthEnd = yearMonth.atEndOfMonth()

        val childProfile = ChildProfile(
            id = childId,
            name = "Test Child",
            dateOfBirth = LocalDate.of(2023, 1, 1),
            gender = Gender.MALE,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        val growthRecords = listOf(
            GrowthRecord(
                id = "g1",
                childId = childId,
                recordDate = monthStart,
                height = 80.0f,
                weight = 10.0f,
                headCircumference = 45.0f,
                notes = null,
                createdAt = Instant.now()
            ),
            GrowthRecord(
                id = "g2",
                childId = childId,
                recordDate = monthEnd,
                height = 81.5f,
                weight = 10.3f,
                headCircumference = 45.5f,
                notes = null,
                createdAt = Instant.now()
            )
        )

        `when`(childProfileRepository.getChildProfileById(childId)).thenReturn(childProfile)
        `when`(growthRecordRepository.getRecordsByDateRange(childId, monthStart, monthEnd))
            .thenReturn(growthRecords)
        `when`(milestoneRepository.getMilestonesByDateRange(childId, monthStart, monthEnd))
            .thenReturn(emptyList())
        `when`(behaviorEntryRepository.getEntriesByDateRange(childId, monthStart, monthEnd))
            .thenReturn(emptyList())

        // When
        val result = useCase(childId, yearMonth)

        // Then
        assertTrue(result.isSuccess)
        val summary = result.getOrNull()!!
        
        assertEquals("Test Child", summary.childName)
        assertEquals(monthStart, summary.monthStart)
        assertEquals(monthEnd, summary.monthEnd)
        
        val growthSummary = summary.growthSummary!!
        assertEquals(80.0f, growthSummary.startHeight)
        assertEquals(81.5f, growthSummary.endHeight)
        assertEquals(1.5f, growthSummary.heightChange!!, 0.01f)
        
        assertEquals(10.0f, growthSummary.startWeight)
        assertEquals(10.3f, growthSummary.endWeight)
        assertEquals(0.3f, growthSummary.weightChange!!, 0.01f)
        
        assertEquals(45.0f, growthSummary.startHeadCircumference)
        assertEquals(45.5f, growthSummary.endHeadCircumference)
        assertEquals(0.5f, growthSummary.headCircumferenceChange!!, 0.01f)
    }

    @Test
    fun `generate summary detects significant changes`() = runBlocking {
        // Given
        val childId = "child1"
        val yearMonth = YearMonth.of(2024, 11)
        val monthStart = yearMonth.atDay(1)
        val monthEnd = yearMonth.atEndOfMonth()

        val childProfile = ChildProfile(
            id = childId,
            name = "Test Child",
            dateOfBirth = LocalDate.of(2023, 1, 1),
            gender = Gender.MALE,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        // Significant height change (>2cm)
        val growthRecords = listOf(
            GrowthRecord(
                id = "g1",
                childId = childId,
                recordDate = monthStart,
                height = 80.0f,
                weight = 10.0f,
                headCircumference = 45.0f,
                notes = null,
                createdAt = Instant.now()
            ),
            GrowthRecord(
                id = "g2",
                childId = childId,
                recordDate = monthEnd,
                height = 83.0f, // +3cm - significant!
                weight = 10.2f,
                headCircumference = 45.3f,
                notes = null,
                createdAt = Instant.now()
            )
        )

        `when`(childProfileRepository.getChildProfileById(childId)).thenReturn(childProfile)
        `when`(growthRecordRepository.getRecordsByDateRange(childId, monthStart, monthEnd))
            .thenReturn(growthRecords)
        `when`(milestoneRepository.getMilestonesByDateRange(childId, monthStart, monthEnd))
            .thenReturn(emptyList())
        `when`(behaviorEntryRepository.getEntriesByDateRange(childId, monthStart, monthEnd))
            .thenReturn(emptyList())

        // When
        val result = useCase(childId, yearMonth)

        // Then
        assertTrue(result.isSuccess)
        val summary = result.getOrNull()!!
        val growthSummary = summary.growthSummary!!
        
        assertTrue(growthSummary.hasSignificantChange)
    }

    @Test
    fun `generate summary with behavior data calculates averages`() = runBlocking {
        // Given
        val childId = "child1"
        val yearMonth = YearMonth.of(2024, 11)
        val monthStart = yearMonth.atDay(1)
        val monthEnd = yearMonth.atEndOfMonth()

        val childProfile = ChildProfile(
            id = childId,
            name = "Test Child",
            dateOfBirth = LocalDate.of(2023, 1, 1),
            gender = Gender.MALE,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )

        val behaviorEntries = listOf(
            BehaviorEntry(
                id = "b1",
                childId = childId,
                entryDate = monthStart,
                mood = Mood.HAPPY,
                sleepQuality = 4,
                eatingHabits = EatingHabits.GOOD,
                notes = "Great day!",
                createdAt = Instant.now()
            ),
            BehaviorEntry(
                id = "b2",
                childId = childId,
                entryDate = monthStart.plusDays(1),
                mood = Mood.HAPPY,
                sleepQuality = 5,
                eatingHabits = EatingHabits.EXCELLENT,
                notes = "Another great day!",
                createdAt = Instant.now()
            ),
            BehaviorEntry(
                id = "b3",
                childId = childId,
                entryDate = monthStart.plusDays(2),
                mood = Mood.CALM,
                sleepQuality = 3,
                eatingHabits = EatingHabits.GOOD,
                notes = "Calm day",
                createdAt = Instant.now()
            )
        )

        `when`(childProfileRepository.getChildProfileById(childId)).thenReturn(childProfile)
        `when`(growthRecordRepository.getRecordsByDateRange(childId, monthStart, monthEnd))
            .thenReturn(emptyList())
        `when`(milestoneRepository.getMilestonesByDateRange(childId, monthStart, monthEnd))
            .thenReturn(emptyList())
        `when`(behaviorEntryRepository.getEntriesByDateRange(childId, monthStart, monthEnd))
            .thenReturn(behaviorEntries)

        // When
        val result = useCase(childId, yearMonth)

        // Then
        assertTrue(result.isSuccess)
        val summary = result.getOrNull()!!
        val behaviorSummary = summary.behaviorSummary!!
        
        assertEquals(3, behaviorSummary.totalEntries)
        assertEquals(4.0f, behaviorSummary.averageSleepQuality!!, 0.01f) // (4+5+3)/3 = 4
        assertEquals(Mood.HAPPY, behaviorSummary.dominantMood) // HAPPY appears twice
        assertEquals(EatingHabits.GOOD, behaviorSummary.dominantEatingHabits) // GOOD appears twice
    }

    @Test
    fun `generate summary returns error when child not found`() = runBlocking {
        // Given
        val childId = "nonexistent"
        val yearMonth = YearMonth.of(2024, 11)

        `when`(childProfileRepository.getChildProfileById(childId)).thenReturn(null)

        // When
        val result = useCase(childId, yearMonth)

        // Then
        assertTrue(result.isFailure)
        assertEquals("Child profile not found", result.exceptionOrNull()?.message)
    }
}
