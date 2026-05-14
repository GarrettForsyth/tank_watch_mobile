package com.garrettforsyth.tankwatch.ui.cards

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.garrettforsyth.tankwatch.R
import com.garrettforsyth.tankwatch.data.model.MeasurementType
import com.garrettforsyth.tankwatch.data.model.Reading
import com.garrettforsyth.tankwatch.data.model.Sensor
import com.garrettforsyth.tankwatch.data.model.SensorConfig
import com.garrettforsyth.tankwatch.ui.theme.Dimensions
import com.garrettforsyth.tankwatch.ui.theme.TankWatchTheme
import com.garrettforsyth.tankwatch.ui.util.PreviewData
import com.garrettforsyth.tankwatch.ui.util.ValueFormatter
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.Axis
import com.patrykandpatrick.vico.compose.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.compose.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.compose.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.compose.cartesian.data.lineSeries
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.data.ExtraStore

@Composable
fun SensorCard(
    sensor: Sensor,
    readings: List<Reading>,
    config: SensorConfig?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentValue = readings.lastOrNull()?.value
    val measurementType = readings.lastOrNull()?.measurementType ?: MeasurementType.Unknown
    val status = deriveStatus(currentValue, config)

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.padding(Dimensions.Padding.medium)) {

            // Name and Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = sensor.displayName ?: sensor.model,
                    style = MaterialTheme.typography.titleMedium
                )
                if (status != null) {
                    StatusBadge(status)
                }
            }

            Spacer(modifier = Modifier.height(Dimensions.Spacer.small))

            // Sensor Reading
            Text(
                text = if (currentValue != null)
                    ValueFormatter.format(currentValue, measurementType)
                else "—",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(Dimensions.Spacer.medium))

            Sparkline(readings = readings)
        }
    }
}

@Composable
private fun StatusBadge(status: SensorStatus) {
    val (label, color) = when (status) {
        SensorStatus.Normal  -> stringResource(R.string.normal) to MaterialTheme.colorScheme.tertiary
        SensorStatus.Warning -> stringResource(R.string.warning) to MaterialTheme.colorScheme.error
    }
    Surface(
        shape = MaterialTheme.shapes.small,
        color = color.copy(alpha = 0.15f),
        contentColor = color
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(
                horizontal = Dimensions.Padding.small,
                vertical = Dimensions.Padding.xSmall
            )
        )
    }
}

@Composable
private fun Sparkline(readings: List<Reading>) {
    if (readings.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No data",
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

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer(
                // Zoom in on the relevant areas
                rangeProvider = object : CartesianLayerRangeProvider {
                    override fun getMinY(minY: Double, maxY: Double, extraStore: ExtraStore): Double {
                        return minY - 0.5
                    }
                    override fun getMaxY(minY: Double, maxY: Double, extraStore: ExtraStore): Double {
                        return maxY + 0.5
                    }
                }),
        ),
        modelProducer = modelProducer,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        scrollState = rememberVicoScrollState(scrollEnabled = false),
        zoomState = rememberVicoZoomState(zoomEnabled = false),
    )
}

// --- Status helpers ---

enum class SensorStatus { Normal, Warning }

private fun deriveStatus(value: Double?, config: SensorConfig?): SensorStatus? {
    if (value == null || config == null) return null
    return when {
        value < config.minValue || value > config.maxValue -> SensorStatus.Warning
        else -> SensorStatus.Normal
    }
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