package com.amitayk.countdownwidget.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.amitayk.countdownwidget.widget.CountdownWidgetUpdater
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class MidnightUpdateWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        CountdownWidgetUpdater.updateAllWidgets(applicationContext)
        WorkScheduler.scheduleMidnightUpdate(applicationContext)
        return Result.success()
    }
}
