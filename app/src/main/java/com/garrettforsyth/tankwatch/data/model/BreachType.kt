package com.garrettforsyth.tankwatch.data.model

/**
 * The reason an alert could be triggered:
 *  MIN - the reading fell below the minimum threshold
 *  MAX - The reading fell above the maximum threshold
 */
enum class BreachType {
    Min, Max;

    companion object {
        fun fromString(value: String): BreachType?{
            return entries.firstOrNull {
                it.name.equals(value, ignoreCase = true)
            }
        }
    }
}