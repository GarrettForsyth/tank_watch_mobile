package com.garrettforsyth.tankwatch.data.model

/* Reading ranges recognized by the server */
enum class ReadingRange(private val value: String) {
    ONE_HOUR("1h"),
    SIX_HOUR("6h"),
    ONE_DAY("24h"),
    ONE_WEEK("7d"),
    ONE_MONTH("30d");

    override fun toString() = value;
}
