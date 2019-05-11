package com.jd.notificationscollector

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import com.jd.notificationscollector.model.AppInfo
import com.jd.notificationscollector.model.Notification
import com.jd.notificationscollector.model.NotificationLog


class NotificationsCollectorDatabase(private val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "ncdb.db"
        private const val DATABASE_VERSION = 8

        private const val NOTIFICATIONS_TABLE_NAME = "notifications"
        private const val APPS_INFO_TABLE_NAME = "apps_info"
        private const val NOTIFICATIONS_LOGS_TABLE_NAME = "notifications_logs"

        object NotificationsTableColumns {
            const val TITLE = "title"
            const val TEXT = "text"
            const val BIG_TEXT = "bigText"
            const val PACKAGE_NAME = "packageName"
            const val TIMESTAMP = "timestamp"
            const val ICON = "icon"
            const val COLOR = "color"
        }

        object AppsInfoTableColumns {
            const val PACKAGE_NAME = "packageName"
            const val APP_NAME = "appName"
            const val APP_ICON = "appIcon"
        }

        object NotificationsLogsColumns {
            const val NOTIFICATION_ID = "notification_id"
            const val SBN = "statusBarNotification"
            const val NOTIFICATION = "notification"
            const val EXTRAS = "extras"
        }

        private const val CREATE_NOTIFICATION_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS $NOTIFICATIONS_TABLE_NAME (${BaseColumns._ID} INTEGER PRIMARY KEY, ${NotificationsTableColumns.TITLE} TEXT, ${NotificationsTableColumns.TEXT} TEXT, ${NotificationsTableColumns.BIG_TEXT} TEXT, ${NotificationsTableColumns.PACKAGE_NAME} TEXT, ${NotificationsTableColumns.TIMESTAMP} INTEGER, ${NotificationsTableColumns.ICON} BLOB)"
        private const val CREATE_APPS_INFO_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS $APPS_INFO_TABLE_NAME (${BaseColumns._ID} INTEGER PRIMARY KEY, ${AppsInfoTableColumns.PACKAGE_NAME} TEXT, ${AppsInfoTableColumns.APP_NAME} TEXT, ${AppsInfoTableColumns.APP_ICON} BLOB)"
        private const val CREATE_NOTIFICATIONS_LOGS_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS $NOTIFICATIONS_LOGS_TABLE_NAME (${BaseColumns._ID} INTEGER PRIMARY KEY, ${NotificationsLogsColumns.NOTIFICATION_ID} INTEGER, ${NotificationsLogsColumns.SBN} TEXT, ${NotificationsLogsColumns.NOTIFICATION} TEXT, ${NotificationsLogsColumns.EXTRAS} TEXT)"
    }

    private val bitmapDrawableConverter = BitmapDrawableConverter(context)

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_NOTIFICATION_TABLE_QUERY)
        db?.execSQL(CREATE_APPS_INFO_TABLE_QUERY)
        db?.execSQL(CREATE_NOTIFICATIONS_LOGS_TABLE_QUERY)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $NOTIFICATIONS_TABLE_NAME")
        db?.execSQL("DROP TABLE IF EXISTS $APPS_INFO_TABLE_NAME")
        db?.execSQL("DROP TABLE IF EXISTS $NOTIFICATIONS_LOGS_TABLE_NAME")
        onCreate(db)
    }

    fun saveNotification(notification: Notification): Notification? {
        val values = ContentValues().apply {
            put(NotificationsTableColumns.TITLE, notification.title)
            put(NotificationsTableColumns.TEXT, notification.text)
            put(NotificationsTableColumns.BIG_TEXT, notification.bigText)
            put(NotificationsTableColumns.PACKAGE_NAME, notification.packageName)
            put(NotificationsTableColumns.TIMESTAMP, notification.timestamp)
            notification.color?.let {
                put(NotificationsTableColumns.COLOR, notification.color)
            }
            notification.icon?.let {
                put(NotificationsTableColumns.ICON, bitmapDrawableConverter.toByteArray(it))
            }
        }

        val rowId = writableDatabase.insert(NOTIFICATIONS_TABLE_NAME, null, values)
        return if (rowId == -1L) null else notification.apply { id = rowId }
    }

    fun findNotifications(count: Int): MutableList<Notification> {
        val cursor = readableDatabase.query(
            NOTIFICATIONS_TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            "${NotificationsTableColumns.TIMESTAMP} DESC",
            count.toString()
        )

        val items = mutableListOf<Notification>()
        with(cursor) {
            while (moveToNext()) {
                items.add(mapToNotification(this))
            }
        }
        return items
    }

    fun clearNotificationsTable() {
        writableDatabase.execSQL("DELETE FROM $NOTIFICATIONS_TABLE_NAME")
        writableDatabase.execSQL("DELETE FROM $APPS_INFO_TABLE_NAME")
    }

    fun saveAppInfoIfNotExists(appInfo: AppInfo) {
        appInfo.packageName?.let {
            findAppInfo(it)?.let {
                return
            }
        }

        val values = ContentValues().apply {
            put(AppsInfoTableColumns.PACKAGE_NAME, appInfo.packageName)
            put(AppsInfoTableColumns.APP_NAME, appInfo.appName)
            appInfo.appIcon?.let {
                put(AppsInfoTableColumns.APP_ICON, bitmapDrawableConverter.toByteArray(it))
            }
        }

        writableDatabase.insert(APPS_INFO_TABLE_NAME, null, values)
    }

    fun findAppInfo(packageName: String): AppInfo? {
        val cursor = readableDatabase.query(
            APPS_INFO_TABLE_NAME,
            null,
            "${AppsInfoTableColumns.PACKAGE_NAME}='$packageName'",
            null,
            null,
            null,
            null
        )

        if (cursor.count > 0) {
            cursor.moveToFirst()

            val appInfo = AppInfo(cursor.getString(cursor.getColumnIndexOrThrow(AppsInfoTableColumns.PACKAGE_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(AppsInfoTableColumns.APP_NAME)),
                bitmapDrawableConverter.toDrawable(cursor.getBlob(cursor.getColumnIndex(AppsInfoTableColumns.APP_ICON))))
            cursor.close()
            return appInfo
        }
        cursor.close()
        return null
    }

    fun saveNotificationLog(notificationLog: NotificationLog): NotificationLog? {
        val values = ContentValues().apply {
            put(NotificationsLogsColumns.NOTIFICATION_ID, notificationLog.notificationId)
            put(NotificationsLogsColumns.SBN, notificationLog.sbn)
            put(NotificationsLogsColumns.NOTIFICATION, notificationLog.notification)
            put(NotificationsLogsColumns.EXTRAS, notificationLog.extras)
        }

        val rowId = writableDatabase.insert(NOTIFICATIONS_LOGS_TABLE_NAME, null, values)
        return if (rowId == -1L) null else notificationLog.apply { id = rowId }
    }

    fun findNotificationLog(notificationId: Long): NotificationLog? {
        val cursor = readableDatabase.query(
            NOTIFICATIONS_LOGS_TABLE_NAME,
            null,
            "${NotificationsLogsColumns.NOTIFICATION_ID}='$notificationId'",
            null,
            null,
            null,
            null
        )

        if (cursor.count > 0) {
            cursor.moveToFirst()

            val id = cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID))
            val notificationIdValue = cursor.getLong(cursor.getColumnIndexOrThrow(NotificationsLogsColumns.NOTIFICATION_ID))
            val sbn = cursor.getString(cursor.getColumnIndexOrThrow(NotificationsLogsColumns.SBN))
            val notification = cursor.getString(cursor.getColumnIndexOrThrow(NotificationsLogsColumns.NOTIFICATION))
            val extras = cursor.getString(cursor.getColumnIndexOrThrow(NotificationsLogsColumns.EXTRAS))

            val notificationLog = NotificationLog(id, notificationIdValue, sbn, notification, extras)
            cursor.close()
            return notificationLog
        }
        cursor.close()
        return null
    }

    private fun mapToNotification(cursor: Cursor): Notification {
        val id = cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID))
        val title = cursor.getString(cursor.getColumnIndexOrThrow(NotificationsTableColumns.TITLE))
        val text = cursor.getString(cursor.getColumnIndexOrThrow(NotificationsTableColumns.TEXT))
        val bigText = cursor.getString(cursor.getColumnIndexOrThrow(NotificationsTableColumns.BIG_TEXT))
        val packageName = cursor.getString(cursor.getColumnIndexOrThrow(NotificationsTableColumns.PACKAGE_NAME))
        val timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(NotificationsTableColumns.TIMESTAMP))
        val icon = bitmapDrawableConverter.toDrawable(cursor.getBlob(cursor.getColumnIndexOrThrow(NotificationsTableColumns.ICON)))
        val color = if (cursor.isNull(cursor.getColumnIndexOrThrow(NotificationsTableColumns.COLOR))) null else cursor.getInt(cursor.getColumnIndexOrThrow(NotificationsTableColumns.COLOR))
        return Notification(id, title, text, bigText, packageName, timestamp, icon, color)
    }

}
