package com.garrettforsyth.tankwatch.ui.cards

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.garrettforsyth.tankwatch.R
import com.garrettforsyth.tankwatch.data.model.Device
import com.garrettforsyth.tankwatch.ui.cards.sensor.SensorCard
import com.garrettforsyth.tankwatch.ui.theme.Dimensions
import com.garrettforsyth.tankwatch.ui.theme.TankWatchTheme
import com.garrettforsyth.tankwatch.ui.util.PreviewData
import com.garrettforsyth.tankwatch.viewmodel.hilt.DashboardDeviceUiState
import com.garrettforsyth.tankwatch.viewmodel.hilt.DashboardSensorUiState

// TODO: For now, tank card build off the device state, after the backend
// supports /dashboard endpoint, we can refactor this to use a tankState
@Composable
fun TankCard(
    deviceState: DashboardDeviceUiState,
    onSensorClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            // Give some depth when holding sensor cards
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        ),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = Dimensions.Opacity.light)
        )
    ) {
        Column(modifier = Modifier.padding(Dimensions.Padding.medium)) {

            TankCardHeader(deviceState.device)

            Spacer(modifier = Modifier.height(Dimensions.Spacer.small))

            when {
                deviceState.error != null -> {
                    TankCardError(message = deviceState.error)
                }
                deviceState.sensors.isEmpty() -> {
                    TankCardEmpty()
                }
                else -> {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(Dimensions.Spacer.small)
                    ) {
                        deviceState.sensors.forEach { sensorState ->
                            SensorCard(
                                sensor = sensorState.sensor,
                                readings = sensorState.readings,
                                config = sensorState.config,
                                onClick = { onSensorClick(sensorState.sensor.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TankCardHeader(device: Device) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = device.name ?: device.macAddress,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun TankCardError(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimensions.Padding.medium),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
private fun TankCardEmpty() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Dimensions.Padding.medium),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.no_sensors),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// Previews
@Preview(showBackground = true)
@Composable
private fun TankCardPreview() {
    TankWatchTheme {
        TankCard(
            deviceState = DashboardDeviceUiState(
                device = PreviewData.device,
                sensors = listOf(
                    DashboardSensorUiState(
                        sensor = PreviewData.sensor,
                        readings = PreviewData.readings,
                        config = PreviewData.sensorConfig
                    )
                )
            ),
            onSensorClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TankCardEmptyPreview() {
    TankWatchTheme {
        TankCard(
            deviceState = DashboardDeviceUiState(
                device = PreviewData.device,
                sensors = emptyList()
            ),
            onSensorClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TankCardErrorPreview() {
    TankWatchTheme {
        TankCard(
            deviceState = DashboardDeviceUiState(
                device = PreviewData.device,
                sensors = emptyList(),
                error = "Failed to fetch tank"
            ),
            onSensorClick = {}
        )
    }
}
