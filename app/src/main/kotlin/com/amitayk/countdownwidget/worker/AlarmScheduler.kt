package com.amitayk.countdownwidget.worker

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.time.LocalDate
import java.time.ZoneId

object AlarmScheduler {

    private const val REQUEST_CODE = 9001

    fun scheduleMidnightUpdate(context: Context) {
        val am = context.getSystemService(AlarmManager::class.java)
        val pi = pendingIntent(context)

        // Next local midnight in milliseconds
        val nextMidnight = LocalDate.now()
            .plusDays(1)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !am.canScheduleExactAlarms()) {
            // API 31+ without exact-alarm permission — fires within ~1 h window (fine for daily countdown)
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextMidnight, pi)
        } else {
            // API 30, or API 31+ with permission granted — fires exactly at midnight even in Doze
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextMidnight, pi)
        }
    }

    fun cancel(context: Context) {
        val am = context.getSystemService(AlarmManager::class.java)
        am.cancel(pendingIntent(context))
    }

    private fun pendingIntent(context: Context): PendingIntent {
        val intent = Intent(context, MidnightAlarmReceiver::class.java)
        return PendingIntent.getBroadcast(
            context, REQUEST_CODE, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }
}
