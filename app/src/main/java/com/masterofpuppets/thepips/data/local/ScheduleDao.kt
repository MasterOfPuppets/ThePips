package com.masterofpuppets.thepips.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Embedded
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

data class ScheduleWithPip(
    @Embedded val schedule: ScheduleEntity,
    @Relation(
        parentColumn = "pipId",
        entityColumn = "id"
    )
    val pip: PipEntity
)

@Dao
interface ScheduleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: ScheduleEntity): Long

    @Update
    suspend fun updateSchedule(schedule: ScheduleEntity): Int

    @Delete
    suspend fun deleteSchedule(schedule: ScheduleEntity): Int

    // Retrieves all schedules and their respective Pips.
    // This is what the Dashboard will use to display the list of active rules.
    @Query("SELECT * FROM schedules_table")
    fun getAllSchedulesWithPips(): Flow<List<ScheduleWithPip>>

    @Query("SELECT * FROM schedules_table WHERE pipId = :pipId")
    fun getSchedulesForPip(pipId: Long): Flow<List<ScheduleEntity>>

    @Query("SELECT * FROM schedules_table WHERE id = :scheduleId")
    suspend fun getScheduleById(scheduleId: Long): ScheduleEntity?
}