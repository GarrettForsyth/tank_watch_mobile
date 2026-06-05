package com.garrettforsyth.tankwatch.ui.util

import android.icu.util.MeasureUnit
import androidx.core.os.LocaleListCompat
import androidx.core.text.util.LocalePreferences
import androidx.core.text.util.LocalePreferences.TemperatureUnit
import com.garrettforsyth.tankwatch.data.model.MeasurementType
import java.util.Locale

object ValueFormatter {
    fun format(value: Double, measurementType: MeasurementType): String {
        return when (measurementType) {
            MeasurementType.Temperature -> TemperatureFormatter.format(value)
            else -> "%.1f".format(value)
        }
    }

    fun unitLabel(measurementType: MeasurementType): String {
        val locale = LocaleListCompat.getAdjustedDefault().get(0) ?: Locale.getDefault()
        val unit = LocalePreferences.getTemperatureUnit(locale)
        return when (unit) {
            TemperatureUnit.FAHRENHEIT -> "°F"
            TemperatureUnit.CELSIUS -> "°C"
            TemperatureUnit.KELVIN -> "°K"
            else  -> "°C"
        }
    }
}