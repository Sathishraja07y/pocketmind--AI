package com.example.pocketmindai.data.repository

import com.example.pocketmindai.data.dao.AppUsageDao
import com.example.pocketmindai.data.entity.AppUsageRecord
import kotlinx.coroutines.flow.Flow

class AppUsageRepository(private val appUsageDao: AppUsageDao) {
    val allUsageRecords: Flow<List<AppUsageRecord>> = appUsageDao.getAllUsageRecords()

    suspend fun insert(record: AppUsageRecord) {
        appUsageDao.insertUsageRecord(record)
    }

    suspend fun getRecordsByPackage(packageName: String): List<AppUsageRecord> {
        return appUsageDao.getRecordsByPackage(packageName)
    }
}
