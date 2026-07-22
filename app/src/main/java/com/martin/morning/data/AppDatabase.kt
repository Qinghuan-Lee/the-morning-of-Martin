package com.martin.morning.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [AlarmEntity::class, SongEntity::class, CategoryEntity::class, AlarmLogEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun alarmDao(): AlarmDao
    abstract fun songDao(): SongDao
    abstract fun categoryDao(): CategoryDao
    abstract fun alarmLogDao(): AlarmLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "martin_morning.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
