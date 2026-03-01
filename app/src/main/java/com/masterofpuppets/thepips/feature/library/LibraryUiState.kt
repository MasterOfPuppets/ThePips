package com.masterofpuppets.thepips.feature.library

import com.masterofpuppets.thepips.domain.model.Pip

data class LibraryUiState(
    val isLoading: Boolean = true,
    val pips: List<Pip> = emptyList(),
    val isEditorOpen: Boolean = false,
    val selectedPipId: Long? = null
)