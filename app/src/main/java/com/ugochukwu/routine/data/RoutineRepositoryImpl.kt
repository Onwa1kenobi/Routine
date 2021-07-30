package com.ugochukwu.routine.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.ugochukwu.routine.data.model.Routine
import com.ugochukwu.routine.data.model.RoutineEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Provides an implementation of the [RoutineRepository] interface for communicating to and from
 * Data sources
 */
class RoutineRepositoryImpl @Inject constructor(private val localDataSource: DataSource) :
    RoutineRepository {

    override fun getRoutines(): Flow<PagingData<RoutineEntity>> {
        val pagingSourceFactory = { localDataSource.getRoutines() }
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                prefetchDistance = 5,
                enablePlaceholders = false
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    override suspend fun getAllRoutines(): List<Routine> {
        return localDataSource.getAllRoutines().map {
            Routine(
                it.id,
                it.title,
                it.description,
                it.hourOfDay,
                it.minuteOfHour,
                it.frequency,
                it.lastRunTime,
                it.doneCount,
                it.missCount,
            )
        }
    }

    override suspend fun getRoutine(id: Int): Routine {
        val routineEntity = localDataSource.getRoutine(id)
        return Routine(
            routineEntity.id,
            routineEntity.title,
            routineEntity.description,
            routineEntity.hourOfDay,
            routineEntity.minuteOfHour,
            routineEntity.frequency,
            routineEntity.lastRunTime,
            routineEntity.doneCount,
            routineEntity.missCount,
        )
    }

    override suspend fun saveRoutine(routine: Routine): Long {
        val routineEntity = RoutineEntity(
            routine.id,
            routine.title,
            routine.description,
            routine.hourOfDay,
            routine.minuteOfHour,
            routine.frequency,
            routine.lastRunTime,
            routine.doneCount,
            routine.missCount,
        )
        return localDataSource.saveRoutine(routineEntity)
    }

    override suspend fun deleteRoutine(routine: Routine) {
        val routineEntity = RoutineEntity(
            routine.id,
            routine.title,
            routine.description,
            routine.hourOfDay,
            routine.minuteOfHour,
            routine.frequency,
            routine.lastRunTime,
            routine.doneCount,
            routine.missCount,
        )
        localDataSource.deleteRoutine(routineEntity)
    }

    override fun updateLastRunTime(runTime: Long, id: Int) {
        localDataSource.updateLastRunTime(runTime, id)
    }

    override suspend fun incrementDoneCount(id: Int) {
        localDataSource.incrementDoneCount(id)
    }

    override suspend fun incrementMissCount(id: Int) {
        localDataSource.incrementMissCount(id)
    }
}