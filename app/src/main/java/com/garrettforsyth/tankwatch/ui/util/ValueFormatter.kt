package com.garrettforsyth.tankwatch.ui.util

import com.garrettforsyth.tankwatch.data.model.MeasurementType

object ValueFormatter {
    fun format(value: Double, measurementType: MeasurementType): String {
        return when (measurementType) {
            MeasurementType.Temperature -> TemperatureFormatter.format(value)
            else -> value.toString()
        }
    }
}