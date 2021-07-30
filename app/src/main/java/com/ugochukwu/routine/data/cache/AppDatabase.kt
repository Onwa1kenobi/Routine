package com.ugochukwu.routine.data.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ugochukwu.routine.data.model.RoutineEntity

@Database(
    entities = [(RoutineEntity::class)],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    FrequencyConverter::class
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun routineDao(): RoutineDAO
}