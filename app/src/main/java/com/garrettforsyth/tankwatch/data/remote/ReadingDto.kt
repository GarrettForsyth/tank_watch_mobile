package com.garrettforsyth.tankwatch.data.remote

import com.garrettforsyth.tankwatch.data.model.Reading
import com.garrettforsyth.tankwatch.data.model.Sensor
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
data class ReadingDto(
    val time: Instant,
    val measurementType: String,
    val value: Double,
    val macAddress: String,
    val sensorModel: String
)

fun ReadingDto.toModel() = Reading(
    time = time,
    measurementType = measurementType,
    value = value,
    macAddress = macAddress,
    sensorModel = sensorModel )