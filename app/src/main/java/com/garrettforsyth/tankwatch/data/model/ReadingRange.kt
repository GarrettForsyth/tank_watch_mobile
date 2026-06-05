package com.garrettforsyth.tankwatch.data.model

import com.garrettforsyth.tankwatch.R

/* Reading ranges recognized by the server */
enum class ReadingRange(private val value: String, val displayStringResId: Int) {
    ONE_HOUR("1h", R.string.range_one_hour),
    SIX_HOUR("6h", R.string.range_six_hours),
    ONE_DAY("24h", R.string.range_one_day),
    ONE_WEEK("7d", R.string.range_one_week),
    ONE_MONTH("30d", R.string.range_one_month);

    override fun toString() = value;
}
