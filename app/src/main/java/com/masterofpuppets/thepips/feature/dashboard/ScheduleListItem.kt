package com.masterofpuppets.thepips.feature.dashboard

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.masterofpuppets.thepips.R
import com.masterofpuppets.thepips.domain.model.Pip
import com.masterofpuppets.thepips.domain.model.PipSchedule
import com.masterofpuppets.thepips.ui.components.PipVisualizer

@Composable
fun ScheduleListItem(
    pipSchedule: PipSchedule,
    onItemClick: (PipSchedule) -> Unit,
    onPlayClick: (Pip) -> Unit,
    onDeleteClick: (PipSchedule) -> Unit,
    modifier: Modifier = Modifier
) {
    val schedule = pipSchedule.schedule
    val pip = pipSchedule.pip
    val context = LocalContext.current

    // Decode days of week mask to human-readable string
    val daysString = remember(schedule.daysOfWeekMask) {
        formatDaysOfWeek(context, schedule.daysOfWeekMask)
    }

    // Decode minutes mask to human-readable string
    val minutesString = remember(schedule.minuteMask) {
        formatMinutes(schedule.minuteMask)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onItemClick(pipSchedule) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Miniature PipVisualizer acting as a Play button on the left
            val frequencies = listOf(
                pip.slot1Hz, pip.slot2Hz, pip.slot3Hz, pip.slot4Hz,
                pip.slot5Hz, pip.slot6Hz, pip.slot7Hz, pip.slot8Hz
            )
            val durations = listOf(
                pip.slot1DurationMs, pip.slot2DurationMs, pip.slot3DurationMs, pip.slot4DurationMs,
                pip.slot5DurationMs, pip.slot6DurationMs, pip.slot7DurationMs, pip.slot8DurationMs
            )

            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(40.dp)
                    .clip(MaterialTheme.shapes.small)
                    .clickable { onPlayClick(pip) }
            ) {
                PipVisualizer(
                    frequencies = frequencies,
                    durations = durations,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Schedule details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = pip.name,
                    style = MaterialTheme.typography.titleMedium
                )

                val hoursString = if (schedule.startHour == 0 && schedule.endHour == 23) {
                    stringResource(id = R.string.action_all_day)
                } else {
                    stringResource(
                        id = R.string.schedule_hours_format,
                        schedule.startHour,
                        schedule.endHour
                    )
                }

                Text(
                    text = hoursString,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = daysString,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(id = R.string.schedule_minutes_format, minutesString),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Minimalist Delete Button
            IconButton(onClick = { onDeleteClick(pipSchedule) }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(id = R.string.action_delete_schedule_content_description),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * Helper function to translate the bitmask into a readable days string.
 */
private fun formatDaysOfWeek(context: Context, mask: Int): String {
    if (mask == 127) return context.getString(R.string.schedule_days_everyday)
    if (mask == 31) return context.getString(R.string.schedule_days_weekdays) // Mon to Fri
    if (mask == 96) return context.getString(R.string.schedule_days_weekends) // Sat and Sun

    val daysRes = listOf(
        R.string.day_mon, R.string.day_tue, R.string.day_wed,
        R.string.day_thu, R.string.day_fri, R.string.day_sat, R.string.day_sun
    )

    val activeDays = mutableListOf<String>()
    for (i in 0..6) {
        if ((mask and (1 shl i)) != 0) {
            // For a compact look, take just the first 3 letters of the localized day if desired,
            // or rely on the strings.xml already being short.
            activeDays.add(context.getString(daysRes[i]))
        }
    }
    return activeDays.joinToString(", ")
}

/**
 * Helper function to translate the 64-bit mask into a list of minutes.
 */
private fun formatMinutes(mask: Long): String {
    val activeMins = mutableListOf<String>()
    for (i in 0..59) {
        if ((mask and (1L shl i)) != 0L) {
            activeMins.add(i.toString().padStart(2, '0'))
        }
    }
    return activeMins.joinToString(", ")
}