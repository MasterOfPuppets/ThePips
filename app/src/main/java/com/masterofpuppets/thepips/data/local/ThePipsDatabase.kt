package com.masterofpuppets.thepips.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [PipEntity::class, ScheduleEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ThePipsDatabase : RoomDatabase() {
    abstract val pipDao: PipDao
    abstract val scheduleDao: ScheduleDao

    companion object {
        const val DATABASE_NAME = "thepips_db"
    }
}