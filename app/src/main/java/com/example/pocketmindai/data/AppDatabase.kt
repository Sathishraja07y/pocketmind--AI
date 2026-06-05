package com.example.pocketmindai.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.pocketmindai.data.dao.AppUsageDao
import com.example.pocketmindai.data.dao.BehaviorDao
import com.example.pocketmindai.data.entity.*

@Database(
    entities = [
        AppUsageRecord::class,
        BatteryRecord::class,
        LocationRecord::class,
        SensorRecord::class,
        PredictionRecord::class,
        BatteryPrediction::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appUsageDao(): AppUsageDao
    abstract fun behaviorDao(): BehaviorDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pocketmind_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
