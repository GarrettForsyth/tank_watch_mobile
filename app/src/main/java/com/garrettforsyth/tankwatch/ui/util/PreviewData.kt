package com.garrettforsyth.tankwatch.ui.util

import com.garrettforsyth.tankwatch.data.model.Device
import com.garrettforsyth.tankwatch.data.model.MeasurementType
import com.garrettforsyth.tankwatch.data.model.Reading
import com.garrettforsyth.tankwatch.data.model.Sensor
import com.garrettforsyth.tankwatch.data.model.SensorConfig
import com.garrettforsyth.tankwatch.ui.model.StatData
import com.garrettforsyth.tankwatch.ui.model.StatType
import com.garrettforsyth.tankwatch.viewmodel.hilt.SensorDetailUiState
import kotlin.Int
import kotlin.collections.List
import kotlin.random.Random
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

object PreviewData {

    val device = Device(
        id = 1,
        tankId = 1,
        name = "Monitor #1",
        macAddress = "6825DDF0A9C0"
    )

    val sensor = Sensor(
        id = 1,
        deviceId = 1,
        model = "DS18B20",
        romAddress = "28FF70B85B64C87B",
        displayName = "Water Temperature",
        deviceName = PreviewData.device.name,
        tankName = "35G Tall" // TODO: Create tank preview
    )

    val statsData = listOf(
        StatData("22 C", StatType.Min),
        StatData("25 C", StatType.Average),
        StatData("28 C", StatType.Max),
    )

    val sensorConfig = SensorConfig(
        id = 1,
        updateIntervalSeconds = 30,
        notificationsEnabled = true,
        minValue = 22.0,
        maxValue = 28.0
    )

    val readings: List<Reading> = buildList {
        val now = Clock.System.now()
        var temperature = 24.0
        for (i in 2880 downTo 0) { // 2880 30 second intervals in 24 hours
            temperature += (Random.nextDouble() - 0.5) * 0.3 // small random walk
            temperature = temperature.coerceIn(22.0, 28.0)   // keep it realistic
            add(
                Reading(
                    time = now - i.times(30).seconds,
                    measurementType = MeasurementType.Temperature,
                    value = temperature,
                    macAddress = "6825DDF0A9C0",
                    sensorModel = "DS18B20"
                )
            )
        }
    }

    val readingsEmpty = emptyList<Reading>()

    val sensorDetailUiState = SensorDetailUiState(
        sensor = PreviewData.sensor,
        readings = PreviewData.readings,
        stats = PreviewData.statsData,
        config = PreviewData.sensorConfig,
        error = null,
        isLoading = false
    )
}