package com.family.childtracker.data.repository

import com.family.childtracker.data.local.dao.BehaviorEntryDao
import com.family.childtracker.data.local.mapper.toDomain
import com.family.childtracker.data.local.mapper.toEntity
import com.family.childtracker.domain.model.BehaviorEntry
import com.family.childtracker.domain.repository.BehaviorEntryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class BehaviorEntryRepositoryImpl(
    private val behaviorEntryDao: BehaviorEntryDao
) : BehaviorEntryRepository {

    override fun getEntriesByChildId(childId: String): Flow<List<BehaviorEntry>> {
        return behaviorEntryDao.getEntriesByChildId(childId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getEntryById(id: String): BehaviorEntry? {
        return behaviorEntryDao.getEntryById(id)?.toDomain()
    }

    override suspend fun getEntryByDate(childId: String, date: LocalDate): BehaviorEntry? {
        return behaviorEntryDao.getEntryByDate(childId, date.toEpochDay())?.toDomain()
    }

    override suspend fun getEntriesByDateRange(
        childId: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<BehaviorEntry> {
        return behaviorEntryDao.getEntriesByDateRange(
            childId,
            startDate.toEpochDay(),
            endDate.toEpochDay()
        ).map { it.toDomain() }
    }

    override suspend fun insertEntry(entry: BehaviorEntry) {
        behaviorEntryDao.insertEntry(entry.toEntity())
    }

    override suspend fun updateEntry(entry: BehaviorEntry) {
        behaviorEntryDao.updateEntry(entry.toEntity())
    }

    override suspend fun deleteEntry(entry: BehaviorEntry) {
        behaviorEntryDao.deleteEntry(entry.toEntity())
    }

    override suspend fun deleteEntryById(id: String) {
        behaviorEntryDao.deleteEntryById(id)
    }
}
