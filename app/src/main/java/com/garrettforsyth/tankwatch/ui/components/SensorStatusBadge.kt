package com.garrettforsyth.tankwatch.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.garrettforsyth.tankwatch.R
import com.garrettforsyth.tankwatch.data.model.SensorConfig
import com.garrettforsyth.tankwatch.ui.theme.Dimensions

@Composable
fun SensorStatusBadge(
    status: SensorStatus
) {
    val (label, color) = when (status) {
        SensorStatus.Normal  -> stringResource(R.string.normal) to MaterialTheme.colorScheme.tertiary
        SensorStatus.Warning -> stringResource(R.string.warning) to MaterialTheme.colorScheme.error
    }
    Surface(
        shape = MaterialTheme.shapes.small,
        color = color.copy(alpha = Dimensions.Opacity.medium),
        contentColor = color
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(
                horizontal = Dimensions.Padding.small,
                vertical = Dimensions.Padding.xSmall
            )
        )
    }
}

enum class SensorStatus { Normal, Warning }
fun deriveSensorStatus(value: Double?, config: SensorConfig?): SensorStatus? {
    if (value == null || config == null) return null
    return when {
        value < config.minValue || value > config.maxValue -> SensorStatus.Warning
        else -> SensorStatus.Normal
    }
}