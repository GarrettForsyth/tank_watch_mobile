package com.garrettforsyth.tankwatch.ui.util

import android.health.connect.datatypes.units.Temperature
import android.icu.util.Measure
import android.icu.util.MeasureUnit
import androidx.core.os.LocaleListCompat
import androidx.core.text.util.LocalePreferences
import androidx.core.text.util.LocalePreferences.TemperatureUnit
import java.util.Locale

/**
 * The server stores temperature in Celsius; Format it to the phone's locale
 */
object TemperatureFormatter {
    fun format(celsius: Double): String {
        val locale = LocaleListCompat.getAdjustedDefault().get(0) ?: Locale.getDefault()
        val (value, unit) = if (LocalePreferences.getTemperatureUnit(locale) == TemperatureUnit.FAHRENHEIT) {
            Pair((celsius * 9.0 / 5.0 + 32.0), MeasureUnit.FAHRENHEIT)
        } else {
            Pair(celsius, MeasureUnit.CELSIUS)
        } // Sorry Kelvin lovers
        val measure = Measure(value, unit)
        return android.icu.text.MeasureFormat
            .getInstance(locale, android.icu.text.MeasureFormat.FormatWidth.SHORT)
            .format(measure)
    }
}
