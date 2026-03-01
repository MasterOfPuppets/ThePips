package com.masterofpuppets.thepips.feature.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masterofpuppets.thepips.domain.audio.PipAudioPlayer
import com.masterofpuppets.thepips.domain.model.Pip
import com.masterofpuppets.thepips.domain.model.PipSchedule
import com.masterofpuppets.thepips.domain.repository.AppPreferencesRepository
import com.masterofpuppets.thepips.domain.repository.PipRepository
import com.masterofpuppets.thepips.feature.background.PipAlarmScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val pipRepository: PipRepository,
    private val pipAudioPlayer: PipAudioPlayer,
    private val appPreferencesRepository: AppPreferencesRepository,
    private val pipAlarmScheduler: PipAlarmScheduler
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    val isMasterSwitchOn: StateFlow<Boolean> = appPreferencesRepository.isGlobalPipsEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    init {
        observeSchedules()
        observePips()
    }

    private fun observeSchedules() {
        pipRepository.getAllSchedules()
            .onEach { schedulesList ->
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        schedules = schedulesList
                    )
                }
            }
            .catch {
                _uiState.update { currentState ->
                    currentState.copy(isLoading = false)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun observePips() {
        pipRepository.getAllPips()
            .onEach { pipsList ->
                _uiState.update { currentState ->
                    currentState.copy(
                        hasPips = pipsList.isNotEmpty()
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun toggleMasterSwitch(isOn: Boolean) {
        viewModelScope.launch {
            appPreferencesRepository.setGlobalPipsEnabled(isOn)
            if (isOn) {
                pipAlarmScheduler.scheduleNextPip()
            } else {
                pipAlarmScheduler.cancelAlarm()
            }
        }
    }

    fun deleteSchedule(schedule: PipSchedule) {
        viewModelScope.launch {
            pipRepository.deleteSchedule(schedule.schedule)
        }
    }

    fun playPip(pip: Pip) {
        pipAudioPlayer.playPip(pip)
    }

    fun openEditor(scheduleId: Long? = null) {
        _uiState.update { currentState ->
            currentState.copy(
                isEditorOpen = true,
                selectedScheduleId = scheduleId
            )
        }
    }

    fun closeEditor() {
        _uiState.update { currentState ->
            currentState.copy(
                isEditorOpen = false,
                selectedScheduleId = null
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        pipAudioPlayer.stop()
    }
}