package com.example.pocketmindai

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.pocketmindai.worker.AppUsageWorker
import java.util.concurrent.TimeUnit

class PocketMindApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        scheduleAppUsageTracking()
    }

    private fun scheduleAppUsageTracking() {
        val workRequest = PeriodicWorkRequestBuilder<AppUsageWorker>(1, TimeUnit.HOURS)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "AppUsageTracking",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }
}
