package com.jd.notificationscollector.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications_logs")
data class NotificationLog (
    @PrimaryKey
    @ColumnInfo(name = "_id")
    var id: Long?,
    @ColumnInfo(name = "notification_id")
    var notificationId: Long?,
    @ColumnInfo(name = "statusBarNotification")
    var sbn: String?,
    var notification: String?,
    var extras: String?
) {
    constructor(notificationId: Long?, sbn: String?, notification: String?, extras: String?) : this(null, notificationId, sbn, notification, extras)
}
