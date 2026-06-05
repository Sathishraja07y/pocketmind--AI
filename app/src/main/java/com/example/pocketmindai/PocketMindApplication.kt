package com.example.pocketmindai

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.pocketmindai.worker.AppUsageWorker
import com.example.pocketmindai.worker.DataCollectionWorker
import java.util.concurrent.TimeUnit

class PocketMindApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        scheduleBackgroundTasks()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "PocketMind AI Actions"
            val descriptionText = "Notifications when your Digital Twin adapts settings"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("POCKETMIND_ACTIONS", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
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
