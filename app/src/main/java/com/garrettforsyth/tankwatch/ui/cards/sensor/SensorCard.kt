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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.garrettforsyth.tankwatch.R
import com.garrettforsyth.tankwatch.data.model.MeasurementType
import com.garrettforsyth.tankwatch.data.model.Reading
import com.garrettforsyth.tankwatch.data.model.Sensor
import com.garrettforsyth.tankwatch.data.model.SensorConfig
import com.garrettforsyth.tankwatch.ui.theme.Dimensions

@Composable
fun SensorCard(
    sensor: Sensor,
    readings: List<Reading>,
    config: SensorConfig?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lastReading = readings.lastOrNull()
    if (lastReading == null) {
        ErrorSensorCard(
            sensor,
            onClick,
            stringResource(R.string.no_data),
            modifier
        )
        return
    }

    val measurementType = lastReading.measurementType
    when (measurementType) {
        MeasurementType.Temperature -> TemperatureSensorCard(sensor, readings, config, onClick, modifier)
        else -> ErrorSensorCard(sensor, onClick, stringResource(R.string.unknown_sensor_type), modifier)
    }
}

@Composable
fun ErrorSensorCard(
    sensor: Sensor,
    onClick: () -> Unit,
    errorMessage: String,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(modifier = Modifier.padding(Dimensions.Padding.medium)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimensions.Spacer.small)
            ) {
                Text(
                    text = sensor.displayName ?: sensor.model,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Dimensions.sparkLineHeight),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
