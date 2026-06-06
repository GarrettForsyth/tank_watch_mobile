package com.garrettforsyth.tankwatch.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.garrettforsyth.tankwatch.R
import com.garrettforsyth.tankwatch.viewmodel.hilt.DashboardViewModel
import com.garrettforsyth.tankwatch.viewmodel.hilt.MainViewModel

@Composable
fun MainScreen(
    mainViewModel: MainViewModel,
    dashboardViewModel: DashboardViewModel,
    toSensorDetailScreen: (Int) -> Unit
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.tank_watch_word_mark),
                    contentDescription = stringResource(R.string.tank_watch),
                    colorFilter = ColorFilter.tint(
                        color = MaterialTheme.colorScheme.onBackground,
                        blendMode = BlendMode.SrcIn
                    )
                )
            }
            DashboardScreen(
                dashboardViewModel = dashboardViewModel,
                toSensorDetailScreen = toSensorDetailScreen
            )
        }
    }
}