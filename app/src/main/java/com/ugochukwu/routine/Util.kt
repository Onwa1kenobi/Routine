package com.ugochukwu.routine

import com.ugochukwu.routine.data.model.Routine
import java.util.*
import kotlin.math.abs

fun isUpNext(routine: Routine): Boolean {
    return abs(Calendar.getInstance().timeInMillis - getRoutineTimeForComparison(routine)) < TWELVE_HOURS
}

fun getRoutineTimeForComparison(routine: Routine): Long {
    var timeToCompare = routine.lastRunTime
    while (timeToCompare < Calendar.getInstance().timeInMillis) {
        if (timeToCompare + routine.frequency.value > Calendar.getInstance().timeInMillis) {
            routine.lastRunTime = timeToCompare
        }
        timeToCompare += routine.frequency.value
    }

    if (timeToCompare != routine.lastRunTime) {
        // TODO(consider updating the time in the db?)
    }

    return timeToCompare
}

const val FIVE_MINUTES = (1000 * 60 * 5)
const val TEN_MINUTES = (1000 * 60 * 10)
const val TWELVE_HOURS = (1000 * 60 * 60 * 12)