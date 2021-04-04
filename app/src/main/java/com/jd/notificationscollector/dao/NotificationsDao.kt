package com.jd.notificationscollector.dao

import androidx.room.Dao
import androidx.room.Query
import com.jd.notificationscollector.model.Notification

@Dao
interface NotificationsDao: BaseDao<Notification> {

    @Query("SELECT * FROM notifications")
    suspend fun findAll(): List<Notification>

    @Query("SELECT * FROM notifications ORDER BY timestamp DESC LIMIT :limit")
    suspend fun findLast(limit: Int): List<Notification>

    @Query("SELECT * FROM notifications WHERE packageName IN (:packagesNames) ORDER BY timestamp DESC LIMIT :limit")
    suspend fun findLastByPackagesNames(limit: Int, packagesNames: List<String>): List<Notification>

    @Query("DELETE FROM notifications WHERE packageName = :packageName")
    fun deleteByPackageName(packageName: String): Int

    @Query("SELECT count(_id) FROM notifications WHERE packageName = :packageName")
    fun countByPackageName(packageName: String): Int

    @Query("DELETE FROM notifications")
    fun clearAll()

    @Query("DELETE FROM notifications WHERE timestamp < :timestamp")
    fun deleteByTimestampBefore(timestamp: Long): Int

    @Query("SELECT count(_id) FROM notifications WHERE timestamp < :timestamp")
    fun countByTimestampBefore(timestamp: Long): Int

}
