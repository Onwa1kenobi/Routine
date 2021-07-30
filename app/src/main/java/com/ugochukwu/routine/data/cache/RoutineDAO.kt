package com.ugochukwu.routine.data.cache

import androidx.paging.PagingSource
import androidx.room.*
import com.ugochukwu.routine.data.model.RoutineEntity

@Dao
interface RoutineDAO {

    @Query("SELECT * FROM ROUTINES ORDER BY lastRunTime")
    fun getRoutines(): PagingSource<Int, RoutineEntity>

    @Query("SELECT * FROM ROUTINES ORDER BY lastRunTime")
    suspend fun getAllRoutines(): List<RoutineEntity>

    @Query("SELECT * FROM ROUTINES WHERE id = :id")
    fun getRoutine(id: Int): RoutineEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveRoutine(routine: RoutineEntity): Long

    @Delete
    suspend fun deleteRoutine(routine: RoutineEntity)

    @Query("UPDATE ROUTINES SET lastRunTime = :runTime WHERE id = :id")
    fun updateLastRunTime(runTime: Long, id: Int)

    suspend fun incrementDoneCount(id: Int) {
        val routine = getRoutine(id)
        routine.doneCount += 1
        saveRoutine(routine)
    }

    suspend fun incrementMissCount(id: Int) {
        val routine = getRoutine(id)
        routine.missCount += 1
        saveRoutine(routine)
    }
}