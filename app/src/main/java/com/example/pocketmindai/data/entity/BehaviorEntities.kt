package com.example.pocketmindai.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "battery_history")
data class BatteryRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val percentage: Int,
    val isCharging: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "location_history")
data class LocationRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "sensor_history")
data class SensorRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val accelerometerX: Float,
    val accelerometerY: Float,
    val accelerometerZ: Float,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "predictions")
data class PredictionRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val predictedContext: String,
    val confidence: Float,
    val timestamp: Long = System.currentTimeMillis()
)
