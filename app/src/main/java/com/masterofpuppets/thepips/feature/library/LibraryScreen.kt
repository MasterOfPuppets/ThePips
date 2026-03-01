package com.masterofpuppets.thepips.feature.library

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.masterofpuppets.thepips.R
import com.masterofpuppets.thepips.domain.model.Pip
import com.masterofpuppets.thepips.feature.editor.EditorScreen

@Composable
fun LibraryScreen(
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var pipToDelete by remember { mutableStateOf<Pip?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.openEditor(null) }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.fab_add_pip_content_description)
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                uiState.pips.isEmpty() -> {
                    Text(
                        text = stringResource(id = R.string.empty_library_message),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(
                            items = uiState.pips,
                            key = { pip -> pip.id }
                        ) { pip ->
                            PipListItem(
                                pip = pip,
                                onPipClick = { selectedPip ->
                                    viewModel.openEditor(selectedPip.id)
                                },
                                onPlayClick = { pipToPlay ->
                                    viewModel.playPip(pipToPlay)
                                },
                                onDeleteClick = { selectedPipToDelete ->
                                    pipToDelete = selectedPipToDelete
                                },
                                onToggleActive = { pipToToggle, isActive ->
                                    viewModel.togglePipState(pipToToggle, isActive)
                                },
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    pipToDelete?.let { pip ->
        AlertDialog(
            onDismissRequest = { pipToDelete = null },
            title = { Text(text = stringResource(id = R.string.dialog_title_delete_pip)) },
            text = { Text(text = stringResource(id = R.string.dialog_msg_delete_pip, pip.name)) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deletePip(pip)
                    pipToDelete = null
                }) {
                    Text(text = stringResource(id = R.string.action_delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { pipToDelete = null }) {
                    Text(text = stringResource(id = R.string.action_cancel))
                }
            }
        )
    }

    if (uiState.isEditorOpen) {
        Dialog(
            onDismissRequest = { viewModel.closeEditor() },
            properties = DialogProperties(usePlatformDefaultWidth = false) // Permite que o modal ocupe mais espaço no ecrã
        ) {
            EditorScreen(
                pipId = uiState.selectedPipId,
                onNavigateBack = { viewModel.closeEditor() }
            )
        }
    }
}