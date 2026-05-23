package com.garrettforsyth.tankwatch.viewmodel.hilt

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.garrettforsyth.tankwatch.data.model.MeasurementType
import com.garrettforsyth.tankwatch.data.model.Reading
import com.garrettforsyth.tankwatch.data.model.ReadingRange
import com.garrettforsyth.tankwatch.data.model.Sensor
import com.garrettforsyth.tankwatch.data.model.SensorConfig
import com.garrettforsyth.tankwatch.data.repository.TankWatchRepository
import com.garrettforsyth.tankwatch.navigation.Destinations
import com.garrettforsyth.tankwatch.ui.model.StatData
import com.garrettforsyth.tankwatch.ui.model.StatType
import com.garrettforsyth.tankwatch.ui.util.ValueFormatter
import com.garrettforsyth.tankwatch.util.dataOrNull
import com.garrettforsyth.tankwatch.util.errorOrNull
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@HiltViewModel
class SensorDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val tankWatchRepository: TankWatchRepository
) : ViewModel() {

    private val sensorId: Int = savedStateHandle.toRoute<Destinations.SensorDetail>().sensorId

    var uiState by mutableStateOf(SensorDetailUiState())
        private set

    init {
        loadSensorDetails()
    }

    fun onRangeFilterChange(readingRange: ReadingRange) {
        loadSensorDetails(readingRange)
    }

    /**
     * Here we:
     * 1) Request the server for all the sensor's info
     * 2) For the server for the sensor's readings
     */
    private fun loadSensorDetails(readingRange: ReadingRange = ReadingRange.ONE_DAY) {
        uiState = SensorDetailUiState(isLoading = true)

        // Fetch the sensor, readings, and config all at the same time
        viewModelScope.launch {
            coroutineScope {
                val sensorDeferred = async { tankWatchRepository.getSensor(sensorId) }
                val readingsDeferred = async { tankWatchRepository.getReadings(sensorId, readingRange) }
                val configDeferred = async { tankWatchRepository.getSensorConfig(sensorId) }

                val sensorResult = sensorDeferred.await()
                val readingsResult = readingsDeferred.await()
                val configResult = configDeferred.await()
                val statsData = calculateStatsData(readingsResult.dataOrNull() ?: emptyList())
                val error = sensorResult.errorOrNull() ?: readingsResult.errorOrNull() ?: configResult.errorOrNull()

                uiState = SensorDetailUiState(
                    sensor = sensorResult.dataOrNull(),
                    readings = readingsResult.dataOrNull()?.sortedBy { it.time } ?: emptyList(),
                    stats = statsData,
                    config = configResult.dataOrNull(),
                    readingRange = readingRange,
                    error = error,
                    isLoading = false
                )
            }
        }
    }

    /* Different sensors may want different stats: switch on measurement type */
    private fun calculateStatsData(readings: List<Reading>): List<StatData> {
        if (readings.isEmpty()) return emptyList()
        return when (val measurementType = readings.first().measurementType) {
            MeasurementType.Temperature -> {
                val readings = readings.map { it.value }
                val max = ValueFormatter.format(readings.max(), measurementType)
                val min = ValueFormatter.format(readings.min(), measurementType)
                val avg = ValueFormatter.format(readings.average(), measurementType)
                listOf(
                    StatData(min, StatType.Min),
                    StatData(avg, StatType.Average),
                    StatData(max, StatType.Max)
                )
            }
            else -> emptyList()
        }
    }
}

data class SensorDetailUiState(
    val sensor: Sensor? = null,
    val readings: List<Reading> = emptyList(),
    val stats: List<StatData> = emptyList(),
    val config: SensorConfig? = null,
    val readingRange: ReadingRange = ReadingRange.ONE_DAY,
    val error: String? = null,
    val isLoading: Boolean = true
)