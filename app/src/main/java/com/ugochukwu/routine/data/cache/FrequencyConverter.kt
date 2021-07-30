package com.ugochukwu.routine.data.cache

import androidx.room.TypeConverter
import com.ugochukwu.routine.data.model.Frequency

class FrequencyConverter {
    @TypeConverter
    fun fromLong(value: Long?): Frequency? {
        return value?.let { Frequency.getFrequency(it) }
    }

    @TypeConverter
    fun toLong(frequency: Frequency?): Long? {
        return frequency?.value
    }
}