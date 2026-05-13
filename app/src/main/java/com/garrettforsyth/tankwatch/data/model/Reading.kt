package com.garrettforsyth.tankwatch.data.model

import kotlin.time.Instant

data class Reading(
    val time: Instant,
    val measurementType: String,
    val value: Double,
    val macAddress: String,
    val sensorModel: String
)