package com.garrettforsyth.tankwatch.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.garrettforsyth.tankwatch.viewmodel.hilt.DashboardViewModel
import com.garrettforsyth.tankwatch.viewmodel.hilt.MainViewModel

@Composable
fun MainScreen(
    mainViewModel: MainViewModel,
    dashboardViewModel: DashboardViewModel,
    toSensorDetailScreen: (Int) -> Unit
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        DashboardScreen(
            modifier = Modifier.padding(innerPadding),
            dashboardViewModel = dashboardViewModel,
            toSensorDetailScreen = toSensorDetailScreen
        )
    }
}