package com.ugochukwu.routine.data.model

enum class Frequency(val value: Long) {
    HOURLY(1000 * 60 * 60 * 1),
    DAILY(HOURLY.value * 24),
    WEEKLY(DAILY.value * 7),
    MONTHLY(WEEKLY.value * 4),
    YEARLY(MONTHLY.value * 12);

    companion object {
        fun getFrequency(value: Long): Frequency {
            return when (value) {
                HOURLY.value -> HOURLY
                DAILY.value -> DAILY
                WEEKLY.value -> WEEKLY
                MONTHLY.value -> MONTHLY
                YEARLY.value -> YEARLY
                else -> HOURLY
            }
        }
    }
}