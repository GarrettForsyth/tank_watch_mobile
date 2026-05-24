package com.garrettforsyth.tankwatch.data.repository

import com.garrettforsyth.tankwatch.data.model.Device
import com.garrettforsyth.tankwatch.data.model.Reading
import com.garrettforsyth.tankwatch.data.model.ReadingRange
import com.garrettforsyth.tankwatch.data.model.Sensor
import com.garrettforsyth.tankwatch.data.model.SensorConfig
import com.garrettforsyth.tankwatch.data.remote.FcmTokenDto
import com.garrettforsyth.tankwatch.data.remote.toModel
import com.garrettforsyth.tankwatch.di.DispatcherModule
import com.garrettforsyth.tankwatch.service.TankWatchApiService
import com.garrettforsyth.tankwatch.util.Resource
import jakarta.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

class TankWatchRepositoryImpl @Inject constructor(
    private val api: TankWatchApiService,
    @param:DispatcherModule.IoDispatcher private val dispatcher: CoroutineDispatcher
) : TankWatchRepository {

    override suspend fun getDevices(): Resource<List<Device>> = safeApiCall {
        api.getDevices().map { it.toModel() }
    }

    override suspend fun getSensors(deviceId: Int): Resource<List<Sensor>> = safeApiCall {
        api.getSensors(deviceId).map { it.toModel() }
    }

    override suspend fun getReadings(
        sensorId: Int,
        range: ReadingRange
    ): Resource<List<Reading>> = safeApiCall {
        api.getReadings(sensorId, range.toString()).map { it.toModel() }
    }

    override suspend fun getSensorConfig(sensorId: Int): Resource<SensorConfig> = safeApiCall {
        api.getSensorConfig(sensorId).toModel()
    }

    override suspend fun getSensor(sensorId: Int): Resource<Sensor> = safeApiCall {
        api.getSensor(sensorId).toModel()
    }

    override suspend fun registerFcmToken(token: String): Resource<Unit> = safeApiCall {
        api.registerFcmToken(FcmTokenDto(token))
    }

    private suspend fun <T> safeApiCall(call: suspend () -> T): Resource<T> = withContext(dispatcher)  {
        return@withContext try {
            Resource.Success(call())
        } catch (e: HttpException) {
            Resource.Error("Server error ${e.code()}: ${e.message()}")
        } catch (e: IOException) {
            Resource.Error("Network error: check your connection")
        }
    }
}