package com.family.childtracker.data.repository

import com.family.childtracker.data.local.dao.GrowthRecordDao
import com.family.childtracker.data.local.mapper.toDomain
import com.family.childtracker.data.local.mapper.toEntity
import com.family.childtracker.domain.model.GrowthRecord
import com.family.childtracker.domain.repository.GrowthRecordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class GrowthRecordRepositoryImpl(
    private val growthRecordDao: GrowthRecordDao
) : GrowthRecordRepository {

    override fun getRecordsByChildId(childId: String): Flow<List<GrowthRecord>> {
        return growthRecordDao.getRecordsByChildId(childId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getRecordById(id: String): GrowthRecord? {
        return growthRecordDao.getRecordById(id)?.toDomain()
    }

    override suspend fun getLatestRecordByChildId(childId: String): GrowthRecord? {
        return growthRecordDao.getLatestRecordByChildId(childId)?.toDomain()
    }

    override suspend fun getRecordsByDateRange(
        childId: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<GrowthRecord> {
        return growthRecordDao.getRecordsByDateRange(
            childId,
            startDate.toEpochDay(),
            endDate.toEpochDay()
        ).map { it.toDomain() }
    }

    override suspend fun insertRecord(record: GrowthRecord) {
        growthRecordDao.insertRecord(record.toEntity())
    }

    override suspend fun updateRecord(record: GrowthRecord) {
        growthRecordDao.updateRecord(record.toEntity())
    }

    override suspend fun deleteRecord(record: GrowthRecord) {
        growthRecordDao.deleteRecord(record.toEntity())
    }

    override suspend fun deleteRecordById(id: String) {
        growthRecordDao.deleteRecordById(id)
    }
}
