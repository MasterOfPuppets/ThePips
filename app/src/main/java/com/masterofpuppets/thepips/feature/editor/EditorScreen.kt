package com.masterofpuppets.thepips.feature.editor

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.masterofpuppets.thepips.R
import com.masterofpuppets.thepips.domain.model.PipPreset
import com.masterofpuppets.thepips.ui.components.PipVisualizer
import kotlin.math.roundToInt

private const val MAX_FREQUENCY = 3000f
private const val FREQUENCY_STEPS = 29

private const val MAX_DURATION = 1000f
private const val DURATION_STEPS = 19

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    pipId: Long?,
    onNavigateBack: () -> Unit,
    viewModel: EditorViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showExitDialog by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }

    var showPresetDialog by remember { mutableStateOf(false) }
    var presetsMenuExpanded by remember { mutableStateOf(false) }
    var presetToApply by remember { mutableStateOf<PipPreset?>(null) }
    var overwriteWarned by remember { mutableStateOf(false) }

    var selectedSlotIndex by remember { mutableStateOf<Int?>(null) }
    var tempFreq by remember { mutableStateOf(0) }
    var tempDur by remember { mutableStateOf(0) }

    var showFreqDialog by remember { mutableStateOf(false) }
    var freqInputText by remember { mutableStateOf("") }

    var showDurDialog by remember { mutableStateOf(false) }
    var durInputText by remember { mutableStateOf("") }

    val hasSetValues = uiState.frequencies.any { it > 0 } || uiState.durations.any { it > 0 }

    LaunchedEffect(pipId) {
        viewModel.loadPip(pipId)
    }

    LaunchedEffect(uiState.isSaveSuccessful) {
        if (uiState.isSaveSuccessful) {
            viewModel.resetSaveState()
            onNavigateBack()
        }
    }

    BackHandler(enabled = true) {
        if (uiState.hasUnsavedChanges) {
            showExitDialog = true
        } else {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        Text(
                            text = if (pipId != null) {
                                stringResource(id = R.string.title_edit_pip)
                            } else {
                                stringResource(id = R.string.title_new_pip)
                            }
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            if (uiState.hasUnsavedChanges) {
                                showExitDialog = true
                            } else {
                                onNavigateBack()
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = stringResource(id = R.string.action_cancel)
                            )
                        }
                    }
                )
                Surface(
                    shadowElevation = 4.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box {
                            TextButton(onClick = { presetsMenuExpanded = true }) {
                                Text(text = stringResource(id = R.string.action_presets))
                            }
                            DropdownMenu(
                                expanded = presetsMenuExpanded,
                                onDismissRequest = { presetsMenuExpanded = false }
                            ) {
                                viewModel.availablePresets.forEach { preset ->
                                    DropdownMenuItem(
                                        text = { Text(text = stringResource(id = preset.nameResId)) },
                                        onClick = {
                                            presetsMenuExpanded = false
                                            presetToApply = preset
                                            if (hasSetValues && !overwriteWarned) {
                                                showPresetDialog = true
                                            } else {
                                                viewModel.applyPreset(preset)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                        TextButton(onClick = {
                            if (hasSetValues && !overwriteWarned) showResetDialog =
                                true else viewModel.resetToDefaults()
                        }) {
                            Text(text = stringResource(id = R.string.action_reset))
                        }
                        Button(
                            onClick = viewModel::playTest,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.tertiary
                            )
                        ) {
                            Text(text = stringResource(id = R.string.action_test))
                        }
                        Button(
                            onClick = viewModel::savePip,
                            enabled = uiState.name.isNotBlank()
                        ) {
                            Text(text = stringResource(id = R.string.action_save))
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
            ) {
                PipVisualizer(
                    frequencies = uiState.frequencies,
                    durations = uiState.durations,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                )
            }

            OutlinedTextField(
                value = uiState.name,
                onValueChange = viewModel::onNameChange,
                label = { Text(text = stringResource(id = R.string.label_pip_name)) },
                placeholder = { Text(text = stringResource(id = R.string.hint_pip_name)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = uiState.name.isBlank()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.label_pip_active),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Switch(
                        checked = uiState.isActive,
                        onCheckedChange = viewModel::onActiveChange
                    )
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = R.string.label_global_volume),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "${(uiState.globalVolume * 100).roundToInt()}%",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Slider(
                        value = uiState.globalVolume,
                        onValueChange = viewModel::onVolumeChange,
                        valueRange = 0f..1f
                    )
                }
            }

            for (i in 0 until 8) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            tempFreq = uiState.frequencies[i]
                            tempDur = uiState.durations[i]
                            selectedSlotIndex = i
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = R.string.label_slot_title, i + 1),
                            style = MaterialTheme.typography.titleMedium
                        )

                        val freqStr = if (uiState.frequencies[i] == 0) {
                            stringResource(id = R.string.label_silence)
                        } else {
                            stringResource(
                                id = R.string.label_frequency_value,
                                uiState.frequencies[i]
                            )
                        }

                        val durStr = if (uiState.durations[i] == 0) {
                            stringResource(id = R.string.label_silence)
                        } else {
                            stringResource(id = R.string.label_duration_value, uiState.durations[i])
                        }

                        Text(
                            text = stringResource(
                                id = R.string.format_slot_summary,
                                freqStr,
                                durStr
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    if (selectedSlotIndex != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedSlotIndex = null },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    text = stringResource(
                        id = R.string.label_slot_title,
                        (selectedSlotIndex ?: 0) + 1
                    ),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(
                            id = R.string.label_slot_frequency,
                            (selectedSlotIndex ?: 0) + 1
                        ),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = if (tempFreq == 0) stringResource(id = R.string.label_silence)
                        else stringResource(id = R.string.label_frequency_value, tempFreq),
                        style = MaterialTheme.typography.labelLarge.copy(
                            textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.small)
                            .clickable {
                                freqInputText = tempFreq.toString()
                                showFreqDialog = true
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Slider(
                    value = tempFreq.toFloat(),
                    onValueChange = { tempFreq = it.roundToInt() },
                    valueRange = 0f..MAX_FREQUENCY,
                    steps = FREQUENCY_STEPS
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(
                            id = R.string.label_slot_duration,
                            (selectedSlotIndex ?: 0) + 1
                        ),
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = if (tempDur == 0) stringResource(id = R.string.label_silence)
                        else stringResource(id = R.string.label_duration_value, tempDur),
                        style = MaterialTheme.typography.labelLarge.copy(
                            textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.small)
                            .clickable {
                                durInputText = tempDur.toString()
                                showDurDialog = true
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Slider(
                    value = tempDur.toFloat(),
                    onValueChange = { tempDur = it.roundToInt() },
                    valueRange = 0f..MAX_DURATION,
                    steps = DURATION_STEPS
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = { selectedSlotIndex = null },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text(text = stringResource(id = R.string.action_cancel))
                    }
                    Button(onClick = {
                        selectedSlotIndex?.let { index ->
                            viewModel.onFrequencyChange(index, tempFreq)
                            viewModel.onDurationChange(index, tempDur)
                        }
                        selectedSlotIndex = null
                    }) {
                        Text(text = stringResource(id = R.string.action_confirm))
                    }
                }
            }
        }
    }

    if (showFreqDialog) {
        AlertDialog(
            onDismissRequest = { showFreqDialog = false },
            title = { Text(text = stringResource(id = R.string.dialog_title_edit_frequency)) },
            text = {
                OutlinedTextField(
                    value = freqInputText,
                    onValueChange = { newValue ->
                        if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                            freqInputText = newValue
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    label = { Text(text = stringResource(id = R.string.hint_enter_value)) }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val parsed = freqInputText.toIntOrNull() ?: 0
                    tempFreq = parsed.coerceIn(0, MAX_FREQUENCY.toInt())
                    showFreqDialog = false
                }) {
                    Text(text = stringResource(id = R.string.action_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showFreqDialog = false }) {
                    Text(text = stringResource(id = R.string.action_cancel))
                }
            }
        )
    }

    if (showDurDialog) {
        AlertDialog(
            onDismissRequest = { showDurDialog = false },
            title = { Text(text = stringResource(id = R.string.dialog_title_edit_duration)) },
            text = {
                OutlinedTextField(
                    value = durInputText,
                    onValueChange = { newValue ->
                        if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                            durInputText = newValue
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    label = { Text(text = stringResource(id = R.string.hint_enter_value)) }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val parsed = durInputText.toIntOrNull() ?: 0
                    tempDur = parsed.coerceIn(0, MAX_DURATION.toInt())
                    showDurDialog = false
                }) {
                    Text(text = stringResource(id = R.string.action_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDurDialog = false }) {
                    Text(text = stringResource(id = R.string.action_cancel))
                }
            }
        )
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text(text = stringResource(id = R.string.dialog_title_unsaved_changes)) },
            text = { Text(text = stringResource(id = R.string.dialog_msg_exit)) },
            confirmButton = {
                TextButton(onClick = {
                    showExitDialog = false
                    onNavigateBack()
                }) {
                    Text(text = stringResource(id = R.string.action_confirm_exit))
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitDialog = false }) {
                    Text(text = stringResource(id = R.string.action_cancel))
                }
            }
        )
    }

    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text(text = stringResource(id = R.string.dialog_title_reset)) },
            text = { Text(text = stringResource(id = R.string.dialog_msg_reset)) },
            confirmButton = {
                TextButton(onClick = {
                    showResetDialog = false
                    overwriteWarned = true
                    viewModel.resetToDefaults()
                }) {
                    Text(text = stringResource(id = R.string.action_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text(text = stringResource(id = R.string.action_cancel))
                }
            }
        )
    }

    if (showPresetDialog) {
        AlertDialog(
            onDismissRequest = { showPresetDialog = false },
            title = { Text(text = stringResource(id = R.string.dialog_title_preset)) },
            text = { Text(text = stringResource(id = R.string.dialog_msg_preset)) },
            confirmButton = {
                TextButton(onClick = {
                    showPresetDialog = false
                    overwriteWarned = true
                    presetToApply?.let { viewModel.applyPreset(it) }
                }) {
                    Text(text = stringResource(id = R.string.action_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showPresetDialog = false }) {
                    Text(text = stringResource(id = R.string.action_cancel))
                }
            }
        )
    }
}