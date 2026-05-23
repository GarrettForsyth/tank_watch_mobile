package com.garrettforsyth.tankwatch.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.garrettforsyth.tankwatch.R
import com.garrettforsyth.tankwatch.data.model.MeasurementType
import com.garrettforsyth.tankwatch.data.model.Reading
import com.garrettforsyth.tankwatch.data.model.ReadingRange
import com.garrettforsyth.tankwatch.data.model.SensorConfig
import com.garrettforsyth.tankwatch.ui.cards.StatCard
import com.garrettforsyth.tankwatch.ui.components.SensorStatusBadge
import com.garrettforsyth.tankwatch.ui.components.deriveSensorStatus
import com.garrettforsyth.tankwatch.ui.model.StatData
import com.garrettforsyth.tankwatch.ui.theme.Dimensions
import com.garrettforsyth.tankwatch.ui.theme.Dimensions.Opacity
import com.garrettforsyth.tankwatch.ui.theme.Dimensions.Padding
import com.garrettforsyth.tankwatch.ui.theme.TankWatchTheme
import com.garrettforsyth.tankwatch.ui.util.PreviewData
import com.garrettforsyth.tankwatch.ui.util.ValueFormatter
import com.garrettforsyth.tankwatch.viewmodel.hilt.SensorDetailUiState
import com.garrettforsyth.tankwatch.viewmodel.hilt.SensorDetailViewModel
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.Zoom
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.compose.cartesian.data.lineSeries
import com.patrykandpatrick.vico.compose.cartesian.decoration.HorizontalLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.component.LineComponent
import com.patrykandpatrick.vico.compose.common.data.ExtraStore
import com.patrykandpatrick.vico.compose.common.Fill
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SensorDetailScreen(
    sensorDetailViewModel: SensorDetailViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state = sensorDetailViewModel.uiState
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val deviceName = sensorDetailViewModel.uiState.sensor?.deviceName
                        val tankName = sensorDetailViewModel.uiState.sensor?.tankName
                        val subtitle = if (deviceName != null && tankName != null) {
                            "$deviceName · $tankName"
                        } else {
                            deviceName ?: tankName
                        }
                        Text(
                            sensorDetailViewModel.uiState.sensor?.displayName ?: "",
                            style = MaterialTheme.typography.titleLarge
                        )
                        subtitle?.let {
                            Text(
                                subtitle,
                                style = MaterialTheme.typography.titleSmall
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        when {
            state.isLoading -> {
                Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            state.error != null -> {
                Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = state.error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            else -> {
                SensorDetailScreenContent(
                    state = state,
                    onRangeFilterChange = sensorDetailViewModel::onRangeFilterChange,
                    innerPadding = innerPadding
                )
            }
        }
    }
}

@Composable
fun SensorDetailScreenContent(
    state: SensorDetailUiState,
    onRangeFilterChange: (ReadingRange) -> Unit,
    innerPadding: PaddingValues
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(Dimensions.Padding.page),
        contentPadding = PaddingValues(Dimensions.Padding.medium),
        verticalArrangement = Arrangement.spacedBy(Dimensions.Spacer.medium)
    ) {
        val currentReading = state.readings.lastOrNull()
        item {
            currentReading?.let {
                SensorReadingBar(it, state.config)
            } ?: Text(stringResource(R.string.no_readings))
        }
        item {
            RangeFilter(
                readingRange = state.readingRange,
                onRangeFilterChange = onRangeFilterChange
            )
        }
        item {
            ReadingsChart(
                readings = state.readings,
                config = state.config
            )
        }
        item {
            StatsSummary(state.stats)
        }
        item {
            AlertSummary(state.config, currentReading?.measurementType)
        }
    }
}

@Composable
private fun SensorReadingBar(currentReading: Reading, sensorConfig: SensorConfig?) {
    val currentValue = currentReading.value
    val measurementType = currentReading.measurementType
    val status = deriveSensorStatus(currentValue, sensorConfig)

    // Calculate + format 'updated at' value
    val diff = Clock.System.now() - currentReading.time
    val updatedAtText = when {
        diff.inWholeSeconds < 60 -> stringResource(R.string.updated_s_ago, diff.inWholeSeconds)
        diff.inWholeMinutes < 60 -> stringResource(R.string.updated_m_ago, diff.inWholeMinutes)
        else -> stringResource(R.string.updated_h_ago, diff.inWholeHours)
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        Column {
            // Sensor Reading
            Text(
                text = ValueFormatter.format(currentValue, measurementType),
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Row(horizontalArrangement = Arrangement.SpaceBetween) {
                status?.let { SensorStatusBadge(it) }
                Spacer(modifier = Modifier.width(Dimensions.Spacer.small))
                Text(
                    text = updatedAtText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = Opacity.heavy)
                )
            }
        }
    }
}

@Composable
private fun RangeFilter(
    readingRange: ReadingRange,
    onRangeFilterChange: (ReadingRange) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Dimensions.Spacer.small),
        modifier = modifier
    ) {
        ReadingRange.entries.forEach {
            FilterChip(
                selected = readingRange == it,
                onClick = { onRangeFilterChange(it) },
                label = { Text(it.toString()) }
            )
        }
    }
}

