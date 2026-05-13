package com.garrettforsyth.tankwatch.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.garrettforsyth.tankwatch.util.Resource
import com.garrettforsyth.tankwatch.viewmodel.hilt.DashboardViewModel

@Composable
fun DashboardScreen(
    modifier: Modifier,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    Column(modifier = modifier) {
        when (val state = viewModel.readings) {
            is Resource.Loading -> Text("Loading...")
            is Resource.Error -> Text("Error: ${state.message}")
            is Resource.Success -> LazyColumn {
                items(state.data) { reading ->
                    Text("${reading.time} — ${reading.value}°C")
                }
            }
        }
    }
}