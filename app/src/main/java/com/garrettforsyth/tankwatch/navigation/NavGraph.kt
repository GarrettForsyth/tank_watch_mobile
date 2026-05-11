package com.garrettforsyth.tankwatch.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.garrettforsyth.tankwatch.ui.screens.MainScreen

@Composable
fun TankWatchNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Destinations.Main) {
        composable<Destinations.Main> {
            MainScreen()
        }
    }
}