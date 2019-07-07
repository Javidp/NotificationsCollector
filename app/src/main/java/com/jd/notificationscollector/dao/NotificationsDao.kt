package com.jd.notificationscollector.dao

import androidx.room.Dao
import androidx.room.Query
import com.jd.notificationscollector.model.Notification

@Dao
interface NotificationsDao: BaseDao<Notification> {

    @Query("SELECT * FROM notifications")
    fun findAll(): List<Notification>

    @Query("SELECT * FROM notifications ORDER BY timestamp DESC LIMIT :limit")
    fun findLast(limit: Int): List<Notification>

    @Query("DELETE FROM notifications WHERE packageName = :packageName")
    fun deleteByPackageName(packageName: String): Int

    @Query("SELECT count(_id) FROM notifications WHERE packageName = :packageName")
    fun countByPackageName(packageName: String): Int

    @Query("DELETE FROM notifications")
    fun clearAll()

}
