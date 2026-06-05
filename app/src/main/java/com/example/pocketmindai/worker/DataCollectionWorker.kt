package com.example.pocketmindai.worker

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.pocketmindai.data.AppDatabase
import com.example.pocketmindai.data.entity.BatteryPrediction
import com.example.pocketmindai.data.entity.BatteryRecord
import com.example.pocketmindai.data.entity.PredictionRecord
import com.example.pocketmindai.manager.SmartActionManager
import kotlinx.coroutines.flow.first

class DataCollectionWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        Log.d("DataCollectionWorker", "Running data collection and prediction engine")
        
        collectBatteryData()
        
        // Phase 8: Battery Prediction
        predictBatteryLife()
        
        // Placeholder for AI Prediction (Phase 6)
        // In the future, this will use TensorFlow Lite to predict based on collected data
        val predictedContext = runInference()
        
        // Save prediction to database
        val database = AppDatabase.getDatabase(applicationContext)
        database.behaviorDao().insertPrediction(
            PredictionRecord(predictedContext = predictedContext, confidence = 0.95f)
        )

        // Execute Smart Actions (Phase 7)
        val actionManager = SmartActionManager(applicationContext)
        actionManager.executeActionsForContext(predictedContext)

        return Result.success()
    }

    private suspend fun collectBatteryData() {
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
            applicationContext.registerReceiver(null, ifilter)
        }

        val level: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        val batteryPct = if (scale > 0) (level * 100 / scale.toFloat()).toInt() else -1

        val status: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL

        val database = AppDatabase.getDatabase(applicationContext)
        database.behaviorDao().insertBattery(
            BatteryRecord(percentage = batteryPct, isCharging = isCharging)
        )
    }

    private suspend fun predictBatteryLife() {
        val database = AppDatabase.getDatabase(applicationContext)
        val latestBattery = database.behaviorDao().getLatestBattery().first()
        
        if (latestBattery != null) {
            // Simple logic for Phase 8: Predict based on current %
            // Assuming average consumption of 5% per hour
            val hoursRemaining = latestBattery.percentage / 5f
            val nextChargeTime = System.currentTimeMillis() + (hoursRemaining * 3600000).toLong()
            
            database.behaviorDao().insertBatteryPrediction(
                BatteryPrediction(
                    predictedHoursRemaining = hoursRemaining,
                    nextChargeTime = nextChargeTime
                )
            )
            Log.d("DataCollectionWorker", "Battery prediction: $hoursRemaining hours left")
        }
    }

    private fun runInference(): String {
        // This will be replaced by TFLite logic in Phase 6
        val contexts = listOf("HOME", "WORK", "STUDY", "GYM", "SLEEP")
        return contexts.random()
    }
}
