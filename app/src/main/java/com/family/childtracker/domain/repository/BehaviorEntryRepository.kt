package com.family.childtracker.domain.repository

import com.family.childtracker.domain.model.BehaviorEntry
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface BehaviorEntryRepository {
    fun getEntriesByChildId(childId: String): Flow<List<BehaviorEntry>>
    suspend fun getEntryById(id: String): BehaviorEntry?
    suspend fun getEntryByDate(childId: String, date: LocalDate): BehaviorEntry?
    suspend fun getEntriesByDateRange(childId: String, startDate: LocalDate, endDate: LocalDate): List<BehaviorEntry>
    suspend fun insertEntry(entry: BehaviorEntry)
    suspend fun updateEntry(entry: BehaviorEntry)
    suspend fun deleteEntry(entry: BehaviorEntry)
    suspend fun deleteEntryById(id: String)
}
