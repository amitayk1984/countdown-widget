package com.amitayk.countdownwidget.worker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.amitayk.countdownwidget.widget.CountdownWidgetUpdater

class MidnightAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        CountdownWidgetUpdater.updateAllWidgets(context)
        // Re-arm for the next midnight
        AlarmScheduler.scheduleMidnightUpdate(context)
    }
}
