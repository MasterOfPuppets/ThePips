package com.masterofpuppets.thepips.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pips_table")
data class PipEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val isActive: Boolean,
    val globalVolume: Float,
    val slot1Hz: Int,
    val slot1DurationMs: Int,
    val slot2Hz: Int,
    val slot2DurationMs: Int,
    val slot3Hz: Int,
    val slot3DurationMs: Int,
    val slot4Hz: Int,
    val slot4DurationMs: Int,
    val slot5Hz: Int,
    val slot5DurationMs: Int,
    val slot6Hz: Int,
    val slot6DurationMs: Int,
    val slot7Hz: Int,
    val slot7DurationMs: Int,
    val slot8Hz: Int,
    val slot8DurationMs: Int
)