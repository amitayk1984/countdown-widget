package com.amitayk.countdownwidget.config

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.amitayk.countdownwidget.R
import com.amitayk.countdownwidget.data.WidgetPreferences
import com.amitayk.countdownwidget.data.WidgetThemes
import com.amitayk.countdownwidget.widget.CountdownWidgetUpdater
import dagger.hilt.android.AndroidEntryPoint
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val CONFIG_BG = Color(0xFF1C1C1E)
private val ACCENT    = Color(0xFFBB86FC)

private val appDarkColorScheme = darkColorScheme(
    primary              = ACCENT,
    onPrimary            = Color.Black,
    primaryContainer     = ACCENT,
    onPrimaryContainer   = Color.Black,
    surface              = Color(0xFF2C2C2E),
    onSurface            = Color.White,
    onSurfaceVariant     = Color.White.copy(alpha = 0.7f),
    background           = CONFIG_BG,
    onBackground         = Color.White,
    outline              = Color.White.copy(alpha = 0.3f),
)

@AndroidEntryPoint
class WidgetConfigActivity : ComponentActivity() {

    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID,
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        // Default CANCELED so back-press removes the widget before save
        setResult(RESULT_CANCELED)

        val prefs = WidgetPreferences(this)
        val existingDate = prefs.getTargetDate(appWidgetId)?.let { LocalDate.parse(it) }
        val existingLabel = prefs.getLabel(appWidgetId).orEmpty()
        val existingTheme = prefs.getTheme(appWidgetId)

        setContent {
            MaterialTheme(colorScheme = appDarkColorScheme) {
                ConfigScreen(
                    initialDate = existingDate ?: LocalDate.now().plusDays(30),
                    initialLabel = existingLabel,
                    initialThemeId = existingTheme,
                    onSave = { date, label, themeId ->
                        saveAndFinish(prefs, date, label, themeId)
                    },
                    onCancel = { finish() },
                )
            }
        }
    }

    private fun saveAndFinish(
        prefs: WidgetPreferences,
        date: LocalDate,
        label: String,
        themeId: String,
    ) {
        prefs.setTargetDate(appWidgetId, date.format(DateTimeFormatter.ISO_LOCAL_DATE))
        prefs.setLabel(appWidgetId, label.trim())
        prefs.setTheme(appWidgetId, themeId)
        CountdownWidgetUpdater.updateWidget(this, appWidgetId)

        val resultIntent = Intent().apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ConfigScreen(
    initialDate: LocalDate,
    initialLabel: String,
    initialThemeId: String,
    onSave: (LocalDate, String, String) -> Unit,
    onCancel: () -> Unit,
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate
            .atStartOfDay(ZoneId.of("UTC"))
            .toInstant()
            .toEpochMilli(),
    )
    var label by remember { mutableStateOf(initialLabel) }
    var themeId by remember { mutableStateOf(initialThemeId) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CONFIG_BG)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 20.dp),
    ) {
        Text(
            text = stringResource(R.string.config_title),
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            modifier = Modifier.padding(bottom = 20.dp),
        )

        // Event label — text/border/label colors come from appDarkColorScheme automatically
        OutlinedTextField(
            value = label,
            onValueChange = { label = it },
            label = { Text(stringResource(R.string.config_label_hint)) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
        )

        // Theme picker
        ThemePicker(
            selectedId = themeId,
            onSelect = { themeId = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
        )

        // Date picker — inherits dark colors from MaterialTheme; only accent overrides needed
        DatePicker(
            state = datePickerState,
            colors = DatePickerDefaults.colors(
                containerColor          = Color(0xFF2C2C2E),
                selectedDayContainerColor  = ACCENT,
                selectedDayContentColor    = Color.Black,
                selectedYearContainerColor = ACCENT,
                selectedYearContentColor   = Color.Black,
                todayContentColor          = ACCENT,
                todayDateBorderColor       = ACCENT,
                currentYearContentColor    = ACCENT,
            ),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(16.dp))

        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End),
        ) {
            TextButton(onClick = onCancel) {
                Text(stringResource(R.string.cancel), color = Color.White.copy(alpha = 0.7f))
            }
            Button(
                onClick = {
                    val millis = datePickerState.selectedDateMillis ?: return@Button
                    val date = Instant.ofEpochMilli(millis)
                        .atZone(ZoneId.of("UTC"))
                        .toLocalDate()
                    onSave(date, label, themeId)
                },
                enabled = datePickerState.selectedDateMillis != null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ACCENT,
                    contentColor = Color.Black,
                ),
            ) {
                Text(stringResource(R.string.save))
            }
        }
    }
}
