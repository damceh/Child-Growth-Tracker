package com.family.childtracker.data.repository

import com.family.childtracker.data.local.dao.WeeklySummaryDao
import com.family.childtracker.data.local.mapper.toDomain
import com.family.childtracker.data.local.mapper.toEntity
import com.family.childtracker.domain.model.WeeklySummary
import com.family.childtracker.domain.repository.WeeklySummaryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class WeeklySummaryRepositoryImpl(
    private val weeklySummaryDao: WeeklySummaryDao
) : WeeklySummaryRepository {

    override fun getSummariesByChildId(childId: String): Flow<List<WeeklySummary>> {
        return weeklySummaryDao.getSummariesByChildId(childId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getSummaryById(id: String): WeeklySummary? {
        return weeklySummaryDao.getSummaryById(id)?.toDomain()
    }

    override suspend fun getLatestSummaryByChildId(childId: String): WeeklySummary? {
        return weeklySummaryDao.getLatestSummaryByChildId(childId)?.toDomain()
    }

    override suspend fun getSummaryByWeek(childId: String, weekStartDate: LocalDate): WeeklySummary? {
        return weeklySummaryDao.getSummaryByWeek(childId, weekStartDate.toEpochDay())?.toDomain()
    }

    override suspend fun insertSummary(summary: WeeklySummary) {
        weeklySummaryDao.insertSummary(summary.toEntity())
    }

    override suspend fun updateSummary(summary: WeeklySummary) {
        weeklySummaryDao.updateSummary(summary.toEntity())
    }

    override suspend fun deleteSummary(summary: WeeklySummary) {
        weeklySummaryDao.deleteSummary(summary.toEntity())
    }

    override suspend fun deleteSummaryById(id: String) {
        weeklySummaryDao.deleteSummaryById(id)
    }
}
