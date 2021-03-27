package com.jd.notificationscollector.model

import androidx.room.*

@Entity(tableName = "notifications_logs",
    indices = [
        Index(value = ["notificationId"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = Notification::class,
            parentColumns = ["_id"],
            childColumns = ["notificationId"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class NotificationLog (
    @PrimaryKey
    @ColumnInfo(name = "_id")
    var id: Long?,
    var notificationId: Long?,
    @ColumnInfo(name = "statusBarNotification")
    var sbn: String?,
    var notification: String?,
    var extras: String?
) {
    constructor(notificationId: Long?, sbn: String?, notification: String?, extras: String?) : this(null, notificationId, sbn, notification, extras)
}
