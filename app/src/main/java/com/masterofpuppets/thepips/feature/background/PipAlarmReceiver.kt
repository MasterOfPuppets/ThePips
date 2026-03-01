package com.masterofpuppets.thepips.feature.background

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.masterofpuppets.thepips.domain.audio.PipAudioPlayer
import com.masterofpuppets.thepips.domain.repository.AppPreferencesRepository
import com.masterofpuppets.thepips.domain.repository.PipRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@AndroidEntryPoint
class PipAlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var pipRepository: PipRepository

    @Inject
    lateinit var appPreferencesRepository: AppPreferencesRepository

    @Inject
    lateinit var pipAudioPlayer: PipAudioPlayer

    @Inject
    lateinit var pipAlarmScheduler: PipAlarmScheduler

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ACTION_FIRED && intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val pendingResult = goAsync()
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        scope.launch {
            try {
                val isEnabled = appPreferencesRepository.isGlobalPipsEnabled.first()
                if (!isEnabled) return@launch

                if (intent.action == ACTION_FIRED) {
                    val now = LocalDateTime.now()
                    val currentMinute = now.minute
                    val currentHour = now.hour
                    val currentDayOfWeek = now.dayOfWeek.value - 1

                    val schedules = pipRepository.getAllSchedules().first()
                    val matchingSchedule = schedules.find { pipSchedule ->
                        pipSchedule.schedule.isMatch(
                            currentMinute,
                            currentDayOfWeek,
                            currentHour
                        ) &&
                                pipSchedule.pip.isActive
                    }

                    matchingSchedule?.let {
                        pipAudioPlayer.playPip(it.pip)
                    }
                }

                pipAlarmScheduler.scheduleNextPip()

            } finally {
                pendingResult.finish()
            }
        }
    }

    companion object {
        const val ACTION_FIRED = "com.masterofpuppets.thepips.ACTION_PIP_FIRED"
    }
}