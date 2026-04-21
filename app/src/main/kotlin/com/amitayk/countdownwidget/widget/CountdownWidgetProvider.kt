package com.amitayk.countdownwidget.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.amitayk.countdownwidget.data.WidgetPreferences
import com.amitayk.countdownwidget.worker.WorkScheduler

class CountdownWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { CountdownWidgetUpdater.updateWidget(context, it) }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        val prefs = WidgetPreferences(context)
        appWidgetIds.forEach { prefs.deleteWidget(it) }
    }

    override fun onEnabled(context: Context) {
        WorkScheduler.scheduleMidnightUpdate(context)
    }

    override fun onDisabled(context: Context) {
        WorkScheduler.cancel(context)
    }

    /** Called when the user resizes the widget — re-render with the new text scale. */
    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle,
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        CountdownWidgetUpdater.updateWidget(context, appWidgetId)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when (intent.action) {
            Intent.ACTION_DATE_CHANGED,
            Intent.ACTION_TIME_CHANGED,
            Intent.ACTION_TIMEZONE_CHANGED,
            Intent.ACTION_BOOT_COMPLETED -> CountdownWidgetUpdater.updateAllWidgets(context)
        }
    }
}
