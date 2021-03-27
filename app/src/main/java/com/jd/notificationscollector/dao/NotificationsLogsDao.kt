package com.jd.notificationscollector.dao

import androidx.room.Dao
import androidx.room.Query
import com.jd.notificationscollector.model.NotificationLog

@Dao
interface NotificationsLogsDao: BaseDao<NotificationLog> {

    @Query("SELECT * FROM notifications_logs WHERE notificationId = :notificationId LIMIT 1")
    fun findByNotificationId(notificationId: Long): NotificationLog?

    @Query("DELETE FROM notifications_logs")
    fun clearAll()

}
