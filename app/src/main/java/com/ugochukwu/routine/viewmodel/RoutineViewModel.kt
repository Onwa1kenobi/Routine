package com.ugochukwu.routine.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.ugochukwu.routine.RoutinesAdapter
import com.ugochukwu.routine.alarm.AlarmManager
import com.ugochukwu.routine.data.RoutineRepository
import com.ugochukwu.routine.data.model.Frequency
import com.ugochukwu.routine.data.model.Routine
import com.ugochukwu.routine.getRoutineTimeForComparison
import com.ugochukwu.routine.isUpNext
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@HiltViewModel
class RoutineViewModel @Inject constructor(
    private val repository: RoutineRepository,
    private val alarmManager: AlarmManager
) : ViewModel(),
    CoroutineScope {
    override val coroutineContext: CoroutineContext = Dispatchers.IO

    // LiveData object for view state interaction
    val routineViewContract: MutableLiveData<Event<RoutineViewContract>> = MutableLiveData()

    @ExperimentalPagingApi
    fun getRoutines(): Flow<PagingData<RoutinesAdapter.UiRoutineModel>> {
        return repository.getRoutines()
            .map { pagingData ->
                pagingData.map {
                    val routine =
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
                    RoutinesAdapter.UiRoutineModel.RoutineItem(routine) as RoutinesAdapter.UiRoutineModel
                }
            }
            .cachedIn(viewModelScope)
    }

    @ExperimentalPagingApi
    fun getNextUpRoutines(): Flow<PagingData<RoutinesAdapter.UiRoutineModel>> {
        return repository.getRoutines()
            .map { pagingData ->
                pagingData
                    .map {
                        val routine =
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
                        RoutinesAdapter.UiRoutineModel.RoutineItem(routine) as RoutinesAdapter.UiRoutineModel
                    }.filter {
                        isUpNext((it as RoutinesAdapter.UiRoutineModel.RoutineItem).routine)
                    }
            }
            .cachedIn(viewModelScope)
    }

    fun saveRoutine(
        title: String,
        description: String,
        hourOfDay: Int,
        minuteOfHour: Int,
        frequency: Frequency
    ) {
        val referenceCalendar = Calendar.getInstance()
        val calendar = Calendar.getInstance()
        calendar.clear()
        calendar.set(Calendar.DAY_OF_MONTH, referenceCalendar.get(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.MONTH, referenceCalendar.get(Calendar.MONTH))
        calendar.set(Calendar.YEAR, referenceCalendar.get(Calendar.YEAR))
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minuteOfHour)

        val routine = Routine(
            title = title,
            description = description,
            hourOfDay = hourOfDay,
            minuteOfHour = minuteOfHour,
            frequency = frequency,
            lastRunTime = calendar.timeInMillis,
        )

        launch {
            val id = repository.saveRoutine(routine)

            if (id != 0L) {
                routine.apply {
                    this.id = id.toInt()
                }
                alarmManager.registerAlarm(routine)
                // Successfully saved. return to routines
                routineViewContract.postValue(Event(RoutineViewContract.SaveSuccess))
            } else {
                // Saved failed, show message
                routineViewContract.postValue(Event(RoutineViewContract.MessageDisplay("Failed to save routine")))
            }
        }
    }

    val routine: LiveData<Routine>
        get() = _routine
    private val _routine = MutableLiveData<Routine>()

    fun getRoutine(id: Int) {
        viewModelScope.launch(Dispatchers.IO) { _routine.postValue(repository.getRoutine(id)) }
    }

    fun markRoutineAsDone(routine: Routine) {
        viewModelScope.launch(Dispatchers.IO) {
            val runTimeUpdate = getRoutineTimeForComparison(routine)
            repository.updateLastRunTime(runTimeUpdate, routine.id)
            repository.incrementDoneCount(routine.id)
            getRoutine(routine.id)
        }
    }

    fun updateRoutine(
        routine: Routine, title: String,
        description: String,
        hourOfDay: Int,
        minuteOfHour: Int,
        frequency: Frequency
    ) {
        val referenceCalendar = Calendar.getInstance()
        val calendar = Calendar.getInstance()
        calendar.clear()
        calendar.set(Calendar.DAY_OF_MONTH, referenceCalendar.get(Calendar.DAY_OF_MONTH))
        calendar.set(Calendar.MONTH, referenceCalendar.get(Calendar.MONTH))
        calendar.set(Calendar.YEAR, referenceCalendar.get(Calendar.YEAR))
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minuteOfHour)

        routine.apply {
            this.title = title
            this.description = description
            this.hourOfDay = hourOfDay
            this.minuteOfHour = minuteOfHour
            this.frequency = frequency
            this.lastRunTime = calendar.timeInMillis
        }

        launch {
            val id = repository.saveRoutine(routine)

            if (id != 0L) {
                alarmManager.cancelMissRoutineAlarm(routine.id)
                alarmManager.registerAlarm(routine)
                // Successfully saved. return to routines
                routineViewContract.postValue(Event(RoutineViewContract.SaveSuccess))
            } else {
                // Saved failed, show message
                routineViewContract.postValue(Event(RoutineViewContract.MessageDisplay("Failed to update routine")))
            }
        }
    }

    fun deleteRoutine(routine: Routine) {
        launch {
            repository.deleteRoutine(routine)
            alarmManager.cancelAlarm(routine)
            alarmManager.cancelMissRoutineAlarm(routine.id)
        }
    }
}