package com.masterofpuppets.thepips.domain.model

import androidx.annotation.StringRes
import com.masterofpuppets.thepips.R

data class PipPreset(
    @StringRes val nameResId: Int,
    val frequencies: List<Int>,
    val durations: List<Int>
)

object DefaultPipPresets {
    val presets = listOf(
        //GTS
        PipPreset(
            nameResId = R.string.preset_name_preset_1,
            frequencies = listOf(1000, 0, 1000, 0, 1000, 0, 1000, 0),
            durations = listOf(100, 900, 100, 900, 100, 900, 900, 0)
        ),
        //Soft pip
        PipPreset(
            nameResId = R.string.preset_name_preset_2,
            frequencies = listOf(300, 0, 0, 0, 0, 0, 0, 0),
            durations = listOf(100, 0, 0, 0, 0, 0, 0, 0)
        ),
        //Reverse
        PipPreset(
            nameResId = R.string.preset_name_preset_3,
            frequencies = listOf(1000, 0, 1000, 0, 1000, 0, 1000, 0),
            durations = listOf(500, 500, 500, 500, 500, 500, 500, 0)
        ),
        //Busy
        PipPreset(
            nameResId = R.string.preset_name_preset_4,
            frequencies = listOf(425, 0, 425, 0, 425, 0, 425, 0),
            durations = listOf(500, 500, 500, 500, 500, 500, 500, 0)
        ),
        //Double
        PipPreset(
            nameResId = R.string.preset_name_preset_5,
            frequencies = listOf(440, 0, 440, 0, 0, 0, 0, 0),
            durations = listOf(100, 50, 100, 0, 0, 0, 0, 0)
        ),
        //Warning
        PipPreset(
            nameResId = R.string.preset_name_preset_6,
            frequencies = listOf(500, 600, 500, 600, 500, 600, 500, 600),
            durations = listOf(500, 500, 500, 500, 500, 500, 500, 500)
        )
    )
}
