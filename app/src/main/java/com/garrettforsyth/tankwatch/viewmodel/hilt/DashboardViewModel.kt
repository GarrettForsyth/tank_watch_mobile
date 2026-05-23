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
import com.garrettforsyth.tankwatch.util.dataOrNull
import com.garrettforsyth.tankwatch.util.errorOrNull
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

            val devicesResult = tankWatchRepository.getDevices()

            val deviceUiStates = coroutineScope {
                devicesResult.dataOrNull().orEmpty().map { device ->
                    async { buildDeviceUiState(device) }
                }.awaitAll()
            }

            uiState = DashboardUiState(
                devices = deviceUiStates,
                error = devicesResult.errorOrNull(),
                isLoading = false
            )
        }
    }

    private suspend fun buildDeviceUiState(device: Device): DashboardDeviceUiState {
        val sensorsResult = tankWatchRepository.getSensors(device.id)

        val sensorUiStates = coroutineScope {
            sensorsResult.dataOrNull().orEmpty().map { sensor ->
                async {
                    val readingsDeferred = async { tankWatchRepository.getReadings(sensor.id) }
                    val configDeferred = async { tankWatchRepository.getSensorConfig(sensor.id) }

                    val readingsResult = readingsDeferred.await()
                    val configResult = configDeferred.await()

                    DashboardSensorUiState(
                        sensor = sensor,
                        readings = readingsResult.dataOrNull()?.sortedBy { it.time } ?: emptyList(),
                        config = configResult.dataOrNull(),
                        readingsError = readingsResult.errorOrNull(),
                        configError = configResult.errorOrNull()
                    )
                }
            }.awaitAll()
        }

        return DashboardDeviceUiState(
            device = device,
            sensors = sensorUiStates,
            error = sensorsResult.errorOrNull()
        )
    }
}

data class DashboardUiState(
    val devices: List<DashboardDeviceUiState> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

data class DashboardDeviceUiState(
    val device: Device,
    val sensors: List<DashboardSensorUiState>,
    val error: String? = null
)

data class DashboardSensorUiState(
    val sensor: Sensor,
    val readings: List<Reading>,
    val config: SensorConfig?,
    val readingsError: String? = null,
    val configError: String? = null
)