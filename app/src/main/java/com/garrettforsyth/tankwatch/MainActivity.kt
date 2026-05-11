package com.garrettforsyth.tankwatch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.garrettforsyth.tankwatch.navigation.TankWatchNavGraph
import com.garrettforsyth.tankwatch.ui.theme.TankWatchTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TankWatchTheme {
                val navController = rememberNavController()
                TankWatchNavGraph(navController)
            }
        }
    }
}