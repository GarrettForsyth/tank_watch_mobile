package com.garrettforsyth.tankwatch.navigation

import kotlinx.serialization.Serializable

/**
 * Defines destinations for the navigation.
 */
object Destinations {
    @Serializable object Main

    @Serializable data class SensorDetail(val sensorId: Int)
}