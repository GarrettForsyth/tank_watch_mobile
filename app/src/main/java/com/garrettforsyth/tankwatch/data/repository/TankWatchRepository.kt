package com.garrettforsyth.tankwatch.data.repository

import com.garrettforsyth.tankwatch.data.model.Device
import com.garrettforsyth.tankwatch.data.model.Reading
import com.garrettforsyth.tankwatch.data.model.Sensor
import com.garrettforsyth.tankwatch.data.model.SensorConfig
import com.garrettforsyth.tankwatch.util.Resource

interface TankWatchRepository {
    suspend fun getDevices(): Resource<List<Device>>
    suspend fun getSensors(deviceId: Int): Resource<List<Sensor>>
    suspend fun getReadings(
        deviceId: Int,
        sensorId: Int,
        range: String = "24h"
    ): Resource<List<Reading>>

    suspend fun getSensorConfig(sensorId: Int): Resource<SensorConfig>
}