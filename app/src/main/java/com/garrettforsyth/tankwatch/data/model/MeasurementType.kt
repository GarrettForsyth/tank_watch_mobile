package com.garrettforsyth.tankwatch.data.model

enum class MeasurementType {
    Temperature,
    Unknown;

    companion object {
        fun fromString(measurementTypeStr: String): MeasurementType {
            return when (measurementTypeStr.lowercase()) {
                "temperature" -> MeasurementType.Temperature
                else -> MeasurementType.Unknown
            }
        }
    }
}