package com.garrettforsyth.tankwatch.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.garrettforsyth.tankwatch.ui.cards.TankCard
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
                        TankCard(
                            deviceState = deviceState,
                            onSensorClick = { toSensorDetailScreen(it) }
                        )
                    }
                }
            }
        }
    }
}