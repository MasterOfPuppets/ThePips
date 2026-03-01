package com.masterofpuppets.thepips.feature.editor

data class EditorUiState(
    val isLoading: Boolean = false,
    val pipId: Long? = null,
    val name: String = "",
    val isActive: Boolean = true,
    val globalVolume: Float = 1.0f,
    val frequencies: List<Int> = List(8) { 0 },
    val durations: List<Int> = List(8) { 0 },
    val isSaveSuccessful: Boolean = false,
    val hasUnsavedChanges: Boolean = false
)