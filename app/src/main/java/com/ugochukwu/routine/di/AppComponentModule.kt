package com.ugochukwu.routine.di

import com.ugochukwu.routine.alarm.AlarmManager
import com.ugochukwu.routine.alarm.AlarmManagerImpl
import com.ugochukwu.routine.data.DataSource
import com.ugochukwu.routine.data.RoutineRepository
import com.ugochukwu.routine.data.RoutineRepositoryImpl
import com.ugochukwu.routine.data.cache.LocalDataSource
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class AppComponentModule {
    @Provides
    fun provideAlarmManager(alarmManager: AlarmManagerImpl): AlarmManager = alarmManager
}