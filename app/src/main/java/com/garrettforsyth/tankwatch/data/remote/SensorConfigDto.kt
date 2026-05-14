package com.garrettforsyth.tankwatch.data.remote

import com.garrettforsyth.tankwatch.data.model.SensorConfig
import kotlinx.serialization.Serializable

@Serializable
data class SensorConfigDto(
    val id: Int,
    val updateIntervalSeconds: Int,
    val notificationsEnabled: Boolean,
    val minValue: Double,
    val maxValue: Double
)

fun SensorConfigDto.toModel() = SensorConfig(
    id = id,
    updateIntervalSeconds = updateIntervalSeconds,
    notificationsEnabled = notificationsEnabled,
    minValue = minValue,
    maxValue = maxValue,
)
