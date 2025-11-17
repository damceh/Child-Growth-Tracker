package com.family.childtracker.data.repository

import com.family.childtracker.data.local.dao.MilestoneDao
import com.family.childtracker.data.local.mapper.toDomain
import com.family.childtracker.data.local.mapper.toEntity
import com.family.childtracker.domain.model.Milestone
import com.family.childtracker.domain.model.MilestoneCategory
import com.family.childtracker.domain.repository.MilestoneRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class MilestoneRepositoryImpl(
    private val milestoneDao: MilestoneDao
) : MilestoneRepository {

    override fun getMilestonesByChildId(childId: String): Flow<List<Milestone>> {
        return milestoneDao.getMilestonesByChildId(childId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getMilestonesByCategory(
        childId: String,
        category: MilestoneCategory
    ): Flow<List<Milestone>> {
        return milestoneDao.getMilestonesByCategory(childId, category.name).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getMilestoneById(id: String): Milestone? {
        return milestoneDao.getMilestoneById(id)?.toDomain()
    }

    override suspend fun getLatestMilestoneByChildId(childId: String): Milestone? {
        return milestoneDao.getLatestMilestoneByChildId(childId)?.toDomain()
    }

    override suspend fun getMilestonesByDateRange(
        childId: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): List<Milestone> {
        return milestoneDao.getMilestonesByDateRange(
            childId,
            startDate.toEpochDay(),
            endDate.toEpochDay()
        ).map { it.toDomain() }
    }

    override suspend fun insertMilestone(milestone: Milestone) {
        milestoneDao.insertMilestone(milestone.toEntity())
    }

    override suspend fun updateMilestone(milestone: Milestone) {
        milestoneDao.updateMilestone(milestone.toEntity())
    }

    override suspend fun deleteMilestone(milestone: Milestone) {
        milestoneDao.deleteMilestone(milestone.toEntity())
    }

    override suspend fun deleteMilestoneById(id: String) {
        milestoneDao.deleteMilestoneById(id)
    }
}
