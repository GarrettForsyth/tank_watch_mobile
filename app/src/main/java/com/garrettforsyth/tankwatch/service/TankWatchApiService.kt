package com.garrettforsyth.tankwatch.service

import com.garrettforsyth.tankwatch.data.model.ReadingRange
import com.garrettforsyth.tankwatch.data.remote.DeviceDto
import com.garrettforsyth.tankwatch.data.remote.ReadingDto
import com.garrettforsyth.tankwatch.data.remote.SensorConfigDto
import com.garrettforsyth.tankwatch.data.remote.SensorDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TankWatchApiService {

    @GET("api/devices")
    suspend fun getDevices(): List<DeviceDto>

    @GET("api/devices/{deviceId}/sensors")
    suspend fun getSensors(
        @Path("deviceId") deviceId: Int
    ): List<SensorDto>

    @GET("api/readings")
    suspend fun getReadings(
        @Query("sensorId") sensorId: Int,
        @Query("range") range: String = ReadingRange.ONE_HOUR.toString()
    ): List<ReadingDto>

    @GET("api/sensors/{sensorId}/config")
    suspend fun getSensorConfig(
        @Path("sensorId") sensorId: Int
    ): SensorConfigDto

    @GET("api/sensors/{sensorId}")
    suspend fun getSensor(
        @Path("sensorId") sensorId: Int
    ): SensorDto
}
