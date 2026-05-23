package com.garrettforsyth.tankwatch.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.garrettforsyth.tankwatch.ui.screens.MainScreen
import com.garrettforsyth.tankwatch.ui.screens.SensorDetailScreen
import com.garrettforsyth.tankwatch.viewmodel.hilt.DashboardViewModel
import com.garrettforsyth.tankwatch.viewmodel.hilt.MainViewModel
import com.garrettforsyth.tankwatch.viewmodel.hilt.SensorDetailViewModel

@Composable
fun TankWatchNavGraph(navController: NavHostController) {
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