package com.garrettforsyth.tankwatch.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.garrettforsyth.tankwatch.R
import com.garrettforsyth.tankwatch.data.model.MeasurementType
import com.garrettforsyth.tankwatch.data.model.Reading
import com.garrettforsyth.tankwatch.data.model.ReadingRange
import com.garrettforsyth.tankwatch.data.model.SensorConfig
import com.garrettforsyth.tankwatch.ui.cards.StatCard
import com.garrettforsyth.tankwatch.ui.components.SensorStatus
import com.garrettforsyth.tankwatch.ui.components.SensorStatusBadge
import com.garrettforsyth.tankwatch.ui.components.deriveSensorStatus
import com.garrettforsyth.tankwatch.ui.model.StatData
import com.garrettforsyth.tankwatch.ui.theme.Dimensions
import com.garrettforsyth.tankwatch.ui.theme.Dimensions.Padding
import com.garrettforsyth.tankwatch.ui.theme.TankWatchTheme
import com.garrettforsyth.tankwatch.ui.theme.TemperatureColor
import com.garrettforsyth.tankwatch.ui.theme.temperatureCold
import com.garrettforsyth.tankwatch.ui.theme.temperatureWarm
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
import com.patrykandpatrick.vico.compose.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.component.LineComponent
import com.patrykandpatrick.vico.compose.common.data.ExtraStore
import com.patrykandpatrick.vico.compose.common.Fill
import com.patrykandpatrick.vico.compose.common.Insets
import com.patrykandpatrick.vico.compose.common.component.TextComponent
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
            .padding(innerPadding),
        contentPadding = PaddingValues(start = Padding.page, end = Padding.page, bottom = Padding.page),
        verticalArrangement = Arrangement.spacedBy(Dimensions.Spacer.medium)
    ) {
        val currentReading = state.readings.lastOrNull()
        item {
            currentReading?.let {
                SensorReadingBar(it, state.config)
            } ?: Text(stringResource(R.string.no_readings))
        }
        item {
            ReadingsChart(
                readings = state.readings,
                config = state.config,
                readingRange = state.readingRange
            )
        }
        item {
            RangeFilter(
                readingRange = state.readingRange,
                onRangeFilterChange = onRangeFilterChange
            )
        }
        item {
            StatsSummary(state.stats)
        }
    }
}

@Composable
private fun SensorReadingBar(
    currentReading: Reading,
    sensorConfig: SensorConfig?,
    modifier: Modifier = Modifier
) {
    val currentValue = currentReading.value
    val measurementType = currentReading.measurementType
    val status = deriveSensorStatus(currentValue, sensorConfig)

    val valueColor = sensorConfig?.let { config ->
        TemperatureColor.forValue(
        celsius = currentReading.value.toFloat(),
        coldTemp = config.minValue.toFloat(),
        neutralTemp = ((config.minValue + config.maxValue) / 2.0).toFloat(),
        warmTemp = config.maxValue.toFloat()
    ) } ?: MaterialTheme.colorScheme.primary

    val diff = Clock.System.now() - currentReading.time
    val updatedAtText = when {
        diff.inWholeSeconds < 60 -> stringResource(R.string.updated_s_ago, diff.inWholeSeconds)
        diff.inWholeMinutes < 60 -> stringResource(R.string.updated_m_ago, diff.inWholeMinutes)
        else -> stringResource(R.string.updated_h_ago, diff.inWholeHours)
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(Dimensions.Spacer.small)
    ) {
        StatusBar(status)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: icon + reading + updated-at
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Dimensions.Spacer.small)
                ) {
                    Icon(
                        imageVector = com.garrettforsyth.tankwatch.ui.theme.Icons.measurementTypeIcon(measurementType),
                        contentDescription = null,
                        tint = valueColor,
                        modifier = Modifier.size(Dimensions.Icon.large)
                    )
                    Text(
                        text = ValueFormatter.format(currentValue, measurementType),
                        style = MaterialTheme.typography.displayMedium,
                        color = valueColor
                    )
                }
                Text(
                    text = updatedAtText,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(start = Dimensions.Padding.xSmall)
                )
            }

            // Right: threshold values
            sensorConfig?.let { config ->
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(Dimensions.Spacer.xSmall)
                ) {
                    ThresholdLabel(
                        label = stringResource(R.string.max),
                        value = ValueFormatter.format(config.maxValue, measurementType),
                        color = temperatureWarm
                    )
                    ThresholdLabel(
                        label = stringResource(R.string.min),
                        value = ValueFormatter.format(config.minValue, measurementType),
                        color = temperatureCold
                    )
                }
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
        horizontalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxWidth()
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
}

