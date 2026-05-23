package com.garrettforsyth.tankwatch.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.garrettforsyth.tankwatch.ui.cards.SensorCard
import com.garrettforsyth.tankwatch.ui.theme.Dimensions
import com.garrettforsyth.tankwatch.viewmodel.hilt.DashboardViewModel

@Composable
fun DashboardScreen(
    modifier: Modifier = Modifier,
    dashboardViewModel: DashboardViewModel,
    toSensorDetailScreen: (Int) -> Unit
) {
    val state = dashboardViewModel.uiState

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
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(Dimensions.Padding.medium),
                verticalArrangement = Arrangement.spacedBy(Dimensions.Spacer.medium)
            ) {
                state.devices.forEach { deviceState ->
                    item {
                        Text(
                            text = deviceState.device.name ?: deviceState.device.macAddress,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(
                                horizontal = Dimensions.Padding.xSmall,
                                vertical = Dimensions.Padding.xSmall
                            )
                        )
                    }
                    items(deviceState.sensors) { sensorState ->
                        SensorCard(
                            sensor = sensorState.sensor,
                            readings = sensorState.readings,
                            config = sensorState.config,
                            onClick = { toSensorDetailScreen(sensorState.sensor.id) }
                        )
                    }
                }
            }
        }
    }
}