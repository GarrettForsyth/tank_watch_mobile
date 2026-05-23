package com.garrettforsyth.tankwatch.data.model

data class Sensor(
    val id: Int,
    val deviceId: Int,
    val model: String,
    val romAddress: String,
    val displayName: String?,
    val deviceName: String?,
    val tankName: String?
)