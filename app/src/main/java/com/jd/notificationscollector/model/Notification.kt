package com.jd.notificationscollector.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class Notification(
    @PrimaryKey
    @ColumnInfo(name = "_id")
    var id: Long?,
    var title: String?,
    var text: String?,
    var bigText: String?,
    var packageName: String?,
    var timestamp: Long?,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    var icon: ByteArray?,
    var color: Int?
) {
    constructor(title: String?, text: String?, bigText: String?, packageName: String?, timestamp: Long?, icon: ByteArray?, color: Int?)
            : this(null, title, text, bigText, packageName, timestamp, icon, color)
}
