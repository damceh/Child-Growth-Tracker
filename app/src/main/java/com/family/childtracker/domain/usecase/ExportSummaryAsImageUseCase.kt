package com.family.childtracker.domain.usecase

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Color
import com.family.childtracker.domain.model.MonthlySummary
import java.io.File
import java.io.FileOutputStream
import java.time.format.DateTimeFormatter

/**
 * Exports a monthly summary as an image (PNG)
 */
class ExportSummaryAsImageUseCase(
    private val context: Context
) {

    suspend operator fun invoke(summary: MonthlySummary): Result<File> {
        return try {
            // Create bitmap with appropriate size
            val width = 1080
            val height = 1920
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            // Background
            canvas.drawColor(Color.parseColor("#F8F9FA"))

            // Set up paint for text
            val titlePaint = Paint().apply {
                textSize = 60f
                isFakeBoldText = true
                color = Color.parseColor("#1976D2")
                isAntiAlias = true
            }

            val headerPaint = Paint().apply {
                textSize = 48f
                isFakeBoldText = true
                color = Color.parseColor("#424242")
                isAntiAlias = true
            }

            val bodyPaint = Paint().apply {
                textSize = 40f
                color = Color.parseColor("#616161")
                isAntiAlias = true
            }

            val smallPaint = Paint().apply {
                textSize = 32f
                color = Color.parseColor("#9E9E9E")
                isAntiAlias = true
            }

            val accentPaint = Paint().apply {
                textSize = 40f
                color = Color.parseColor("#FF6B6B")
                isFakeBoldText = true
                isAntiAlias = true
            }

            var yPosition = 100f
            val leftMargin = 80f
            val lineSpacing = 60f

            // Title
            canvas.drawText(
                "Monthly Summary",
                leftMargin,
                yPosition,
                titlePaint
            )
            yPosition += lineSpacing * 1.5f

            // Child name and date range
            val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
            canvas.drawText(
                summary.childName,
                leftMargin,
                yPosition,
                headerPaint
            )
            yPosition += lineSpacing

            canvas.drawText(
                "${summary.monthStart.format(dateFormatter)} - ${summary.monthEnd.format(dateFormatter)}",
                leftMargin,
                yPosition,
                bodyPaint
            )
            yPosition += lineSpacing * 2f

            // Growth Summary
            summary.growthSummary?.let { growth ->
                canvas.drawText("📊 Growth", leftMargin, yPosition, headerPaint)
                yPosition += lineSpacing * 1.2f

                growth.endHeight?.let { height ->
                    val change = growth.heightChange?.let { 
                        " (${if (it >= 0) "+" else ""}%.1f cm)".format(it) 
                    } ?: ""
                    val percentile = growth.heightPercentile?.let { " • ${it}th %" } ?: ""
                    canvas.drawText(
                        "Height: %.1f cm%s".format(height, change),
                        leftMargin + 40f,
                        yPosition,
                        bodyPaint
                    )
                    if (percentile.isNotEmpty()) {
                        canvas.drawText(
                            percentile,
                            leftMargin + 40f,
                            yPosition + lineSpacing * 0.7f,
                            smallPaint
                        )
                    }
                    yPosition += lineSpacing * 1.5f
                }

                growth.endWeight?.let { weight ->
                    val change = growth.weightChange?.let { 
                        " (${if (it >= 0) "+" else ""}%.2f kg)".format(it) 
                    } ?: ""
                    val percentile = growth.weightPercentile?.let { " • ${it}th %" } ?: ""
                    canvas.drawText(
                        "Weight: %.2f kg%s".format(weight, change),
                        leftMargin + 40f,
                        yPosition,
                        bodyPaint
                    )
                    if (percentile.isNotEmpty()) {
                        canvas.drawText(
                            percentile,
                            leftMargin + 40f,
                            yPosition + lineSpacing * 0.7f,
                            smallPaint
                        )
                    }
                    yPosition += lineSpacing * 1.5f
                }

                if (growth.hasSignificantChange) {
                    canvas.drawText(
                        "⚠️ Significant change",
                        leftMargin + 40f,
                        yPosition,
                        accentPaint
                    )
                    yPosition += lineSpacing
                }

                yPosition += lineSpacing
            }

            // Milestones
            if (summary.milestones.isNotEmpty()) {
                canvas.drawText(
                    "🎯 Milestones (${summary.milestones.size})",
                    leftMargin,
                    yPosition,
                    headerPaint
                )
                yPosition += lineSpacing * 1.2f

                summary.milestones.take(5).forEach { milestone ->
                    val text = "• ${milestone.description}"
                    canvas.drawText(text, leftMargin + 40f, yPosition, bodyPaint)
                    yPosition += lineSpacing
                }

                if (summary.milestones.size > 5) {
                    canvas.drawText(
                        "... and ${summary.milestones.size - 5} more",
                        leftMargin + 40f,
                        yPosition,
                        smallPaint
                    )
                    yPosition += lineSpacing
                }

                yPosition += lineSpacing
            }

            // Behavior Summary
            summary.behaviorSummary?.let { behavior ->
                canvas.drawText("😊 Behavior", leftMargin, yPosition, headerPaint)
                yPosition += lineSpacing * 1.2f

                behavior.averageSleepQuality?.let { sleep ->
                    canvas.drawText(
                        "Sleep: %.1f/5 ⭐".format(sleep),
                        leftMargin + 40f,
                        yPosition,
                        bodyPaint
                    )
                    yPosition += lineSpacing
                }

                behavior.dominantMood?.let { mood ->
                    canvas.drawText(
                        "Mood: ${mood.name.lowercase().replaceFirstChar { it.uppercase() }}",
                        leftMargin + 40f,
                        yPosition,
                        bodyPaint
                    )
                    yPosition += lineSpacing
                }
            }

            // Footer
            yPosition = height - 100f
            canvas.drawText(
                "Child Growth Tracker",
                leftMargin,
                yPosition,
                smallPaint
            )

            // Save to file
            val fileName = "summary_${summary.childName.replace(" ", "_")}_${summary.monthStart}.png"
            val file = File(context.getExternalFilesDir(null), fileName)
            
            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }

            Result.success(file)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
