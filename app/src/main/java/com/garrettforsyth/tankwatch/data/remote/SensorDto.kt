package com.garrettforsyth.tankwatch.data.remote

import com.garrettforsyth.tankwatch.data.model.Sensor
import kotlinx.serialization.Serializable

@Serializable
    data class SensorDto(
val id: Int,
val deviceId: Int,
val model: String,
val romAddress: String,
val displayName: String? = null,
val deviceName: String? = null,
val tankName: String? = null
)

fun SensorDto.toModel() = Sensor(
    id = id,
    deviceId = deviceId,
    model = model,
    romAddress = romAddress,
    displayName = displayName,
    deviceName = deviceName,
    tankName = tankName
)