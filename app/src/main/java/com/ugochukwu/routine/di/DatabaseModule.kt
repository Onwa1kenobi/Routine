package com.ugochukwu.routine.di

import android.content.Context
import androidx.room.Room
import com.ugochukwu.routine.data.DataSource
import com.ugochukwu.routine.data.RoutineRepository
import com.ugochukwu.routine.data.RoutineRepositoryImpl
import com.ugochukwu.routine.data.cache.AppDatabase
import com.ugochukwu.routine.data.cache.LocalDataSource
import com.ugochukwu.routine.data.cache.RoutineDAO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    fun provideRoutineRepository(repositoryImpl: RoutineRepositoryImpl): RoutineRepository {
        return repositoryImpl
    }

    @Provides
    fun bindLocalDataSource(localDataSource: LocalDataSource): DataSource = localDataSource

    @Provides
    fun provideRoutineDao(appDatabase: AppDatabase): RoutineDAO {
        return appDatabase.routineDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "Routine.db"
        ).build()
    }
}