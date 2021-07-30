package com.ugochukwu.routine.data

import androidx.paging.PagingSource
import com.ugochukwu.routine.data.model.RoutineEntity

/**
 * Interface defining methods for the data storage and retrieval operations related to Routines.
 * This is to be implemented by external data source layers (Remote and/or Cache), setting the requirements for the
 * operations that need to be implemented.
 */
interface DataSource {

    /**
     * Gets a paged list of all the registered routines from the local database
     */
    fun getRoutines(): PagingSource<Int, RoutineEntity>

    /**
     * Gets all the registered routines from the local database
     */
    suspend fun getAllRoutines(): List<RoutineEntity>

    /**
     * Gets the routine with the id passed from the data source
     */
    suspend fun getRoutine(id: Int): RoutineEntity

    /**
     * Saves a routine to the local database
     */
    suspend fun saveRoutine(routine: RoutineEntity): Long

    /**
     * Deletes a routine from the data source
     */
    suspend fun deleteRoutine(routine: RoutineEntity)

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