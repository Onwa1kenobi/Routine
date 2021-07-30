package com.ugochukwu.routine.alarm

import com.ugochukwu.routine.data.model.Routine

interface AlarmManager {
    fun registerAlarm(routine: Routine)
    fun cancelAlarm(routine: Routine)

    fun registerMissRoutineAlarm(routineId: Int)
    fun cancelMissRoutineAlarm(routineId: Int)
}