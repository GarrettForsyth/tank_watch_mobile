package com.garrettforsyth.tankwatch.data.model

data class SensorConfig(
    val id: Int,
    val updateIntervalSeconds: Int,
    val notificationsEnabled: Boolean,
    val minValue: Double,
    val maxValue: Double
)