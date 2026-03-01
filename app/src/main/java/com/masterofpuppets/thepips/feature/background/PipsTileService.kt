package com.masterofpuppets.thepips.feature.background

import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.masterofpuppets.thepips.R
import com.masterofpuppets.thepips.core.util.ExactAlarmPermission
import com.masterofpuppets.thepips.domain.repository.AppPreferencesRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PipsTileService : TileService() {

    @Inject
    lateinit var appPreferencesRepository: AppPreferencesRepository

    @Inject
    lateinit var pipAlarmScheduler: PipAlarmScheduler

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onStartListening() {
        super.onStartListening()
        updateTileState()
    }

    override fun onClick() {
        super.onClick()
        serviceScope.launch {
            val isCurrentlyEnabled = appPreferencesRepository.isGlobalPipsEnabled.first()

            if (isCurrentlyEnabled) {
                // Turn OFF
                appPreferencesRepository.setGlobalPipsEnabled(false)
                pipAlarmScheduler.cancelAlarm()
            } else {
                // Turn ON
                if (!ExactAlarmPermission.canScheduleExactAlarms(this@PipsTileService)) {
                    val intent = ExactAlarmPermission.createPermissionIntent(this@PipsTileService)
                    if (intent != null) {
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK

                        // Handle API 34+ deprecation of startActivityAndCollapse(Intent)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                            val pendingIntent = PendingIntent.getActivity(
                                this@PipsTileService,
                                0,
                                intent,
                                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                            )
                            startActivityAndCollapse(pendingIntent)
                        } else {
                            @Suppress("DEPRECATION")
                            startActivityAndCollapse(intent)
                        }
                    }
                    return@launch
                }

                appPreferencesRepository.setGlobalPipsEnabled(true)
                pipAlarmScheduler.scheduleNextPip()
            }
            updateTileState()
        }
    }

    private fun updateTileState() {
        val tile = qsTile ?: return

        serviceScope.launch {
            val isEnabled = appPreferencesRepository.isGlobalPipsEnabled.first()

            if (isEnabled) {
                tile.state = Tile.STATE_ACTIVE
                tile.label = getString(R.string.tile_label_on)
            } else {
                tile.state = Tile.STATE_INACTIVE
                tile.label = getString(R.string.tile_label_off)
            }
            tile.updateTile()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}