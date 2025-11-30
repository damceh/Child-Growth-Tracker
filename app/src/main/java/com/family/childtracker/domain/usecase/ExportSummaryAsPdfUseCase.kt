package com.family.childtracker.domain.usecase

import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.family.childtracker.domain.model.MonthlySummary
import java.io.File
import java.io.FileOutputStream
import java.time.format.DateTimeFormatter

/**
 * Exports a monthly summary as a PDF document
 */
class ExportSummaryAsPdfUseCase(
    private val context: Context
) {

    suspend operator fun invoke(summary: MonthlySummary): Result<File> {
        return try {
            val pdfDocument = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            // Set up paint for text
            val titlePaint = Paint().apply {
                textSize = 24f
                isFakeBoldText = true
            }

            val headerPaint = Paint().apply {
                textSize = 18f
                isFakeBoldText = true
            }

            val bodyPaint = Paint().apply {
                textSize = 14f
            }

            val smallPaint = Paint().apply {
                textSize = 12f
                color = android.graphics.Color.GRAY
            }

            var yPosition = 50f
            val leftMargin = 50f
            val lineSpacing = 25f

            // Title
            canvas.drawText(
                "Monthly Summary Report",
                leftMargin,
                yPosition,
                titlePaint
            )
            yPosition += lineSpacing * 1.5f

            // Child name and date range
            val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
            canvas.drawText(
                "Child: ${summary.childName}",
                leftMargin,
                yPosition,
                headerPaint
            )
            yPosition += lineSpacing

            canvas.drawText(
                "Period: ${summary.monthStart.format(dateFormatter)} - ${summary.monthEnd.format(dateFormatter)}",
                leftMargin,
                yPosition,
                bodyPaint
            )
            yPosition += lineSpacing * 1.5f

            // Growth Summary
            summary.growthSummary?.let { growth ->
                canvas.drawText("Growth Metrics", leftMargin, yPosition, headerPaint)
                yPosition += lineSpacing

                growth.endHeight?.let { height ->
                    val change = growth.heightChange?.let { " (${if (it >= 0) "+" else ""}%.1f cm)".format(it) } ?: ""
                    val percentile = growth.heightPercentile?.let { " - ${it}th percentile" } ?: ""
                    canvas.drawText(
                        "Height: %.1f cm%s%s".format(height, change, percentile),
                        leftMargin + 20f,
                        yPosition,
                        bodyPaint
                    )
                    yPosition += lineSpacing
                }

                growth.endWeight?.let { weight ->
                    val change = growth.weightChange?.let { " (${if (it >= 0) "+" else ""}%.2f kg)".format(it) } ?: ""
                    val percentile = growth.weightPercentile?.let { " - ${it}th percentile" } ?: ""
                    canvas.drawText(
                        "Weight: %.2f kg%s%s".format(weight, change, percentile),
                        leftMargin + 20f,
                        yPosition,
                        bodyPaint
                    )
                    yPosition += lineSpacing
                }

                growth.endHeadCircumference?.let { headCirc ->
                    val change = growth.headCircumferenceChange?.let { " (${if (it >= 0) "+" else ""}%.1f cm)".format(it) } ?: ""
                    val percentile = growth.headCircumferencePercentile?.let { " - ${it}th percentile" } ?: ""
                    canvas.drawText(
                        "Head Circumference: %.1f cm%s%s".format(headCirc, change, percentile),
                        leftMargin + 20f,
                        yPosition,
                        bodyPaint
                    )
                    yPosition += lineSpacing
                }

                if (growth.hasSignificantChange) {
                    canvas.drawText(
                        "⚠ Significant growth change detected",
                        leftMargin + 20f,
                        yPosition,
                        bodyPaint
                    )
                    yPosition += lineSpacing
                }

                yPosition += lineSpacing * 0.5f
            }

            // Milestones
            if (summary.milestones.isNotEmpty()) {
                canvas.drawText("Milestones Achieved (${summary.milestones.size})", leftMargin, yPosition, headerPaint)
                yPosition += lineSpacing

                summary.milestones.take(10).forEach { milestone ->
                    val text = "• ${milestone.description} (${milestone.achievementDate.format(dateFormatter)})"
                    canvas.drawText(text, leftMargin + 20f, yPosition, bodyPaint)
                    yPosition += lineSpacing
                }

                if (summary.milestones.size > 10) {
                    canvas.drawText(
                        "... and ${summary.milestones.size - 10} more",
                        leftMargin + 20f,
                        yPosition,
                        smallPaint
                    )
                    yPosition += lineSpacing
                }

                yPosition += lineSpacing * 0.5f
            }

            // Behavior Summary
            summary.behaviorSummary?.let { behavior ->
                canvas.drawText("Behavior Summary", leftMargin, yPosition, headerPaint)
                yPosition += lineSpacing

                canvas.drawText(
                    "Total entries: ${behavior.totalEntries}",
                    leftMargin + 20f,
                    yPosition,
                    bodyPaint
                )
                yPosition += lineSpacing

                behavior.averageSleepQuality?.let { sleep ->
                    canvas.drawText(
                        "Average sleep quality: %.1f/5".format(sleep),
                        leftMargin + 20f,
                        yPosition,
                        bodyPaint
                    )
                    yPosition += lineSpacing
                }

                behavior.dominantMood?.let { mood ->
                    canvas.drawText(
                        "Most common mood: ${mood.name.lowercase().replaceFirstChar { it.uppercase() }}",
                        leftMargin + 20f,
                        yPosition,
                        bodyPaint
                    )
                    yPosition += lineSpacing
                }

                behavior.dominantEatingHabits?.let { eating ->
                    canvas.drawText(
                        "Eating habits: ${eating.name.lowercase().replaceFirstChar { it.uppercase() }}",
                        leftMargin + 20f,
                        yPosition,
                        bodyPaint
                    )
                    yPosition += lineSpacing
                }
            }

            // Footer
            yPosition = 800f
            canvas.drawText(
                "Generated on ${summary.generatedAt.toString().substring(0, 10)}",
                leftMargin,
                yPosition,
                smallPaint
            )

            pdfDocument.finishPage(page)

            // Save to file
            val fileName = "summary_${summary.childName.replace(" ", "_")}_${summary.monthStart}.pdf"
            val file = File(context.getExternalFilesDir(null), fileName)
            
            FileOutputStream(file).use { outputStream ->
                pdfDocument.writeTo(outputStream)
            }
            
            pdfDocument.close()

            Result.success(file)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