@Composable
fun ReadingsChart(
    readings: List<Reading>,
    config: SensorConfig?,
    readingRange: ReadingRange,
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

    val currentReading = readings.last()
    val measurementType = currentReading.measurementType
    val lineColour = config?.let { config ->
        TemperatureColor.forValue(
            celsius = currentReading.value.toFloat(),
            coldTemp = config.minValue.toFloat(),
            neutralTemp = ((config.minValue + config.maxValue) / 2.0).toFloat(),
            warmTemp = config.maxValue.toFloat()
        ) } ?: MaterialTheme.colorScheme.primary

    val minLabel = config?.let {
        ValueFormatter.format(it.minValue, measurementType)
    } ?: ""
    val maxLabel = config?.let {
        ValueFormatter.format(it.maxValue, measurementType)
    } ?: ""

    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
    val labelTextStyle = TextStyle(
        color = onSurfaceVariant,
        fontSize = 10.sp
    )

    val decorations = remember(config, onSurfaceVariant) {
        buildList {
            config?.let { cfg ->
                add(
                    HorizontalLine(
                        y = { cfg.minValue },
                        line = LineComponent(
                            fill = Fill(temperatureCold),
                            thickness = 1.dp
                        ),
                        labelComponent = TextComponent(
                            textStyle = labelTextStyle.copy(color = temperatureCold),
                            margins = Insets(end = Padding.xSmall)
                        ),
                        label = { minLabel }
                    )
                )
                add(
                    HorizontalLine(
                        y = { cfg.maxValue },
                        line = LineComponent(
                            fill = Fill(temperatureWarm),
                            thickness = 1.dp
                        ),
                        labelComponent = TextComponent(
                            textStyle = labelTextStyle.copy(color = temperatureWarm),
                            margins = Insets(end = Padding.xSmall)
                        ),
                        label = { maxLabel }
                    )
                )
            }
        }
    }

    val bottomAxisFormatter = CartesianValueFormatter { _, x, _ ->
        val instant = Instant.fromEpochSeconds(x.toLong())
        val local = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        "%02d:%02d".format(local.hour, local.minute)
    }

    Column(modifier = modifier.fillMaxWidth()) {
        // Y-axis title
        Row (verticalAlignment = Alignment.Bottom) {
            Text(
                text = ValueFormatter.unitLabel(measurementType),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = Padding.xSmall)
            )
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(
                        R.string.readings_in_last,
                        stringResource(readingRange.displayStringResId)
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        CartesianChartHost(
            chart = rememberCartesianChart(
                rememberLineCartesianLayer(
                    lineProvider = LineCartesianLayer.LineProvider.series(
                        LineCartesianLayer.rememberLine(
                            fill = LineCartesianLayer.LineFill.single(Fill(lineColour)),
                            areaFill = LineCartesianLayer.AreaFill.single(
                                Fill(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            lineColour.copy(alpha = 0.3f),
                                            lineColour.copy(alpha = 0f)
                                        )
                                    )
                                )
                            )
                        )
                    ),
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
                    valueFormatter = bottomAxisFormatter
                ),
                decorations = decorations,
            ),
            modelProducer = modelProducer,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            scrollState = rememberVicoScrollState(scrollEnabled = true),
            zoomState = rememberVicoZoomState(
                zoomEnabled = true,
                initialZoom = Zoom.Content
            )
        )

        // X-axis title
        Text(
            text = stringResource(R.string.x_axis_time),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .align(Alignment.End)
                .padding(end = Padding.xSmall)
        )
    }
}

@Composable
private fun StatsSummary(
    stats: List<StatData>,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Dimensions.Spacer.small),
        modifier = modifier.fillMaxWidth()
    ) {
        stats.forEach {
            StatCard(
                value = it.value,
                type = it.type,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ThresholdLabel(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Dimensions.Spacer.xSmall),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = color
        )
    }
}

@Composable
private fun StatusBar(status: SensorStatus?) {
    val (label, color) = when (status) {
        SensorStatus.Normal -> stringResource(R.string.normal) to MaterialTheme.colorScheme.tertiary
        SensorStatus.Warning -> stringResource(R.string.warning) to MaterialTheme.colorScheme.error
        null -> stringResource(R.string.no_data) to MaterialTheme.colorScheme.onSurfaceVariant
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimensions.Spacer.small),
        modifier = Modifier.fillMaxWidth()
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = color.copy(alpha = Dimensions.Opacity.heavy)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = color
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = color.copy(alpha = Dimensions.Opacity.heavy)
        )
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
private fun SensorDetailScreenPreview() {
    TankWatchTheme {
        SensorDetailScreenContent(
            state = PreviewData.sensorDetailUiState,
            onRangeFilterChange = {},
            innerPadding = PaddingValues()
        )
    }
}