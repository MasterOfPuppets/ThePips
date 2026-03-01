package com.masterofpuppets.thepips.domain.model

data class Pip(
    val id: Long = 0,
    val name: String,
    val isActive: Boolean = true,
    val globalVolume: Float = 1.0f,
    val slot1Hz: Int = 0,
    val slot1DurationMs: Int = 0,
    val slot2Hz: Int = 0,
    val slot2DurationMs: Int = 0,
    val slot3Hz: Int = 0,
    val slot3DurationMs: Int = 0,
    val slot4Hz: Int = 0,
    val slot4DurationMs: Int = 0,
    val slot5Hz: Int = 0,
    val slot5DurationMs: Int = 0,
    val slot6Hz: Int = 0,
    val slot6DurationMs: Int = 0,
    val slot7Hz: Int = 0,
    val slot7DurationMs: Int = 0,
    val slot8Hz: Int = 0,
    val slot8DurationMs: Int = 0
)