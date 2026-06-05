package com.example.pocketmindai.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pocketmindai.data.entity.AppUsageRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface AppUsageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsageRecord(record: AppUsageRecord)

    @Query("SELECT * FROM app_usage_records ORDER BY timestamp DESC")
    fun getAllUsageRecords(): Flow<List<AppUsageRecord>>

    @Query("SELECT * FROM app_usage_records WHERE packageName = :packageName")
    suspend fun getRecordsByPackage(packageName: String): List<AppUsageRecord>
}
