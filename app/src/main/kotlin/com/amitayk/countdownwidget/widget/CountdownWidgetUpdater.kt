package com.amitayk.countdownwidget.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.widget.RemoteViews
import com.amitayk.countdownwidget.R
import com.amitayk.countdownwidget.config.WidgetConfigActivity
import com.amitayk.countdownwidget.data.WidgetPreferences
import com.amitayk.countdownwidget.data.WidgetTheme
import com.amitayk.countdownwidget.data.WidgetThemes
import java.time.LocalDate
import java.time.temporal.ChronoUnit

object CountdownWidgetUpdater {

    fun updateWidget(context: Context, appWidgetId: Int) {
        val prefs = WidgetPreferences(context)
        val manager = AppWidgetManager.getInstance(context)
        val isoDate = prefs.getTargetDate(appWidgetId) ?: return

        val theme = WidgetThemes.find(prefs.getTheme(appWidgetId))
        val targetDate = LocalDate.parse(isoDate)
        val today = LocalDate.now()
        val daysLeft = ChronoUnit.DAYS.between(today, targetDate)

        val views = when {
            daysLeft == 0L -> buildCelebrationViews(context, appWidgetId, theme)
            daysLeft < 0L  -> buildExpiredViews(context, appWidgetId, -daysLeft, theme)
            else           -> buildCountdownViews(context, appWidgetId, daysLeft, prefs.getLabel(appWidgetId), theme)
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

    // ── View builders ─────────────────────────────────────────────────────────

    private fun buildCountdownViews(
        context: Context,
        appWidgetId: Int,
        daysLeft: Long,
        label: String?,
        theme: WidgetTheme,
    ): RemoteViews = base(context, appWidgetId).apply {
        setViewVisibility(R.id.layout_countdown, View.VISIBLE)
        setViewVisibility(R.id.image_celebration, View.GONE)
        setTextViewText(R.id.text_days, daysLeft.toString())
        setTextViewText(R.id.text_days_label, context.getString(R.string.days_left))
        setTextViewText(R.id.text_event_label, label.orEmpty())
        applyTheme(this, theme)
    }

    private fun buildCelebrationViews(
        context: Context,
        appWidgetId: Int,
        theme: WidgetTheme,
    ): RemoteViews = base(context, appWidgetId).apply {
        setViewVisibility(R.id.layout_countdown, View.GONE)
        setViewVisibility(R.id.image_celebration, View.VISIBLE)
        applyTheme(this, theme)
    }

    private fun buildExpiredViews(
        context: Context,
        appWidgetId: Int,
        daysAgo: Long,
        theme: WidgetTheme,
    ): RemoteViews = base(context, appWidgetId).apply {
        setViewVisibility(R.id.layout_countdown, View.VISIBLE)
        setViewVisibility(R.id.image_celebration, View.GONE)
        setTextViewText(R.id.text_days, daysAgo.toString())
        setTextViewText(R.id.text_days_label, context.getString(R.string.days_ago))
        setTextViewText(R.id.text_event_label, "")
        applyTheme(this, theme)
    }

    // ── Theme application ─────────────────────────────────────────────────────

    /**
     * Applies theme colors to the RemoteViews:
     *  - image_bg  → programmatic GradientDrawable bitmap (handles solid, gradient, border, corners)
     *  - text views → primary / secondary colour
     */
    private fun applyTheme(views: RemoteViews, theme: WidgetTheme) {
        views.setImageViewBitmap(R.id.image_bg, buildBackgroundBitmap(theme))
        views.setTextColor(R.id.text_days, Color.parseColor(theme.primaryTextColorHex))
        views.setTextColor(R.id.text_days_label, Color.parseColor(theme.secondaryTextColorHex))
        views.setTextColor(R.id.text_event_label, Color.parseColor(theme.secondaryTextColorHex))
    }

    /**
     * Renders a [BITMAP_SIZE]×[BITMAP_SIZE] bitmap with the theme's background.
     * Using fitXY on the ImageView means corners may appear slightly elliptical on
     * non-square widgets — acceptable at the design's 16–28 dp radii.
     */
    private fun buildBackgroundBitmap(theme: WidgetTheme): Bitmap {
        val size = BITMAP_SIZE
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // Corner radius scaled to bitmap dimensions (assuming ~180 dp minimum widget width)
        val cornerPx = theme.cornerRadiusDp / 180f * size

        val drawable = GradientDrawable()
        drawable.shape = GradientDrawable.RECTANGLE
        drawable.cornerRadius = cornerPx

        if (theme.backgroundIsGradient
            && theme.gradientStartHex != null
            && theme.gradientEndHex != null
        ) {
            drawable.orientation = GradientDrawable.Orientation.TOP_BOTTOM
            drawable.colors = intArrayOf(
                Color.parseColor(theme.gradientStartHex),
                Color.parseColor(theme.gradientEndHex),
            )
        } else {
            drawable.setColor(Color.parseColor(theme.backgroundColorHex))
        }

        theme.borderColorHex?.let { hex ->
            val strokePx = (2f / 180f * size).coerceAtLeast(1f).toInt()
            drawable.setStroke(strokePx, Color.parseColor(hex))
        }

        drawable.setBounds(0, 0, size, size)
        drawable.draw(canvas)
        return bitmap
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /** Base RemoteViews wired with the tap-to-configure pending intent. */
    private fun base(context: Context, appWidgetId: Int): RemoteViews =
        RemoteViews(context.packageName, R.layout.widget_countdown).also { views ->
            views.setOnClickPendingIntent(R.id.widget_root, configPendingIntent(context, appWidgetId))
        }

    private fun configPendingIntent(context: Context, appWidgetId: Int): PendingIntent {
        val intent = Intent(context, WidgetConfigActivity::class.java).apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        return PendingIntent.getActivity(
            context,
            appWidgetId, // unique request code per widget instance
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private const val BITMAP_SIZE = 120 // px — ~57 KB ARGB_8888, well within binder limits
}
