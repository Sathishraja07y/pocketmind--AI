package com.example.pocketmindai

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.pocketmindai.worker.AppUsageWorker
import com.example.pocketmindai.worker.DataCollectionWorker
import java.util.concurrent.TimeUnit

class PocketMindApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        scheduleBackgroundTasks()
    }

    private fun scheduleBackgroundTasks() {
        val workManager = WorkManager.getInstance(this)

        // Task 1: App Usage Tracking
        val usageRequest = PeriodicWorkRequestBuilder<AppUsageWorker>(1, TimeUnit.HOURS)
            .build()
        workManager.enqueueUniquePeriodicWork(
            "AppUsageTracking",
            ExistingPeriodicWorkPolicy.KEEP,
            usageRequest
        )

        // Task 2: Sensor Data Collection & Prediction Engine
        val collectionRequest = PeriodicWorkRequestBuilder<DataCollectionWorker>(15, TimeUnit.MINUTES)
            .build()
        workManager.enqueueUniquePeriodicWork(
            "DataCollectionAndPrediction",
            ExistingPeriodicWorkPolicy.KEEP,
            collectionRequest
        )
    }
}
