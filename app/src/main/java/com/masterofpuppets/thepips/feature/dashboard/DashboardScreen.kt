package com.masterofpuppets.thepips.feature.dashboard

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.masterofpuppets.thepips.R
import com.masterofpuppets.thepips.core.util.ExactAlarmPermission
import com.masterofpuppets.thepips.domain.model.PipSchedule

@Composable
fun DashboardScreen(
    onNavigateToInfo: () -> Unit = {},
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isMasterSwitchOn by viewModel.isMasterSwitchOn.collectAsStateWithLifecycle()

    var scheduleToDelete by remember { mutableStateOf<PipSchedule?>(null) }

    val context = LocalContext.current

    var hasCheckedInitialState by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(uiState.isLoading) {
        if (!uiState.isLoading && !hasCheckedInitialState) {
            hasCheckedInitialState = true
            if (!uiState.hasPips) {
                onNavigateToInfo()
            }
        }
    }

    // Launcher to handle the Exact Alarm Permission request result
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        if (ExactAlarmPermission.canScheduleExactAlarms(context)) {
            viewModel.toggleMasterSwitch(true)
        }
    }

    Scaffold(
        floatingActionButton = {
            if (uiState.hasPips) {
                FloatingActionButton(onClick = { viewModel.openEditor(null) }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(id = R.string.fab_add_schedule_content_description)
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Master Switch Card
            val backgroundColor = if (isMasterSwitchOn) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }

            val contentColor = if (isMasterSwitchOn) {
                MaterialTheme.colorScheme.onPrimaryContainer
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = backgroundColor,
                    contentColor = contentColor
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(id = R.string.master_switch_title),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = stringResource(
                                id = if (isMasterSwitchOn) R.string.master_switch_active else R.string.master_switch_inactive
                            ),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Switch(
                        checked = isMasterSwitchOn,
                        onCheckedChange = { isChecked ->
                            if (isChecked) {
                                if (ExactAlarmPermission.canScheduleExactAlarms(context)) {
                                    viewModel.toggleMasterSwitch(true)
                                } else {
                                    val intent =
                                        ExactAlarmPermission.createPermissionIntent(context)
                                    if (intent != null) {
                                        permissionLauncher.launch(intent)
                                    } else {
                                        // Fallback for older APIs that don't require the intent
                                        viewModel.toggleMasterSwitch(true)
                                    }
                                }
                            } else {
                                viewModel.toggleMasterSwitch(false)
                            }
                        }
                    )
                }
            }

            // Schedules List
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f) // Takes the remaining space below the card
            ) {
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    uiState.schedules.isEmpty() -> {
                        Text(
                            text = if (uiState.hasPips) {
                                stringResource(id = R.string.empty_dashboard_message)
                            } else {
                                stringResource(id = R.string.empty_dashboard_no_pips_message)
                            },
                            modifier = Modifier.align(Alignment.Center),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                start = 16.dp,
                                end = 16.dp,
                                top = 16.dp,
                                bottom = 88.dp
                            )
                        ) {
                            items(
                                items = uiState.schedules,
                                key = { it.schedule.id }
                            ) { pipSchedule ->
                                ScheduleListItem(
                                    pipSchedule = pipSchedule,
                                    onItemClick = { clickedSchedule ->
                                        viewModel.openEditor(clickedSchedule.schedule.id)
                                    },
                                    onPlayClick = { pipToPlay ->
                                        viewModel.playPip(pipToPlay)
                                    },
                                    onDeleteClick = { clickedScheduleToDelete ->
                                        scheduleToDelete = clickedScheduleToDelete
                                    },
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    scheduleToDelete?.let { schedule ->
        AlertDialog(
            onDismissRequest = { scheduleToDelete = null },
            title = { Text(text = stringResource(id = R.string.dialog_title_delete_schedule)) },
            text = {
                Text(
                    text = stringResource(
                        id = R.string.dialog_msg_delete_schedule,
                        schedule.pip.name
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteSchedule(schedule)
                    scheduleToDelete = null
                }) {
                    Text(text = stringResource(id = R.string.action_delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { scheduleToDelete = null }) {
                    Text(text = stringResource(id = R.string.action_cancel))
                }
            }
        )
    }

    if (uiState.isEditorOpen) {
        Dialog(
            onDismissRequest = { viewModel.closeEditor() },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            ScheduleEditorScreen(
                scheduleId = uiState.selectedScheduleId,
                onNavigateBack = { viewModel.closeEditor() }
            )
        }
    }
}