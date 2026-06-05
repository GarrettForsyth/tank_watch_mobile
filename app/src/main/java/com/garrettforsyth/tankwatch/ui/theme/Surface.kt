package com.garrettforsyth.tankwatch.ui.theme

import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

object Surface {
    @Composable
    fun tankCardColors() = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
    )

    @Composable
    fun sensorCardColors() = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
    )
}