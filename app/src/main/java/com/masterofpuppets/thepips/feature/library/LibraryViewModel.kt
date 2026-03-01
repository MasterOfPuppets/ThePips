package com.masterofpuppets.thepips.feature.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masterofpuppets.thepips.domain.audio.PipAudioPlayer
import com.masterofpuppets.thepips.domain.model.Pip
import com.masterofpuppets.thepips.domain.repository.PipRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val pipRepository: PipRepository,
    private val pipAudioPlayer: PipAudioPlayer
) : ViewModel() {

    private val _uiState = MutableStateFlow(LibraryUiState())
    val uiState: StateFlow<LibraryUiState> = _uiState.asStateFlow()

    init {
        observePips()
    }

    private fun observePips() {
        pipRepository.getAllPips()
            .onEach { pipsList ->
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        pips = pipsList
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

    fun deletePip(pip: Pip) {
        viewModelScope.launch {
            pipRepository.deletePip(pip)
        }
    }

    fun togglePipState(pip: Pip, isActive: Boolean) {
        viewModelScope.launch {
            pipRepository.updatePip(pip.copy(isActive = isActive))
        }
    }

    fun playPip(pip: Pip) {
        pipAudioPlayer.playPip(pip)
    }

    fun openEditor(pipId: Long? = null) {
        _uiState.update { currentState ->
            currentState.copy(
                isEditorOpen = true,
                selectedPipId = pipId
            )
        }
    }

    fun closeEditor() {
        _uiState.update { currentState ->
            currentState.copy(
                isEditorOpen = false,
                selectedPipId = null
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        pipAudioPlayer.stop()
    }
}