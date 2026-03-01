package com.masterofpuppets.thepips.feature.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.masterofpuppets.thepips.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ScheduleEditorScreen(
    scheduleId: Long?,
    onNavigateBack: () -> Unit,
    viewModel: ScheduleEditorViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val availablePips by viewModel.availablePips.collectAsStateWithLifecycle()

    LaunchedEffect(scheduleId) {
        viewModel.loadSchedule(scheduleId)
    }

    LaunchedEffect(uiState.isSaveSuccessful) {
        if (uiState.isSaveSuccessful) {
            viewModel.resetSaveState()
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (scheduleId != null) {
                            stringResource(id = R.string.title_edit_schedule)
                        } else {
                            stringResource(id = R.string.title_new_schedule)
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(id = R.string.action_cancel)
                        )
                    }
                }
            )
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

            // Command Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (uiState.minutesMask == 0L) {
                    Text(
                        text = stringResource(id = R.string.error_no_minutes_selected),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                }
                Button(
                    onClick = viewModel::saveSchedule,
                    // Prevent saving if no pip is selected or no minutes are defined
                    enabled = uiState.selectedPipId != null && uiState.minutesMask != 0L
                ) {
                    Text(text = stringResource(id = R.string.action_save))
                }
            }

            // Pip Selection Dropdown
            var expanded by remember { mutableStateOf(false) }
            val selectedPip = availablePips.find { it.id == uiState.selectedPipId }

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value = selectedPip?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(id = R.string.label_select_pip)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    isError = uiState.selectedPipId == null,
                    supportingText = {
                        if (uiState.selectedPipId == null) {
                            Text(text = stringResource(id = R.string.error_no_pip_selected))
                        }
                    }
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    availablePips.forEach { pip ->
                        DropdownMenuItem(
                            text = { Text(text = pip.name) },
                            onClick = {
                                viewModel.onPipSelected(pip.id)
                                expanded = false
                            }
                        )
                    }
                }
            }

            // Active Days Configuration
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(id = R.string.label_days_of_week),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    val daysRes = listOf(
                        R.string.day_mon, R.string.day_tue, R.string.day_wed,
                        R.string.day_thu, R.string.day_fri, R.string.day_sat, R.string.day_sun
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        daysRes.forEachIndexed { index, dayResId ->
                            val isSelected = (uiState.daysOfWeekMask and (1 shl index)) != 0
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .clickable { viewModel.toggleDay(index) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(id = dayResId).take(3),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Active Hours Configuration
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = R.string.label_active_hours),
                            style = MaterialTheme.typography.titleMedium
                        )
                        TextButton(
                            onClick = {
                                viewModel.onStartHourChanged(0)
                                viewModel.onEndHourChanged(23)
                            }
                        ) {
                            Text(text = stringResource(id = R.string.action_all_day))
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Start Hour Dropdown
                        var startExpanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = startExpanded,
                            onExpandedChange = { startExpanded = it },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = String.format(
                                    java.util.Locale.getDefault(),
                                    "%02d:00",
                                    uiState.startHour
                                ),
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(stringResource(id = R.string.label_start)) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = startExpanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = startExpanded,
                                onDismissRequest = { startExpanded = false }
                            ) {
                                for (hour in 0..23) {
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                String.format(
                                                    java.util.Locale.getDefault(),
                                                    "%02d:00",
                                                    hour
                                                )
                                            )
                                        },
                                        onClick = {
                                            viewModel.onStartHourChanged(hour)
                                            startExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // End Hour Dropdown
                        var endExpanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = endExpanded,
                            onExpandedChange = { endExpanded = it },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = String.format(
                                    java.util.Locale.getDefault(),
                                    "%02d:00",
                                    uiState.endHour
                                ),
                                onValueChange = {},
                                readOnly = true,
                                label = { Text(stringResource(id = R.string.label_end)) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = endExpanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = endExpanded,
                                onDismissRequest = { endExpanded = false }
                            ) {
                                for (hour in 0..23) {
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                String.format(
                                                    java.util.Locale.getDefault(),
                                                    "%02d:00",
                                                    hour
                                                )
                                            )
                                        },
                                        onClick = {
                                            viewModel.onEndHourChanged(hour)
                                            endExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Active Minutes Configuration
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(id = R.string.label_minutes),
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Minute Presets
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextButton(onClick = viewModel::applyMinutePresetTopHour) {
                            Text(text = stringResource(id = R.string.preset_top_hour))
                        }
                        TextButton(onClick = viewModel::applyMinutePresetHalfHours) {
                            Text(text = stringResource(id = R.string.preset_half_hours))
                        }
                        TextButton(onClick = viewModel::applyMinutePresetQuarters) {
                            Text(text = stringResource(id = R.string.preset_quarters))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Selected Minutes & Add Button
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Dynamically calculate selected and unselected minutes
                        val selectedMinutes =
                            (0..59).filter { (uiState.minutesMask and (1L shl it)) != 0L }
                        val unselectedMinutes =
                            (0..59).filter { (uiState.minutesMask and (1L shl it)) == 0L }

                        // Draw active chips
                        selectedMinutes.forEach { minute ->
                            InputChip(
                                selected = true,
                                onClick = { viewModel.toggleMinute(minute) },
                                label = { Text(text = minute.toString().padStart(2, '0')) },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            )
                        }

                        // Add new minute Button & Dropdown
                        Box {
                            var addExpanded by remember { mutableStateOf(false) }

                            InputChip(
                                selected = false,
                                onClick = { addExpanded = true },
                                label = { Text(text = stringResource(id = R.string.action_add_minute)) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            )

                            DropdownMenu(
                                expanded = addExpanded,
                                onDismissRequest = { addExpanded = false },
                                modifier = Modifier.heightIn(max = 250.dp) // Prevents the list from taking up the whole screen
                            ) {
                                unselectedMinutes.forEach { minute ->
                                    DropdownMenuItem(
                                        text = { Text(text = minute.toString().padStart(2, '0')) },
                                        onClick = {
                                            viewModel.toggleMinute(minute)
                                            addExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}