package com.example.pocketmindai.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.pocketmindai.data.dao.AppUsageDao
import com.example.pocketmindai.data.entity.AppUsageRecord

@Database(entities = [AppUsageRecord::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appUsageDao(): AppUsageDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pocketmind_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
