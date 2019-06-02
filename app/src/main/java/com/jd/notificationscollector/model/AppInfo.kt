package com.jd.notificationscollector.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "apps_info")
data class AppInfo (
    @PrimaryKey
    var packageName: String,
    var appName: String?,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    var appIcon: ByteArray?
)
