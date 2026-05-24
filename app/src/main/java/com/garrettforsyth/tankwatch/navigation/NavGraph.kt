package com.garrettforsyth.tankwatch.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.garrettforsyth.tankwatch.ui.screens.MainScreen
import com.garrettforsyth.tankwatch.ui.screens.SensorDetailScreen
import com.garrettforsyth.tankwatch.viewmodel.hilt.DashboardViewModel
import com.garrettforsyth.tankwatch.viewmodel.hilt.MainViewModel
import com.garrettforsyth.tankwatch.viewmodel.hilt.SensorDetailViewModel

@Composable
fun TankWatchNavGraph(mainViewModel: MainViewModel) {
    val navController = rememberNavController()
    val pendingSensorId by mainViewModel.pendingSensorId.collectAsStateWithLifecycle()

    LaunchedEffect(pendingSensorId) {
        pendingSensorId?.let { sensorId ->
            navController.navigate(Destinations.SensorDetail(sensorId = sensorId))
            mainViewModel.onNavigatedToSensor()
        }
    }

    NavHost(navController = navController, startDestination = Destinations.Main) {
        composable<Destinations.Main> {
            val mainViewModel: MainViewModel = hiltViewModel()
            val dashboardViewModel: DashboardViewModel = hiltViewModel()
            MainScreen(
                mainViewModel = mainViewModel,
                dashboardViewModel = dashboardViewModel,
                toSensorDetailScreen = {
                    navController.navigate(Destinations.SensorDetail(it))
                }
            )
        }
        composable<Destinations.SensorDetail> {
            val sensorDetailViewModel: SensorDetailViewModel = hiltViewModel()
            SensorDetailScreen(
                sensorDetailViewModel = sensorDetailViewModel,
                onBack = { navController.navigateUp() }
            )
        }
    }
}