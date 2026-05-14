package com.garrettforsyth.tankwatch.viewmodel.hilt

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.garrettforsyth.tankwatch.data.model.Device
import com.garrettforsyth.tankwatch.data.model.Reading
import com.garrettforsyth.tankwatch.data.model.Sensor
import com.garrettforsyth.tankwatch.data.model.SensorConfig
import com.garrettforsyth.tankwatch.data.repository.TankWatchRepository
import com.garrettforsyth.tankwatch.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.collections.emptyList

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val tankWatchRepository: TankWatchRepository
): ViewModel()  {

    var uiState by mutableStateOf(DashboardUiState())
        private set

    init {
        loadDashboard()
    }

    /**
     * Here we:
     * 1) Request the server for all the user's devices
     * 2) For each device request all the sensors for that device
     * 3.1) For each device sensor, request for its configuration
     * 3.2) For each device sensor, request for its readings
     */
    private fun loadDashboard() {
        viewModelScope.launch {
            uiState = DashboardUiState(isLoading = true)

            when (val devices = tankWatchRepository.getDevices()) {
                is Resource.Loading -> return@launch
                is Resource.Error -> {
                    uiState = DashboardUiState(isLoading = false, error = devices.message)
                }
                is Resource.Success ->{
                    // Build all device states in parallel
                    val deviceUiStates = devices.data.map { device ->
                        async { buildDeviceUiState(device) }
                    }.awaitAll()

                    uiState = DashboardUiState(
                        devices = deviceUiStates,
                        isLoading = false
                    )
                }
            }
        }
    }

    private suspend fun buildDeviceUiState(device: Device): DeviceUiState {
        val (sensors, error) = when (val result = tankWatchRepository.getSensors(device.id)) {
            is Resource.Success -> result.data to null
            is Resource.Error -> emptyList<Sensor>() to result.message
            else -> emptyList<Sensor>() to null
        }

        val sensorUiStates = coroutineScope {
            sensors.map { sensor ->
                async {
                    val (readings, readingsError) =
                        when (val r = tankWatchRepository.getReadings(device.id, sensor.id)) {
                            is Resource.Success -> r.data.sortedBy { it.time } to null
                            is Resource.Error -> emptyList<Reading>() to r.message
                            else -> emptyList<Reading>() to null
                        }
                    val (config, configError) = when (val r = tankWatchRepository.getSensorConfig(sensor.id)) {
                        is Resource.Success -> r.data to null
                        is Resource.Error -> null to r.message
                        else -> null to null
                    }
                    SensorUiState(sensor, readings, config, readingsError, configError)
                }
            }.awaitAll()
        }
        return DeviceUiState(device, sensorUiStates, error)
    }
}

data class DashboardUiState(
    val devices: List<DeviceUiState> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

data class DeviceUiState(
    val device: Device,
    val sensors: List<SensorUiState>,
    val error: String? = null
)

data class SensorUiState(
    val sensor: Sensor,
    val readings: List<Reading>,
    val config: SensorConfig?,
    val readingsError: String? = null,
    val configError: String? = null
)