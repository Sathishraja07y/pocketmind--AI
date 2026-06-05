package com.example.pocketmindai.worker

import android.app.usage.UsageStatsManager
import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.pocketmindai.data.AppDatabase
import com.example.pocketmindai.data.entity.AppUsageRecord
import com.example.pocketmindai.data.repository.AppUsageRepository
import java.util.Calendar

class AppUsageWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val usageStatsManager =
            applicationContext.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        
        val calendar = Calendar.getInstance()
        val endTime = calendar.timeInMillis
        calendar.add(Calendar.HOUR_OF_DAY, -1)
        val startTime = calendar.timeInMillis

        val stats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY, startTime, endTime
        )

        if (stats != null) {
            val database = AppDatabase.getDatabase(applicationContext)
            val repository = AppUsageRepository(database.appUsageDao())

            for (usageStats in stats) {
                if (usageStats.totalTimeInForeground > 0) {
                    val record = AppUsageRecord(
                        packageName = usageStats.packageName,
                        startTime = startTime,
                        endTime = endTime,
                        totalTimeInForeground = usageStats.totalTimeInForeground
                    )
                    repository.insert(record)
                    Log.d("AppUsageWorker", "Saved usage for ${usageStats.packageName}")
                }
            }
        }

        return Result.success()
    }
}
