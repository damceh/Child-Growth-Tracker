package com.family.childtracker.presentation.growth

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.family.childtracker.domain.model.GrowthRecord
import com.family.childtracker.domain.usecase.CalculateGrowthPercentilesUseCase
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

enum class GrowthMetric {
    HEIGHT, WEIGHT, HEAD_CIRCUMFERENCE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GrowthChartScreen(
    records: List<GrowthRecord>,
    calculatePercentiles: (GrowthRecord) -> CalculateGrowthPercentilesUseCase.PercentileResult?,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedMetric by remember { mutableStateOf(GrowthMetric.HEIGHT) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Growth Trends") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tab navigation
            TabRow(selectedTabIndex = selectedMetric.ordinal) {
                Tab(
                    selected = selectedMetric == GrowthMetric.HEIGHT,
                    onClick = { selectedMetric = GrowthMetric.HEIGHT },
                    text = { Text("Height") }
                )
                Tab(
                    selected = selectedMetric == GrowthMetric.WEIGHT,
                    onClick = { selectedMetric = GrowthMetric.WEIGHT },
                    text = { Text("Weight") }
                )
                Tab(
                    selected = selectedMetric == GrowthMetric.HEAD_CIRCUMFERENCE,
                    onClick = { selectedMetric = GrowthMetric.HEAD_CIRCUMFERENCE },
                    text = { Text("Head Circ.") }
                )
            }

            // Chart content
            if (records.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No growth data available",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                GrowthChart(
                    records = records,
                    metric = selectedMetric,
                    calculatePercentiles = calculatePercentiles,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun GrowthChart(
    records: List<GrowthRecord>,
    metric: GrowthMetric,
    calculatePercentiles: (GrowthRecord) -> CalculateGrowthPercentilesUseCase.PercentileResult?,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary.toArgb()
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface.toArgb()
    val surfaceVariantColor = MaterialTheme.colorScheme.surfaceVariant.toArgb()

    // Filter and sort records by date
    val sortedRecords = remember(records, metric) {
        records
            .filter { record ->
                when (metric) {
                    GrowthMetric.HEIGHT -> record.height != null
                    GrowthMetric.WEIGHT -> record.weight != null
                    GrowthMetric.HEAD_CIRCUMFERENCE -> record.headCircumference != null
                }
            }
            .sortedBy { it.recordDate }
    }

    if (sortedRecords.isEmpty()) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No ${metric.name.lowercase().replace('_', ' ')} data available",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Chart
        AndroidView(
            factory = { context ->
                LineChart(context).apply {
                    description.isEnabled = false
                    setTouchEnabled(true)
                    isDragEnabled = true
                    setScaleEnabled(true)
                    setPinchZoom(true)
                    setDrawGridBackground(false)
                    legend.isEnabled = true

                    // Configure X axis
                    xAxis.apply {
                        position = XAxis.XAxisPosition.BOTTOM
                        setDrawGridLines(true)
                        textColor = onSurfaceColor
                        gridColor = surfaceVariantColor
                    }

                    // Configure left Y axis
                    axisLeft.apply {
                        setDrawGridLines(true)
                        textColor = onSurfaceColor
                        gridColor = surfaceVariantColor
                    }

                    // Disable right Y axis
                    axisRight.isEnabled = false
                }
            },
            update = { chart ->
                val chartData = prepareChartData(
                    records = sortedRecords,
                    metric = metric,
                    primaryColor = primaryColor
                )

                chart.apply {
                    data = chartData
                    xAxis.valueFormatter = DateAxisValueFormatter(sortedRecords)
                    invalidate()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )

        // Statistics
        GrowthStatistics(
            records = sortedRecords,
            metric = metric,
            calculatePercentiles = calculatePercentiles
        )
    }
}

@Composable
fun GrowthStatistics(
    records: List<GrowthRecord>,
    metric: GrowthMetric,
    calculatePercentiles: (GrowthRecord) -> CalculateGrowthPercentilesUseCase.PercentileResult?,
    modifier: Modifier = Modifier
) {
    if (records.isEmpty()) return

    val latestRecord = records.last()
    val latestValue = when (metric) {
        GrowthMetric.HEIGHT -> latestRecord.height
        GrowthMetric.WEIGHT -> latestRecord.weight
        GrowthMetric.HEAD_CIRCUMFERENCE -> latestRecord.headCircumference
    }

    val latestPercentile = calculatePercentiles(latestRecord)?.let { percentiles ->
        when (metric) {
            GrowthMetric.HEIGHT -> percentiles.heightPercentile
            GrowthMetric.WEIGHT -> percentiles.weightPercentile
            GrowthMetric.HEAD_CIRCUMFERENCE -> percentiles.headCircumferencePercentile
        }
    }

    val unit = when (metric) {
        GrowthMetric.HEIGHT -> "cm"
        GrowthMetric.WEIGHT -> "kg"
        GrowthMetric.HEAD_CIRCUMFERENCE -> "cm"
    }

    val growthRate = if (records.size >= 2) {
        val firstRecord = records.first()
        val firstValue = when (metric) {
            GrowthMetric.HEIGHT -> firstRecord.height
            GrowthMetric.WEIGHT -> firstRecord.weight
            GrowthMetric.HEAD_CIRCUMFERENCE -> firstRecord.headCircumference
        }

        if (firstValue != null && latestValue != null) {
            val daysBetween = ChronoUnit.DAYS.between(firstRecord.recordDate, latestRecord.recordDate)
            if (daysBetween > 0) {
                val change = latestValue - firstValue
                val changePerMonth = (change / daysBetween) * 30
                String.format("%.2f $unit/month", changePerMonth)
            } else null
        } else null
    } else null

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Latest Measurement",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = latestValue?.let { "$it $unit" } ?: "N/A",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = latestRecord.recordDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                if (latestPercentile != null) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            text = "${latestPercentile}th\npercentile",
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }

            if (growthRate != null) {
                Divider()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Growth Rate:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = growthRate,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

private fun prepareChartData(
    records: List<GrowthRecord>,
    metric: GrowthMetric,
    primaryColor: Int
): LineData {
    val entries = records.mapIndexedNotNull { index, record ->
        val value = when (metric) {
            GrowthMetric.HEIGHT -> record.height
            GrowthMetric.WEIGHT -> record.weight
            GrowthMetric.HEAD_CIRCUMFERENCE -> record.headCircumference
        }
        value?.let { Entry(index.toFloat(), it) }
    }

    val label = when (metric) {
        GrowthMetric.HEIGHT -> "Height (cm)"
        GrowthMetric.WEIGHT -> "Weight (kg)"
        GrowthMetric.HEAD_CIRCUMFERENCE -> "Head Circumference (cm)"
    }

    val dataSet = LineDataSet(entries, label).apply {
        color = primaryColor
        setCircleColor(primaryColor)
        lineWidth = 2f
        circleRadius = 4f
        setDrawCircleHole(false)
        setDrawValues(true)
        valueTextSize = 10f
        mode = LineDataSet.Mode.CUBIC_BEZIER
    }

    return LineData(dataSet)
}

private class DateAxisValueFormatter(
    private val records: List<GrowthRecord>
) : ValueFormatter() {
    private val dateFormatter = DateTimeFormatter.ofPattern("MMM dd")

    override fun getFormattedValue(value: Float): String {
        val index = value.toInt()
        return if (index >= 0 && index < records.size) {
            records[index].recordDate.format(dateFormatter)
        } else {
            ""
        }
    }
}
