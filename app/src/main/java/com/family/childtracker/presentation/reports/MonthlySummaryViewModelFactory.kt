package com.family.childtracker.presentation.reports

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.family.childtracker.data.local.database.DatabaseProvider
import com.family.childtracker.data.repository.*
import com.family.childtracker.domain.usecase.*

class MonthlySummaryViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MonthlySummaryViewModel::class.java)) {
            val database = DatabaseProvider.getDatabase(context)

            val childProfileRepository = ChildProfileRepositoryImpl(database.childProfileDao())
            val growthRecordRepository = GrowthRecordRepositoryImpl(database.growthRecordDao())
            val milestoneRepository = MilestoneRepositoryImpl(database.milestoneDao())
            val behaviorEntryRepository = BehaviorEntryRepositoryImpl(database.behaviorEntryDao())

            val calculatePercentilesUseCase = CalculateGrowthPercentilesUseCase()

            val generateMonthlySummaryUseCase = GenerateMonthlySummaryUseCase(
                childProfileRepository = childProfileRepository,
                growthRecordRepository = growthRecordRepository,
                milestoneRepository = milestoneRepository,
                behaviorEntryRepository = behaviorEntryRepository,
                calculatePercentilesUseCase = calculatePercentilesUseCase
            )

            val exportSummaryAsPdfUseCase = ExportSummaryAsPdfUseCase(context)
            val exportSummaryAsImageUseCase = ExportSummaryAsImageUseCase(context)
            val shareSummaryUseCase = ShareSummaryUseCase(context)

            return MonthlySummaryViewModel(
                context = context,
                generateMonthlySummaryUseCase = generateMonthlySummaryUseCase,
                exportSummaryAsPdfUseCase = exportSummaryAsPdfUseCase,
                exportSummaryAsImageUseCase = exportSummaryAsImageUseCase,
                shareSummaryUseCase = shareSummaryUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
