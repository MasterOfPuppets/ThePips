package com.masterofpuppets.thepips.feature.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masterofpuppets.thepips.domain.audio.PipAudioPlayer
import com.masterofpuppets.thepips.domain.model.DefaultPipPresets
import com.masterofpuppets.thepips.domain.model.Pip
import com.masterofpuppets.thepips.domain.model.PipPreset
import com.masterofpuppets.thepips.domain.repository.PipRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @Inject constructor(
    private val pipRepository: PipRepository,
    private val pipAudioPlayer: PipAudioPlayer
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditorUiState())
    val uiState: StateFlow<EditorUiState> = _uiState.asStateFlow()

    private var originalState = EditorUiState()

    // Exposes the static presets to the UI
    val availablePresets: List<PipPreset> = DefaultPipPresets.presets

    fun loadPip(id: Long?) {
        if (id == null) {
            val defaultState = EditorUiState()
            _uiState.value = defaultState
            originalState = defaultState
            return
        }

        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            val pip = pipRepository.getPipById(id)
            pip?.let { loadedPip ->
                val loadedState = EditorUiState(
                    isLoading = false,
                    pipId = loadedPip.id,
                    name = loadedPip.name,
                    isActive = loadedPip.isActive,
                    globalVolume = loadedPip.globalVolume,
                    frequencies = listOf(
                        loadedPip.slot1Hz, loadedPip.slot2Hz, loadedPip.slot3Hz, loadedPip.slot4Hz,
                        loadedPip.slot5Hz, loadedPip.slot6Hz, loadedPip.slot7Hz, loadedPip.slot8Hz
                    ),
                    durations = listOf(
                        loadedPip.slot1DurationMs,
                        loadedPip.slot2DurationMs,
                        loadedPip.slot3DurationMs,
                        loadedPip.slot4DurationMs,
                        loadedPip.slot5DurationMs,
                        loadedPip.slot6DurationMs,
                        loadedPip.slot7DurationMs,
                        loadedPip.slot8DurationMs
                    )
                )
                _uiState.value = loadedState
                originalState = loadedState
            } ?: run {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun checkForUnsavedChanges(currentState: EditorUiState): Boolean {
        return currentState.name != originalState.name ||
                currentState.isActive != originalState.isActive ||
                currentState.globalVolume != originalState.globalVolume ||
                currentState.frequencies != originalState.frequencies ||
                currentState.durations != originalState.durations
    }

    fun onNameChange(newName: String) {
        _uiState.update { state ->
            val newState = state.copy(name = newName)
            newState.copy(hasUnsavedChanges = checkForUnsavedChanges(newState))
        }
    }

    fun onActiveChange(isActive: Boolean) {
        _uiState.update { state ->
            val newState = state.copy(isActive = isActive)
            newState.copy(hasUnsavedChanges = checkForUnsavedChanges(newState))
        }
    }

    fun onVolumeChange(newVolume: Float) {
        _uiState.update { state ->
            val newState = state.copy(globalVolume = newVolume)
            newState.copy(hasUnsavedChanges = checkForUnsavedChanges(newState))
        }
    }

    fun onFrequencyChange(index: Int, newFrequency: Int) {
        _uiState.update { state ->
            val updatedFrequencies = state.frequencies.toMutableList()
            updatedFrequencies[index] = newFrequency
            val newState = state.copy(frequencies = updatedFrequencies)
            newState.copy(hasUnsavedChanges = checkForUnsavedChanges(newState))
        }
    }

    fun onDurationChange(index: Int, newDuration: Int) {
        _uiState.update { state ->
            val updatedDurations = state.durations.toMutableList()
            updatedDurations[index] = newDuration
            val newState = state.copy(durations = updatedDurations)
            newState.copy(hasUnsavedChanges = checkForUnsavedChanges(newState))
        }
    }

    fun applyPreset(preset: PipPreset) {
        _uiState.update { state ->
            val newState = state.copy(
                frequencies = preset.frequencies,
                durations = preset.durations
            )
            newState.copy(hasUnsavedChanges = checkForUnsavedChanges(newState))
        }
    }

    fun resetToDefaults() {
        _uiState.update { state ->
            val newState = state.copy(
                frequencies = List(8) { 0 },
                durations = List(8) { 0 },
                globalVolume = 1.0f
            )
            newState.copy(hasUnsavedChanges = checkForUnsavedChanges(newState))
        }
    }

    fun playTest() {
        val currentState = _uiState.value

        val tempPip = Pip(
            id = currentState.pipId ?: 0L,
            name = currentState.name.ifBlank { "Test" },
            isActive = currentState.isActive,
            globalVolume = currentState.globalVolume,
            slot1Hz = currentState.frequencies.getOrElse(0) { 0 },
            slot1DurationMs = currentState.durations.getOrElse(0) { 0 },
            slot2Hz = currentState.frequencies.getOrElse(1) { 0 },
            slot2DurationMs = currentState.durations.getOrElse(1) { 0 },
            slot3Hz = currentState.frequencies.getOrElse(2) { 0 },
            slot3DurationMs = currentState.durations.getOrElse(2) { 0 },
            slot4Hz = currentState.frequencies.getOrElse(3) { 0 },
            slot4DurationMs = currentState.durations.getOrElse(3) { 0 },
            slot5Hz = currentState.frequencies.getOrElse(4) { 0 },
            slot5DurationMs = currentState.durations.getOrElse(4) { 0 },
            slot6Hz = currentState.frequencies.getOrElse(5) { 0 },
            slot6DurationMs = currentState.durations.getOrElse(5) { 0 },
            slot7Hz = currentState.frequencies.getOrElse(6) { 0 },
            slot7DurationMs = currentState.durations.getOrElse(6) { 0 },
            slot8Hz = currentState.frequencies.getOrElse(7) { 0 },
            slot8DurationMs = currentState.durations.getOrElse(7) { 0 }
        )

        pipAudioPlayer.playPip(tempPip)
    }

    fun savePip() {
        val currentState = _uiState.value
        if (currentState.name.isBlank()) return

        val pip = Pip(
            id = currentState.pipId ?: 0L,
            name = currentState.name,
            isActive = currentState.isActive,
            globalVolume = currentState.globalVolume,
            slot1Hz = currentState.frequencies.getOrElse(0) { 0 },
            slot1DurationMs = currentState.durations.getOrElse(0) { 0 },
            slot2Hz = currentState.frequencies.getOrElse(1) { 0 },
            slot2DurationMs = currentState.durations.getOrElse(1) { 0 },
            slot3Hz = currentState.frequencies.getOrElse(2) { 0 },
            slot3DurationMs = currentState.durations.getOrElse(2) { 0 },
            slot4Hz = currentState.frequencies.getOrElse(3) { 0 },
            slot4DurationMs = currentState.durations.getOrElse(3) { 0 },
            slot5Hz = currentState.frequencies.getOrElse(4) { 0 },
            slot5DurationMs = currentState.durations.getOrElse(4) { 0 },
            slot6Hz = currentState.frequencies.getOrElse(5) { 0 },
            slot6DurationMs = currentState.durations.getOrElse(5) { 0 },
            slot7Hz = currentState.frequencies.getOrElse(6) { 0 },
            slot7DurationMs = currentState.durations.getOrElse(6) { 0 },
            slot8Hz = currentState.frequencies.getOrElse(7) { 0 },
            slot8DurationMs = currentState.durations.getOrElse(7) { 0 }
        )

        viewModelScope.launch {
            if (pip.id == 0L) {
                pipRepository.insertPip(pip)
            } else {
                pipRepository.updatePip(pip)
            }
            originalState = _uiState.value.copy(
                pipId = if (pip.id == 0L) null else pip.id,
                hasUnsavedChanges = false
            )
            _uiState.update { it.copy(isSaveSuccessful = true, hasUnsavedChanges = false) }
        }
    }

    fun resetSaveState() {
        _uiState.update { it.copy(isSaveSuccessful = false) }
    }

    override fun onCleared() {
        super.onCleared()
        pipAudioPlayer.stop()
    }
}