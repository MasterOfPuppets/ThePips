package com.masterofpuppets.thepips.feature.dashboard

data class ScheduleEditorUiState(
    val scheduleId: Long? = null,
    val selectedPipId: Long? = null,
    val daysOfWeekMask: Int = 127, // 127 represents all 7 days active by default
    val minutesMask: Long = 0L,
    val startHour: Int = 0,
    val endHour: Int = 23,
    val isLoading: Boolean = false,
    val isSaveSuccessful: Boolean = false
)