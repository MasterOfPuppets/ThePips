package com.masterofpuppets.thepips.feature.dashboard

import com.masterofpuppets.thepips.domain.model.PipSchedule

data class DashboardUiState(
    val isLoading: Boolean = true,
    val schedules: List<PipSchedule> = emptyList(),
    val hasPips: Boolean = false,
    val isEditorOpen: Boolean = false,
    val selectedScheduleId: Long? = null
)