package com.garrettforsyth.tankwatch.service

import com.garrettforsyth.tankwatch.data.remote.DeviceDto
import com.garrettforsyth.tankwatch.data.remote.ReadingDto
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
        @Query("deviceId") deviceId: Int,
        @Query("romAddress") romAddress: String,
        @Query("range") range: String = "24h"
    ): List<ReadingDto>
}
