package com.ugochukwu.routine.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routines")
data class RoutineEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var title: String,
    var description: String,
    var hourOfDay: Int,
    var minuteOfHour: Int,
    var frequency: Frequency,
    var lastRunTime: Long,
    var doneCount: Int,
    var missCount: Int,
)