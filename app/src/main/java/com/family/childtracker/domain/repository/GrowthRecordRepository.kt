package com.family.childtracker.domain.repository

import com.family.childtracker.domain.model.GrowthRecord
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface GrowthRecordRepository {
    fun getRecordsByChildId(childId: String): Flow<List<GrowthRecord>>
    suspend fun getRecordById(id: String): GrowthRecord?
    suspend fun getLatestRecordByChildId(childId: String): GrowthRecord?
    suspend fun getRecordsByDateRange(childId: String, startDate: LocalDate, endDate: LocalDate): List<GrowthRecord>
    suspend fun insertRecord(record: GrowthRecord)
    suspend fun updateRecord(record: GrowthRecord)
    suspend fun deleteRecord(record: GrowthRecord)
    suspend fun deleteRecordById(id: String)
}
