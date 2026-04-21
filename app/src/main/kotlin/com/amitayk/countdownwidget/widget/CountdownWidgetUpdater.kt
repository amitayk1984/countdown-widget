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
import android.os.Build
import android.util.SizeF
import android.util.TypedValue
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

    /**
     * Two text-size profiles:
     *  SMALL → 1×1 widget  (≈ 57 dp)
     *  LARGE → 3×3 widget  (≈ 177 dp)
     *
     * Sizes (sp) — hierarchy: days (biggest) > reason (medium) > label (smallest)
     */
    private enum class TextScale(
        val daysSp: Float,
        val reasonSp: Float,
        val labelSp: Float,
        val paddingDp: Int,
    ) {
        SMALL(daysSp = 22f, reasonSp = 9f,  labelSp = 7f,  paddingDp = 6),
        LARGE(daysSp = 68f, reasonSp = 18f, labelSp = 13f, paddingDp = 16),
    }

    // ── Public API ────────────────────────────────────────────────────────────

    fun updateWidget(context: Context, appWidgetId: Int) {
        val prefs   = WidgetPreferences(context)
        val manager = AppWidgetManager.getInstance(context)
        val isoDate = prefs.getTargetDate(appWidgetId) ?: return

        val theme   = WidgetThemes.find(prefs.getTheme(appWidgetId))
        val label   = prefs.getLabel(appWidgetId)
        val daysLeft = ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.parse(isoDate))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // API 31+: supply both sizes; Android picks the right one automatically
            // as the user resizes the widget — no manual re-render needed.
            val views = RemoteViews(
                mapOf(
                    SizeF(57f,  57f)  to buildStateViews(context, appWidgetId, daysLeft, label, theme, TextScale.SMALL),
                    SizeF(177f, 177f) to buildStateViews(context, appWidgetId, daysLeft, label, theme, TextScale.LARGE),
                )
            )
            manager.updateAppWidget(appWidgetId, views)
        } else {
            // API 30: read current size from widget options and pick manually
            val scale = scaleFromOptions(manager, appWidgetId)
            manager.updateAppWidget(
                appWidgetId,
                buildStateViews(context, appWidgetId, daysLeft, label, theme, scale),
            )
        }
    }

    fun updateAllWidgets(context: Context) {
        val manager = AppWidgetManager.getInstance(context)
        manager.getAppWidgetIds(ComponentName(context, CountdownWidgetProvider::class.java))
            .forEach { updateWidget(context, it) }
    }

    // ── State builders ────────────────────────────────────────────────────────

    private fun buildStateViews(
        context: Context,
        appWidgetId: Int,
        daysLeft: Long,
        label: String?,
        theme: WidgetTheme,
        scale: TextScale,
    ): RemoteViews = when {
        daysLeft == 0L -> buildCelebrationViews(context, appWidgetId, theme, scale)
        daysLeft < 0L  -> buildExpiredViews(context, appWidgetId, -daysLeft, label, theme, scale)
        else           -> buildCountdownViews(context, appWidgetId, daysLeft, label, theme, scale)
    }

    private fun buildCountdownViews(
        context: Context,
        appWidgetId: Int,
        daysLeft: Long,
        label: String?,
        theme: WidgetTheme,
        scale: TextScale,
    ): RemoteViews = base(context, appWidgetId).apply {
        val hebrew = isHebrew(label)
        setViewVisibility(R.id.layout_countdown, View.VISIBLE)
        setViewVisibility(R.id.image_celebration, View.GONE)
        setTextViewText(R.id.text_days, daysLeft.toString())
        setTextViewText(R.id.text_event_label, label.orEmpty())
        setTextViewText(
            R.id.text_days_label,
            context.getString(if (hebrew) R.string.days_left_he else R.string.days_left),
        )
        applyTheme(this, theme)
        applyTextSizes(context, this, scale)
    }

    private fun buildCelebrationViews(
        context: Context,
        appWidgetId: Int,
        theme: WidgetTheme,
        scale: TextScale,
    ): RemoteViews = base(context, appWidgetId).apply {
        setViewVisibility(R.id.layout_countdown, View.GONE)
        setViewVisibility(R.id.image_celebration, View.VISIBLE)
        applyTheme(this, theme)
        applyTextSizes(context, this, scale)
    }

    private fun buildExpiredViews(
        context: Context,
        appWidgetId: Int,
        daysAgo: Long,
        label: String?,
        theme: WidgetTheme,
        scale: TextScale,
    ): RemoteViews = base(context, appWidgetId).apply {
        val hebrew = isHebrew(label)
        setViewVisibility(R.id.layout_countdown, View.VISIBLE)
        setViewVisibility(R.id.image_celebration, View.GONE)
        setTextViewText(R.id.text_days, daysAgo.toString())
        setTextViewText(R.id.text_event_label, label.orEmpty())
        setTextViewText(
            R.id.text_days_label,
            context.getString(if (hebrew) R.string.days_ago_he else R.string.days_ago),
        )
        applyTheme(this, theme)
        applyTextSizes(context, this, scale)
    }

    // ── Text sizing ───────────────────────────────────────────────────────────

    private fun applyTextSizes(context: Context, views: RemoteViews, scale: TextScale) {
        views.setTextViewTextSize(R.id.text_days,        TypedValue.COMPLEX_UNIT_SP, scale.daysSp)
        views.setTextViewTextSize(R.id.text_event_label, TypedValue.COMPLEX_UNIT_SP, scale.reasonSp)
        views.setTextViewTextSize(R.id.text_days_label,  TypedValue.COMPLEX_UNIT_SP, scale.labelSp)
        val px = dpToPx(context, scale.paddingDp)
        views.setViewPadding(R.id.layout_countdown, px, px, px, px)
    }

    private fun scaleFromOptions(manager: AppWidgetManager, appWidgetId: Int): TextScale {
        val opts     = manager.getAppWidgetOptions(appWidgetId)
        val minWidth = opts.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 57)
        return if (minWidth >= 150) TextScale.LARGE else TextScale.SMALL
    }

    private fun dpToPx(context: Context, dp: Int): Int =
        (dp * context.resources.displayMetrics.density + 0.5f).toInt()

    // ── Theme ─────────────────────────────────────────────────────────────────

    private fun applyTheme(views: RemoteViews, theme: WidgetTheme) {
        views.setImageViewBitmap(R.id.image_bg, buildBackgroundBitmap(theme))
        views.setTextColor(R.id.text_days,        Color.parseColor(theme.primaryTextColorHex))
        views.setTextColor(R.id.text_event_label, Color.parseColor(theme.secondaryTextColorHex))
        views.setTextColor(R.id.text_days_label,  Color.parseColor(theme.secondaryTextColorHex))
    }

    private fun buildBackgroundBitmap(theme: WidgetTheme): Bitmap {
        val size     = BITMAP_SIZE
        val bitmap   = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas   = Canvas(bitmap)
        val cornerPx = theme.cornerRadiusDp / 180f * size

        val drawable = GradientDrawable().apply {
            shape       = GradientDrawable.RECTANGLE
            cornerRadius = cornerPx
            if (theme.backgroundIsGradient
                && theme.gradientStartHex != null
                && theme.gradientEndHex != null
            ) {
                orientation = GradientDrawable.Orientation.TOP_BOTTOM
                colors = intArrayOf(
                    Color.parseColor(theme.gradientStartHex),
                    Color.parseColor(theme.gradientEndHex),
                )
            } else {
                setColor(Color.parseColor(theme.backgroundColorHex))
            }
            theme.borderColorHex?.let { hex ->
                val strokePx = (2f / 180f * size).coerceAtLeast(1f).toInt()
                setStroke(strokePx, Color.parseColor(hex))
            }
            setBounds(0, 0, size, size)
        }
        drawable.draw(canvas)
        return bitmap
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

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
            context, appWidgetId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    private fun isHebrew(text: String?): Boolean =
        text?.any { it.code in 0x0590..0x05FF || it.code in 0xFB1D..0xFB4F } == true

    private const val BITMAP_SIZE = 120
}
