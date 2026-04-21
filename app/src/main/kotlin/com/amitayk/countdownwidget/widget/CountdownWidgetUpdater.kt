package com.amitayk.countdownwidget.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import com.amitayk.countdownwidget.R
import com.amitayk.countdownwidget.config.WidgetConfigActivity
import com.amitayk.countdownwidget.data.WidgetPreferences
import java.time.LocalDate
import java.time.temporal.ChronoUnit

object CountdownWidgetUpdater {

    fun updateWidget(context: Context, appWidgetId: Int) {
        val prefs = WidgetPreferences(context)
        val manager = AppWidgetManager.getInstance(context)
        val isoDate = prefs.getTargetDate(appWidgetId) ?: return

        val targetDate = LocalDate.parse(isoDate)
        val today = LocalDate.now()
        val daysLeft = ChronoUnit.DAYS.between(today, targetDate)

        val views = when {
            daysLeft == 0L -> buildCelebrationViews(context, appWidgetId)
            daysLeft < 0L -> buildExpiredViews(context, appWidgetId, -daysLeft)
            else -> buildCountdownViews(context, appWidgetId, daysLeft, prefs.getLabel(appWidgetId))
        }

        manager.updateAppWidget(appWidgetId, views)
    }

    fun updateAllWidgets(context: Context) {
        val manager = AppWidgetManager.getInstance(context)
        val ids = manager.getAppWidgetIds(
            ComponentName(context, CountdownWidgetProvider::class.java)
        )
        ids.forEach { updateWidget(context, it) }
    }

    private fun buildCountdownViews(
        context: Context,
        appWidgetId: Int,
        daysLeft: Long,
        label: String?
    ): RemoteViews = RemoteViews(context.packageName, R.layout.widget_countdown).apply {
        setViewVisibility(R.id.layout_countdown, View.VISIBLE)
        setViewVisibility(R.id.image_celebration, View.GONE)
        setTextViewText(R.id.text_days, daysLeft.toString())
        setTextViewText(R.id.text_event_label, label.orEmpty())
        setOnClickPendingIntent(R.id.layout_countdown, configPendingIntent(context, appWidgetId))
    }

    private fun buildCelebrationViews(context: Context, appWidgetId: Int): RemoteViews =
        RemoteViews(context.packageName, R.layout.widget_countdown).apply {
            setViewVisibility(R.id.layout_countdown, View.GONE)
            setViewVisibility(R.id.image_celebration, View.VISIBLE)
            setOnClickPendingIntent(R.id.image_celebration, configPendingIntent(context, appWidgetId))
        }

    private fun buildExpiredViews(context: Context, appWidgetId: Int, daysAgo: Long): RemoteViews =
        RemoteViews(context.packageName, R.layout.widget_countdown).apply {
            setViewVisibility(R.id.layout_countdown, View.VISIBLE)
            setViewVisibility(R.id.image_celebration, View.GONE)
            setTextViewText(R.id.text_days, daysAgo.toString())
            setTextViewText(R.id.text_event_label, context.getString(R.string.days_ago))
            setOnClickPendingIntent(R.id.layout_countdown, configPendingIntent(context, appWidgetId))
        }

    private fun configPendingIntent(context: Context, appWidgetId: Int): PendingIntent {
        val intent = Intent(context, WidgetConfigActivity::class.java).apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        return PendingIntent.getActivity(
            context,
            appWidgetId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
