package com.jd.notificationscollector.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jd.notificationscollector.dao.AppsInfoDao
import com.jd.notificationscollector.dao.NotificationsDao
import com.jd.notificationscollector.dao.NotificationsLogsDao
import com.jd.notificationscollector.database.migrations.Migrations
import com.jd.notificationscollector.model.AppInfo
import com.jd.notificationscollector.model.Notification
import com.jd.notificationscollector.model.NotificationLog

@Database(entities = [Notification::class, AppInfo::class, NotificationLog::class], version = 10)
abstract class NcDatabase: RoomDatabase() {

    abstract fun notificationsDao(): NotificationsDao
    abstract fun notificationsLogsDao(): NotificationsLogsDao
    abstract fun appsInfoDao(): AppsInfoDao

    companion object {
        fun create(context: Context): NcDatabase {
            return Room.databaseBuilder(context, NcDatabase::class.java, "ncdb.db")
                .addMigrations(*Migrations.all)
                .allowMainThreadQueries()
                .build()
        }
    }

}
