package com.garrettforsyth.tankwatch.viewmodel.hilt

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.garrettforsyth.tankwatch.data.model.Reading
import com.garrettforsyth.tankwatch.data.repository.TankWatchRepository
import com.garrettforsyth.tankwatch.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val tankWatchRepository: TankWatchRepository
): ViewModel()  {

    var readings by mutableStateOf<Resource<List<Reading>>>(Resource.Loading)
        private set

    init {
        fetchReadings()
    }

    private fun fetchReadings() {
        viewModelScope.launch {
            // TODO: Fetch devices and sensors for logged in user
            readings = tankWatchRepository.getReadings(
                deviceId = 1,
                sensorId = 1,
            )
        }
    }
}