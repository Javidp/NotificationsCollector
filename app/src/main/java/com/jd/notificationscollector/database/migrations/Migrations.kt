package com.jd.notificationscollector.database.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object Migrations {

    private val MIGRATION_8_9 = object: Migration(8, 9) {
        override fun migrate(database: SupportSQLiteDatabase) {}
    }

    private val MIGRATION_9_10 = object: Migration(9, 10) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE apps_info_tmp (packageName TEXT NOT NULL PRIMARY KEY, appName TEXT, appIcon BLOB)")
            database.execSQL("INSERT INTO apps_info_tmp (packageName, appName, appIcon) SELECT packageName, appName, appIcon FROM apps_info")
            database.execSQL("DROP TABLE apps_info")
            database.execSQL("ALTER TABLE apps_info_tmp RENAME TO apps_info")
        }
    }

    private val MIGRATION_10_11 = object: Migration(10, 11) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE apps_info_tmp (packageName TEXT NOT NULL PRIMARY KEY, appName TEXT, appIcon BLOB, isNotificationsCollectingActive INTEGER NOT NULL)")
            database.execSQL("INSERT INTO apps_info_tmp (packageName, appName, appIcon, isNotificationsCollectingActive) SELECT packageName, appName, appIcon, 1 FROM apps_info")
            database.execSQL("DROP TABLE apps_info")
            database.execSQL("ALTER TABLE apps_info_tmp RENAME TO apps_info")
        }
    }

    private val MIGRATION_11_12 = object: Migration(11, 12) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE INDEX index_notification_timestamp ON  notifications(timestamp)")
            database.execSQL("CREATE INDEX index_notification_packageName_timestamp ON  notifications(packageName, timestamp)")
            database.execSQL("CREATE INDEX index_notifications_logs_notification_id ON  notifications_logs(notification_id)")
            database.execSQL("CREATE INDEX index_apps_info_packageName ON  apps_info(packageName)")
        }
    }

    private val MIGRATION_12_13 = object: Migration(12, 13) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE notifications_logs_tmp (_id INTEGER PRIMARY KEY, notificationId INTEGER, statusBarNotification TEXT, notification TEXT, extras TEXT, FOREIGN KEY(notificationId) REFERENCES notifications(_id) ON UPDATE CASCADE ON DELETE CASCADE)")
            database.execSQL("INSERT INTO notifications_logs_tmp (_id, notificationId, statusBarNotification, notification, extras) SELECT _id, notification_id, statusBarNotification, notification, extras FROM notifications_logs")
            database.execSQL("DROP TABLE notifications_logs")
            database.execSQL("ALTER TABLE notifications_logs_tmp RENAME TO notifications_logs")

            database.execSQL("DROP INDEX IF EXISTS index_notifications_logs_notification_id")
            database.execSQL("CREATE INDEX index_notifications_logs_notificationId ON notifications_logs(notificationId)")
        }
    }

    val all = arrayOf(MIGRATION_8_9, MIGRATION_9_10, MIGRATION_10_11, MIGRATION_11_12, MIGRATION_12_13)

}
