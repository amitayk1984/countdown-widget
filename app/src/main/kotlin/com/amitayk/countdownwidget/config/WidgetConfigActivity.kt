package com.amitayk.countdownwidget.config

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.amitayk.countdownwidget.R
import com.amitayk.countdownwidget.data.WidgetPreferences
import com.amitayk.countdownwidget.widget.CountdownWidgetUpdater
import dagger.hilt.android.AndroidEntryPoint
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class WidgetConfigActivity : ComponentActivity() {

    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appWidgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID,
            AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        // Default result is CANCELED so back-press removes the widget
        setResult(RESULT_CANCELED)

        val prefs = WidgetPreferences(this)
        val existingDate = prefs.getTargetDate(appWidgetId)?.let { LocalDate.parse(it) }
        val existingLabel = prefs.getLabel(appWidgetId).orEmpty()

        setContent {
            MaterialTheme {
                ConfigScreen(
                    initialDate = existingDate ?: LocalDate.now().plusDays(30),
                    initialLabel = existingLabel,
                    onSave = { date, label -> saveAndFinish(prefs, date, label) },
                    onCancel = { finish() }
                )
            }
        }
    }

    private fun saveAndFinish(prefs: WidgetPreferences, date: LocalDate, label: String) {
        prefs.setTargetDate(appWidgetId, date.format(DateTimeFormatter.ISO_LOCAL_DATE))
        prefs.setLabel(appWidgetId, label.trim())
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
    onSave: (LocalDate, String) -> Unit,
    onCancel: () -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = initialDate
            .atStartOfDay(ZoneId.of("UTC"))
            .toInstant()
            .toEpochMilli()
    )
    var label by remember { mutableStateOf(initialLabel) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.config_title),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = label,
            onValueChange = { label = it },
            label = { Text(stringResource(R.string.config_label_hint)) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        DatePicker(
            state = datePickerState,
            modifier = Modifier.weight(1f)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.End)
        ) {
            TextButton(onClick = onCancel) {
                Text(stringResource(R.string.cancel))
            }
            Button(
                onClick = {
                    val millis = datePickerState.selectedDateMillis ?: return@Button
                    val date = Instant.ofEpochMilli(millis)
                        .atZone(ZoneId.of("UTC"))
                        .toLocalDate()
                    onSave(date, label)
                },
                enabled = datePickerState.selectedDateMillis != null
            ) {
                Text(stringResource(R.string.save))
            }
        }
    }
}