@Composable
fun ReadingsChart(
    readings: List<Reading>,
    config: SensorConfig?,
    modifier: Modifier = Modifier
) {
    if (readings.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.no_data),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        return
    }

    val modelProducer = remember { CartesianChartModelProducer() }

    LaunchedEffect(readings) {
        modelProducer.runTransaction {
            lineSeries {
                series(
                    y = readings.map { it.value },
                    x = readings.map { it.time.epochSeconds.toFloat() }
                )
            }
        }
    }

    // Threshold line colours
    val minLineColor = MaterialTheme.colorScheme.error
    val maxLineColor = MaterialTheme.colorScheme.error

    // Build decorations only when config is available
    val decorations = remember(config) {
        buildList {
            config?.let { config ->
                add(HorizontalLine(
                        y = { config.minValue },
                        line = LineComponent(fill = Fill(minLineColor), thickness = 1.dp),
                    )
                )
                add(HorizontalLine(
                    y = { config.maxValue },
                    line = LineComponent(fill = Fill(maxLineColor), thickness = 1.dp),
                ))
            }
        }
    }

    // X axis: format epoch seconds → time label
    val bottomAxisFormatter = CartesianValueFormatter { _, x, _ ->
        val instant = Instant.fromEpochSeconds(x.toLong())
        val local = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        "%02d:%02d".format(local.hour, local.minute)
    }

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                rangeProvider = object : CartesianLayerRangeProvider {
                    override fun getMinY(minY: Double, maxY: Double, extraStore: ExtraStore): Double {
                        val threshold = config?.minValue ?: minY
                        return minOf(minY, threshold) - 0.5
                    }
                    override fun getMaxY(minY: Double, maxY: Double, extraStore: ExtraStore): Double {
                        val threshold = config?.maxValue ?: maxY
                        return maxOf(maxY, threshold) + 0.5
                    }
                }
            ),
            startAxis = VerticalAxis.rememberStart(),
            bottomAxis = HorizontalAxis.rememberBottom(
                valueFormatter = bottomAxisFormatter,
                itemPlacer = remember { HorizontalAxis.ItemPlacer.aligned(spacing = { 4 }) }
            ),
            decorations = decorations,
        ),
        modelProducer = modelProducer,
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
        scrollState = rememberVicoScrollState(scrollEnabled = true),
        zoomState = rememberVicoZoomState(
            zoomEnabled = true,
            initialZoom = Zoom.Content
        )
    )
}
@Composable
private fun StatsSummary(
    stats: List<StatData>,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        modifier = modifier.fillMaxWidth()
    ) {
        stats.forEach {
            StatCard(it.value, it.type)
        }
    }
}

@Composable
private fun AlertSummary(
    config: SensorConfig?,
    measurementType: MeasurementType?,
    modifier: Modifier = Modifier
) {
    if (config == null || measurementType == null) {
        return
    }
    val formattedMin = ValueFormatter.format(config.minValue, measurementType)
    val formattedMax = ValueFormatter.format(config.maxValue, measurementType)
    Surface(
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = modifier
                .fillMaxWidth()
                .padding(Padding.medium)
        ) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = stringResource(R.string.notification),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(Dimensions.Icon.medium)
            )
            Text(
                stringResource(R.string.warning_threshold, formattedMin, formattedMax),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SensorReadingBarPreview() {
    TankWatchTheme {
        SensorReadingBar(
            currentReading = PreviewData.readings.last(),
            sensorConfig = PreviewData.sensorConfig
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RangeFilterPreview() {
    TankWatchTheme {
        RangeFilter(
            readingRange = ReadingRange.ONE_DAY,
            onRangeFilterChange = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StatsSummaryPreview() {
    TankWatchTheme {
        StatsSummary(PreviewData.statsData)
    }
}

@Preview(showBackground = true)
@Composable
private fun AlertSummaryPreview() {
    TankWatchTheme {
        AlertSummary(
            config = PreviewData.sensorConfig,
            measurementType = PreviewData.readings.last().measurementType
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SensorDetailScreenPreview() {
    TankWatchTheme {
        SensorDetailScreenContent(
            state = PreviewData.sensorDetailUiState,
            onRangeFilterChange = {},
            innerPadding = PaddingValues()
        )
    }
}