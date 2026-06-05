package com.garrettforsyth.tankwatch.ui.cards.sensor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.garrettforsyth.tankwatch.R
import com.garrettforsyth.tankwatch.data.model.MeasurementType
import com.garrettforsyth.tankwatch.data.model.Reading
import com.garrettforsyth.tankwatch.data.model.Sensor
import com.garrettforsyth.tankwatch.data.model.SensorConfig
import com.garrettforsyth.tankwatch.ui.components.SensorStatusBadge
import com.garrettforsyth.tankwatch.ui.components.deriveSensorStatus
import com.garrettforsyth.tankwatch.ui.theme.Dimensions
import com.garrettforsyth.tankwatch.ui.theme.Icons
import com.garrettforsyth.tankwatch.ui.theme.TankWatchTheme
import com.garrettforsyth.tankwatch.ui.theme.TemperatureColor
import com.garrettforsyth.tankwatch.ui.theme.temperatureCold
import com.garrettforsyth.tankwatch.ui.theme.temperatureNeutral
import com.garrettforsyth.tankwatch.ui.theme.temperatureWarm
import com.garrettforsyth.tankwatch.ui.util.PreviewData
import com.garrettforsyth.tankwatch.ui.util.ValueFormatter
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.compose.cartesian.data.lineSeries
import com.patrykandpatrick.vico.compose.cartesian.decoration.HorizontalLine
import com.patrykandpatrick.vico.compose.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.Fill
import com.patrykandpatrick.vico.compose.common.component.LineComponent
import com.patrykandpatrick.vico.compose.common.data.ExtraStore
import kotlin.collections.buildList

@Composable
fun TemperatureSensorCard(
    sensor: Sensor,
    readings: List<Reading>,
    config: SensorConfig?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lastReading = readings.last()  // Should always exist (see SensorCard)
    val status = deriveSensorStatus(lastReading.value, config)
    val valueColor = config?.let { TemperatureColor.forValue(
        celsius = lastReading.value.toFloat(),
        coldTemp = config.minValue.toFloat(),
        neutralTemp = ((config.minValue + config.maxValue) / 2.0).toFloat(),
        warmTemp = config.maxValue.toFloat()
    ) } ?: MaterialTheme.colorScheme.primary

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(modifier = Modifier.padding(Dimensions.Padding.medium)) {

            // Single header row: icon + name + reading + badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimensions.Spacer.small)
            ) {
                Icon(
                    imageVector = Icons.MeasurementTypeIcon(MeasurementType.Temperature),
                    contentDescription = null,
                    modifier = Modifier.size(Dimensions.Icon.small),
                    tint = valueColor
                )
                Text(
                    text = sensor.displayName ?: sensor.model,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
                if (status != null) {
                    Spacer(modifier = Modifier.width(Dimensions.Spacer.xSmall))
                    SensorStatusBadge(status)
                }
            }

            Spacer(modifier = Modifier.height(Dimensions.Spacer.small))

            Sparkline(
                readings = readings,
                config = config,
                lineColor = valueColor
            )
        }
    }
}

@Composable
private fun Sparkline(
    readings: List<Reading>,
    config: SensorConfig?,
    lineColor: Color
) {
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

    val decorations = remember(config) {
        buildList {
            config?.let { config ->
                add(
                    HorizontalLine(
                        y = { config.minValue },
                        line = LineComponent(
                            fill = Fill(temperatureCold),
                            thickness = 1.dp
                        )
                    )
                )
                add(HorizontalLine(
                    y = { config.maxValue },
                    line = LineComponent(
                        fill = Fill(temperatureWarm),
                        thickness = 1.dp
                    )
                ))
            }
        }
    }

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                lineProvider = LineCartesianLayer.LineProvider.series(
                    LineCartesianLayer.rememberLine(
                        fill = LineCartesianLayer.LineFill.single(Fill(lineColor)),
                        areaFill = LineCartesianLayer.AreaFill.single(
                            Fill(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        lineColor.copy(alpha = Dimensions.Opacity.heavy),
                                        lineColor.copy(alpha = Dimensions.Opacity.none)
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
            decorations = decorations
        ),
        modelProducer = modelProducer,
        modifier = Modifier
            .fillMaxWidth()
            .height(Dimensions.sparkLineHeight),
        scrollState = rememberVicoScrollState(scrollEnabled = false),
        zoomState = rememberVicoZoomState(zoomEnabled = false),
    )
}

// --- Previews ---
// Note: vico charts will not show

@Preview(showBackground = true)
@Composable
private fun SensorCardPreview() {
    TankWatchTheme {
        SensorCard(
            sensor = PreviewData.sensor,
            readings = PreviewData.readings,
            config = PreviewData.sensorConfig,
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SensorCardWarningPreview() {
    TankWatchTheme {
        SensorCard(
            sensor = PreviewData.sensor,
            readings = PreviewData.readings,
            config = PreviewData.sensorConfig.copy(minValue = 28.0),
            onClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SensorCardEmptyPreview() {
    TankWatchTheme {
        SensorCard(
            sensor = PreviewData.sensor,
            readings = PreviewData.readingsEmpty,
            config = PreviewData.sensorConfig,
            onClick = {}
        )
    }
}