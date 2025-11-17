package com.family.childtracker.domain.repository

import com.family.childtracker.domain.model.Milestone
import com.family.childtracker.domain.model.MilestoneCategory
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface MilestoneRepository {
    fun getMilestonesByChildId(childId: String): Flow<List<Milestone>>
    fun getMilestonesByCategory(childId: String, category: MilestoneCategory): Flow<List<Milestone>>
    suspend fun getMilestoneById(id: String): Milestone?
    suspend fun getLatestMilestoneByChildId(childId: String): Milestone?
    suspend fun getMilestonesByDateRange(childId: String, startDate: LocalDate, endDate: LocalDate): List<Milestone>
    suspend fun insertMilestone(milestone: Milestone)
    suspend fun updateMilestone(milestone: Milestone)
    suspend fun deleteMilestone(milestone: Milestone)
    suspend fun deleteMilestoneById(id: String)
}
