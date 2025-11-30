package com.family.childtracker.domain.usecase

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File

/**
 * Shares a summary file (PDF or image) using Android's share sheet
 */
class ShareSummaryUseCase(
    private val context: Context
) {

    operator fun invoke(file: File, mimeType: String = "application/pdf"): Result<Intent> {
        return try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Monthly Summary Report")
                putExtra(Intent.EXTRA_TEXT, "Here's the monthly summary report from Child Growth Tracker")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            val chooserIntent = Intent.createChooser(shareIntent, "Share Summary")
            chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            Result.success(chooserIntent)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
