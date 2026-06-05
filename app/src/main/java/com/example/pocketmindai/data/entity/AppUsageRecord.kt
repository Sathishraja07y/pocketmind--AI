package com.example.pocketmindai.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_usage_records")
data class AppUsageRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val packageName: String,
    val startTime: Long,
    val endTime: Long,
    val totalTimeInForeground: Long,
    val timestamp: Long = System.currentTimeMillis()
)
