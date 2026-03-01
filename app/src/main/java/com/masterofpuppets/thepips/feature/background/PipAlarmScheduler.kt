package com.masterofpuppets.thepips.feature.background

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.masterofpuppets.thepips.core.util.ExactAlarmPermission
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PipAlarmScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleNextPip() {
        if (!ExactAlarmPermission.canScheduleExactAlarms(context)) {
            return
        }
        val now = LocalDateTime.now()
        val nextMinute = now.plusMinutes(1).withSecond(0).withNano(0)
        val triggerTimeMs = nextMinute.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val intent = Intent(context, PipAlarmReceiver::class.java).apply {
            action = PipAlarmReceiver.ACTION_FIRED
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTimeMs,
                pendingIntent
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    fun cancelAlarm() {
        val intent = Intent(context, PipAlarmReceiver::class.java).apply {
            action = PipAlarmReceiver.ACTION_FIRED
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}