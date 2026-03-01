package com.masterofpuppets.thepips.feature.library

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
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.masterofpuppets.thepips.R
import com.masterofpuppets.thepips.domain.model.Pip
import com.masterofpuppets.thepips.ui.components.PipVisualizer

@Composable
fun PipListItem(
    pip: Pip,
    onPipClick: (Pip) -> Unit,
    onPlayClick: (Pip) -> Unit,
    onDeleteClick: (Pip) -> Unit,
    onToggleActive: (Pip, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onPipClick(pip) }
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

            // Pip details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = pip.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = stringResource(
                        id = R.string.pip_item_volume_label,
                        (pip.globalVolume * 100).toInt()
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Minimalist Toggle Active Button
            IconToggleButton(
                checked = pip.isActive,
                onCheckedChange = { isChecked -> onToggleActive(pip, isChecked) }
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null, // Optional: Add a string resource for accessibility later
                    tint = if (pip.isActive) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    }
                )
            }

            // Minimalist Delete Button
            IconButton(onClick = { onDeleteClick(pip) }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(id = R.string.action_delete_pip_content_description),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}