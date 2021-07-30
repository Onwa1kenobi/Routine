package com.ugochukwu.routine.data.cache

import androidx.paging.PagingSource
import com.ugochukwu.routine.data.DataSource
import com.ugochukwu.routine.data.model.RoutineEntity
import javax.inject.Inject

class LocalDataSource @Inject constructor(private val routineDAO: RoutineDAO) : DataSource {

    override fun getRoutines(): PagingSource<Int, RoutineEntity> {
        return routineDAO.getRoutines()
    }

    override suspend fun getAllRoutines(): List<RoutineEntity> {
        return routineDAO.getAllRoutines()
    }

    override suspend fun getRoutine(id: Int): RoutineEntity {
        return routineDAO.getRoutine(id)
    }

    override suspend fun saveRoutine(routine: RoutineEntity): Long {
        return routineDAO.saveRoutine(routine)
    }

    override suspend fun deleteRoutine(routine: RoutineEntity) {
        routineDAO.deleteRoutine(routine)
    }

    override fun updateLastRunTime(runTime: Long, id: Int) {
        routineDAO.updateLastRunTime(runTime, id)
    }

    override suspend fun incrementDoneCount(id: Int) {
        routineDAO.incrementDoneCount(id)
    }

    override suspend fun incrementMissCount(id: Int) {
        routineDAO.incrementMissCount(id)
    }
}