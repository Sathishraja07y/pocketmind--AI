package com.example.pocketmindai.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.pocketmindai.data.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BehaviorDao {
    @Insert
    suspend fun insertBattery(record: BatteryRecord)

    @Insert
    suspend fun insertLocation(record: LocationRecord)

    @Insert
    suspend fun insertSensor(record: SensorRecord)

    @Insert
    suspend fun insertPrediction(record: PredictionRecord)

    @Query("SELECT * FROM battery_history ORDER BY timestamp DESC LIMIT 1")
    fun getLatestBattery(): Flow<BatteryRecord?>

    @Query("SELECT predictedContext FROM predictions ORDER BY timestamp DESC LIMIT 1")
    fun getLatestPrediction(): Flow<String?>

    @Query("SELECT * FROM battery_history ORDER BY timestamp DESC")
    fun getAllBattery(): Flow<List<BatteryRecord>>
}
