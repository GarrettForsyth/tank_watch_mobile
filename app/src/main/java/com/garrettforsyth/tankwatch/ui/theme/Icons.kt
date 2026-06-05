package com.garrettforsyth.tankwatch.ui.theme

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DeviceUnknown
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.ui.graphics.vector.ImageVector
import com.garrettforsyth.tankwatch.data.model.MeasurementType

object Icons {
    fun measurementTypeIcon(measurementType: MeasurementType): ImageVector = when (measurementType) {
        MeasurementType.Temperature -> Icons.Outlined.Thermostat
        MeasurementType.Unknown -> Icons.Outlined.DeviceUnknown
    }
}