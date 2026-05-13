package com.garrettforsyth.tankwatch.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.garrettforsyth.tankwatch.viewmodel.hilt.MainViewModel

@Composable
fun MainScreen() {
    val viewModel: MainViewModel = hiltViewModel()
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        DashboardScreen(
            modifier = Modifier.padding(innerPadding)
        )
    }
}