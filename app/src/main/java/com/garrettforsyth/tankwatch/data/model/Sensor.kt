package com.garrettforsyth.tankwatch.data.model

import kotlin.time.Instant

data class Sensor(
    val id: Int,
    val deviceId: Int,
    val model: String,
    val romAddress: String,
    val displayName: String?,
)