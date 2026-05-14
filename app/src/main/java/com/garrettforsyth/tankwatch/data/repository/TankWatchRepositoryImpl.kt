package com.garrettforsyth.tankwatch.data.repository

import com.garrettforsyth.tankwatch.data.model.Device
import com.garrettforsyth.tankwatch.data.model.Reading
import com.garrettforsyth.tankwatch.data.model.Sensor
import com.garrettforsyth.tankwatch.data.remote.toModel
import com.garrettforsyth.tankwatch.service.TankWatchApiService
import com.garrettforsyth.tankwatch.util.Resource
import jakarta.inject.Inject
import retrofit2.HttpException
import java.io.IOException

class TankWatchRepositoryImpl @Inject constructor(
    private val api: TankWatchApiService
) : TankWatchRepository {

    override suspend fun getDevices(): Resource<List<Device>> = safeApiCall {
        api.getDevices().map { it.toModel() }
    }

    override suspend fun getSensors(deviceId: Int): Resource<List<Sensor>> = safeApiCall {
        api.getSensors(deviceId).map { it.toModel() }
    }

    override suspend fun getReadings(
        deviceId: Int,
        sensorId: Int,
        range: String
    ): Resource<List<Reading>> = safeApiCall {
        api.getReadings(deviceId, sensorId, range).map { it.toModel() }
    }

    private suspend fun <T> safeApiCall(call: suspend () -> T): Resource<T> {
        return try {
            Resource.Success(call())
        } catch (e: HttpException) {
            Resource.Error("Server error ${e.code()}: ${e.message()}")
        } catch (e: IOException) {
            Resource.Error("Network error: check your connection")
        }
    }
}