package com.family.childtracker.domain.repository

import com.family.childtracker.domain.model.WeeklySummary
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface WeeklySummaryRepository {
    fun getSummariesByChildId(childId: String): Flow<List<WeeklySummary>>
    suspend fun getSummaryById(id: String): WeeklySummary?
    suspend fun getLatestSummaryByChildId(childId: String): WeeklySummary?
    suspend fun getSummaryByWeek(childId: String, weekStartDate: LocalDate): WeeklySummary?
    suspend fun insertSummary(summary: WeeklySummary)
    suspend fun updateSummary(summary: WeeklySummary)
    suspend fun deleteSummary(summary: WeeklySummary)
    suspend fun deleteSummaryById(id: String)
}
