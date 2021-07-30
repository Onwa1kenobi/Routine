package com.ugochukwu.routine.data

import androidx.paging.PagingData
import com.ugochukwu.routine.data.model.Routine
import com.ugochukwu.routine.data.model.RoutineEntity
import kotlinx.coroutines.flow.Flow

interface RoutineRepository {

    /**
     * Gets paged list of all the registered routines from the data source
     */
    fun getRoutines(): Flow<PagingData<RoutineEntity>>

    /**
     * Gets all the registered routines from the data source
     */
    suspend fun getAllRoutines(): List<Routine>

    /**
     * Gets the routine with the id passed from the data source
     */
    suspend fun getRoutine(id: Int): Routine

    /**
     * Saves a routine to the data source
     */
    suspend fun saveRoutine(routine: Routine): Long

    /**
     * Deletes a routine from the data source
     */
    suspend fun deleteRoutine(routine: Routine)

    /**
     * Updates a routine's last run time to the closest
     */
    fun updateLastRunTime(runTime: Long, id: Int)

    /**
     * Increments a routine's done count
     */
    suspend fun incrementDoneCount(id: Int)

    /**
     * Increments a routine's miss count
     */
    suspend fun incrementMissCount(id: Int)
}