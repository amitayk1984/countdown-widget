package com.amitayk.countdownwidget.data

import android.content.Context
import android.content.SharedPreferences

class WidgetPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("countdown_widget_prefs", Context.MODE_PRIVATE)

    fun setTargetDate(appWidgetId: Int, isoDate: String) {
        prefs.edit().putString("target_date_$appWidgetId", isoDate).apply()
    }

    fun getTargetDate(appWidgetId: Int): String? =
        prefs.getString("target_date_$appWidgetId", null)

    fun setLabel(appWidgetId: Int, label: String) {
        prefs.edit().putString("label_$appWidgetId", label).apply()
    }

    fun getLabel(appWidgetId: Int): String? =
        prefs.getString("label_$appWidgetId", null)

    fun deleteWidget(appWidgetId: Int) {
        prefs.edit()
            .remove("target_date_$appWidgetId")
            .remove("label_$appWidgetId")
            .apply()
    }
}
