package com.masterofpuppets.thepips.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "schedules_table",
    foreignKeys = [
        ForeignKey(
            entity = PipEntity::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("pipId"),
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["pipId"])]
)
data class ScheduleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val pipId: Long,
    val minuteMask: Long,
    val daysOfWeekMask: Int,
    val startHour: Int,
    val endHour: Int
)