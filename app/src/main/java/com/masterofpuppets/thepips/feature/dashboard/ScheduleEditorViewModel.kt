package com.masterofpuppets.thepips.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masterofpuppets.thepips.domain.model.Pip
import com.masterofpuppets.thepips.domain.model.Schedule
import com.masterofpuppets.thepips.domain.repository.PipRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScheduleEditorViewModel @Inject constructor(
    private val pipRepository: PipRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScheduleEditorUiState())
    val uiState: StateFlow<ScheduleEditorUiState> = _uiState.asStateFlow()

    val availablePips: StateFlow<List<Pip>> = pipRepository.getAllPips()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun loadSchedule(scheduleId: Long?) {
        if (scheduleId == null) {
            _uiState.value = ScheduleEditorUiState(
                minutesMask = 1L, // Default: Top of the hour
                daysOfWeekMask = 127
            )
            return
        }

        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val schedule = pipRepository.getScheduleById(scheduleId)
            schedule?.let { loadedSchedule ->
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        scheduleId = loadedSchedule.id,
                        selectedPipId = loadedSchedule.pipId,
                        minutesMask = loadedSchedule.minuteMask,
                        daysOfWeekMask = loadedSchedule.daysOfWeekMask,
                        startHour = loadedSchedule.startHour,
                        endHour = loadedSchedule.endHour
                    )
                }
            } ?: run {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onPipSelected(pipId: Long) {
        _uiState.update { it.copy(selectedPipId = pipId) }
    }

    fun toggleDay(dayIndex: Int) {
        // Toggle specific bit (0 = Monday, 6 = Sunday)
        _uiState.update { state ->
            val newMask = state.daysOfWeekMask xor (1 shl dayIndex)
            state.copy(daysOfWeekMask = newMask)
        }
    }

    fun toggleMinute(minute: Int) {
        // Toggle specific bit (0 to 59)
        _uiState.update { state ->
            val newMask = state.minutesMask xor (1L shl minute)
            state.copy(minutesMask = newMask)
        }
    }

    fun applyMinutePresetTopHour() {
        _uiState.update { it.copy(minutesMask = 1L) }
    }

    fun applyMinutePresetHalfHours() {
        _uiState.update { it.copy(minutesMask = (1L) or (1L shl 30)) }
    }

    fun applyMinutePresetQuarters() {
        _uiState.update { it.copy(minutesMask = (1L) or (1L shl 15) or (1L shl 30) or (1L shl 45)) }
    }

    fun onStartHourChanged(hour: Int) {
        _uiState.update { it.copy(startHour = hour) }
    }

    fun onEndHourChanged(hour: Int) {
        _uiState.update { it.copy(endHour = hour) }
    }

    fun saveSchedule() {
        val currentState = _uiState.value
        val pipId = currentState.selectedPipId ?: return

        // Validation: Prevent saving if no minutes are selected
        if (currentState.minutesMask == 0L) return

        val schedule = Schedule(
            id = currentState.scheduleId ?: 0L,
            pipId = pipId,
            minuteMask = currentState.minutesMask,
            daysOfWeekMask = currentState.daysOfWeekMask,
            startHour = currentState.startHour,
            endHour = currentState.endHour
        )

        viewModelScope.launch {
            if (schedule.id == 0L) {
                pipRepository.insertSchedule(schedule)
            } else {
                pipRepository.updateSchedule(schedule)
            }
            _uiState.update { it.copy(isSaveSuccessful = true) }
        }
    }

    fun resetSaveState() {
        _uiState.update { it.copy(isSaveSuccessful = false) }
    }
}