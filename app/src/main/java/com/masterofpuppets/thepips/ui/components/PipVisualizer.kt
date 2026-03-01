package com.masterofpuppets.thepips.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp

private const val MAX_FREQUENCY = 3000f

@Composable
fun PipVisualizer(
    frequencies: List<Int>,
    durations: List<Int>,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val silenceColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
    val textColor = MaterialTheme.colorScheme.onSurfaceVariant
    val textStyle = MaterialTheme.typography.labelSmall

    val textMeasurer = rememberTextMeasurer()

    Canvas(modifier = modifier) {
        val minWidthPx = 4.dp.toPx()
        val minHeightPx = 8.dp.toPx()
        val gapPx = 2.dp.toPx()
        val silenceLineHeightPx = 2.dp.toPx()

        // Space reserved at the bottom for the slot labels
        val labelSpacePx = 20.dp.toPx()
        val drawingHeight = size.height - labelSpacePx

        val activeSlotsCount = durations.count { it > 0 }

        // If all slots are 0ms, draw a single empty baseline
        if (activeSlotsCount == 0) {
            drawRect(
                color = silenceColor,
                topLeft = Offset(0f, drawingHeight - silenceLineHeightPx),
                size = Size(size.width, silenceLineHeightPx)
            )
            return@Canvas
        }

        val totalGapSpace = gapPx * (activeSlotsCount - 1).coerceAtLeast(0)
        val availableWidth = (size.width - totalGapSpace).coerceAtLeast(1f)
        val totalDuration = durations.sum().coerceAtLeast(1)

        var currentX = 0f

        for (i in 0 until 8) {
            val dur = durations[i]
            val freq = frequencies[i]

            // Slots with 0 duration do not exist in the timeline
            if (dur == 0) continue

            // Calculate proportional width, enforcing a minimum visibility limit
            val calculatedWidth = (dur.toFloat() / totalDuration) * availableWidth
            val drawWidth = calculatedWidth.coerceAtLeast(minWidthPx)

            if (freq > 0) {
                // Calculate proportional height relative to drawingHeight
                val calculatedHeight = (freq.toFloat() / MAX_FREQUENCY) * drawingHeight
                val drawHeight = calculatedHeight.coerceAtLeast(minHeightPx)

                // Draw from bottom to top (above the label space)
                val topY = drawingHeight - drawHeight

                drawRect(
                    color = primaryColor,
                    topLeft = Offset(currentX, topY),
                    size = Size(drawWidth, drawHeight)
                )
            } else {
                // Draw silence as a thin baseline indicator
                val topY = drawingHeight - silenceLineHeightPx

                drawRect(
                    color = silenceColor,
                    topLeft = Offset(currentX, topY),
                    size = Size(drawWidth, silenceLineHeightPx)
                )
            }

            // Draw slot number label if the bar is wide enough
            val textToDraw = (i + 1).toString()
            val textLayoutResult = textMeasurer.measure(textToDraw, textStyle)

            if (drawWidth >= textLayoutResult.size.width) {
                // Center the text horizontally under the bar
                val textX = currentX + (drawWidth - textLayoutResult.size.width) / 2f
                val textY = drawingHeight + 4.dp.toPx() // Small padding below the baseline

                drawText(
                    textMeasurer = textMeasurer,
                    text = textToDraw,
                    topLeft = Offset(textX, textY),
                    style = textStyle.copy(color = textColor)
                )
            }

            currentX += drawWidth + gapPx
        }
    }
}