package com.jd.notificationscollector

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.util.Log
import com.jd.notificationscollector.model.AppInfo
import com.jd.notificationscollector.model.Notification


class NotificationsCollectorDatabase(private val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "ncdb.db"
        private const val DATABASE_VERSION = 6

        private const val NOTIFICATIONS_TABLE_NAME = "notifications"
        private const val APPS_INFO_TABLE_NAME = "apps_info"

        object NotificationsTableColumns {
            const val TITLE = "title"
            const val TEXT = "text"
            const val BIG_TEXT = "bigText"
            const val PACKAGE_NAME = "packageName"
            const val TIMESTAMP = "timestamp"
            const val ICON = "icon"
        }

        object AppsInfoTableColumns {
            const val PACKAGE_NAME = "packageName"
            const val APP_NAME = "appName"
            const val APP_ICON = "appIcon"
        }

        private const val CREATE_NOTIFICATION_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS $NOTIFICATIONS_TABLE_NAME (${BaseColumns._ID} INTEGER PRIMARY KEY, ${NotificationsTableColumns.TITLE} TEXT, ${NotificationsTableColumns.TEXT} TEXT, ${NotificationsTableColumns.BIG_TEXT} TEXT, ${NotificationsTableColumns.PACKAGE_NAME} TEXT, ${NotificationsTableColumns.TIMESTAMP} INTEGER, ${NotificationsTableColumns.ICON} BLOB)"
        private const val CREATE_APPS_INFO_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS $APPS_INFO_TABLE_NAME (${BaseColumns._ID} INTEGER PRIMARY KEY, ${AppsInfoTableColumns.PACKAGE_NAME} TEXT, ${AppsInfoTableColumns.APP_NAME} TEXT, ${AppsInfoTableColumns.APP_ICON} BLOB)"
    }

    private val bitmapDrawableConverter = BitmapDrawableConverter(context)

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_NOTIFICATION_TABLE_QUERY)
        db?.execSQL(CREATE_APPS_INFO_TABLE_QUERY)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $NOTIFICATIONS_TABLE_NAME")
        db?.execSQL("DROP TABLE IF EXISTS $APPS_INFO_TABLE_NAME")
        onCreate(db)
    }

    fun saveNotification(notification: Notification) {
        val values = ContentValues().apply {
            put(NotificationsTableColumns.TITLE, notification.title)
            put(NotificationsTableColumns.TEXT, notification.text)
            put(NotificationsTableColumns.BIG_TEXT, notification.bigText)
            put(NotificationsTableColumns.PACKAGE_NAME, notification.packageName)
            put(NotificationsTableColumns.TIMESTAMP, notification.timestamp)
            notification.icon?.let {
                Log.i("DB", "icon bytes: ${bitmapDrawableConverter.toByteArray(it).size}")
                put(NotificationsTableColumns.ICON, bitmapDrawableConverter.toByteArray(it))
            }
        }

        writableDatabase.insert(NOTIFICATIONS_TABLE_NAME, null, values)
    }

    fun getNotifications(count: Int): MutableList<Notification> {
        val cursor = readableDatabase.query(
            NOTIFICATIONS_TABLE_NAME,   // The table to query
            null,             // The array of columns to return (pass null to get all)
            null,             // The columns for the WHERE clause
            null,          // The values for the WHERE clause
            null,              // don't group the rows
            null,               // don't filter by row groups
            "${NotificationsTableColumns.TIMESTAMP} DESC",
            count.toString()
        )

        val items = mutableListOf<Notification>()
        with(cursor) {
            while (moveToNext()) {
                val id = getInt(getColumnIndexOrThrow(BaseColumns._ID))
                val title = getString(getColumnIndexOrThrow(NotificationsTableColumns.TITLE))
                val text = getString(getColumnIndexOrThrow(NotificationsTableColumns.TEXT))
                val bigText = getString(getColumnIndexOrThrow(NotificationsTableColumns.BIG_TEXT))
                val packageName = getString(getColumnIndexOrThrow(NotificationsTableColumns.PACKAGE_NAME))
                val timestamp = getLong(getColumnIndexOrThrow(NotificationsTableColumns.TIMESTAMP))
                val icon = bitmapDrawableConverter.toDrawable(getBlob(getColumnIndexOrThrow(NotificationsTableColumns.ICON)))
                items.add(Notification(id, title, text, bigText, packageName, timestamp, icon))
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
            APPS_INFO_TABLE_NAME,   // The table to query
            null,             // The array of columns to return (pass null to get all)
            "${AppsInfoTableColumns.PACKAGE_NAME}='$packageName'",             // The columns for the WHERE clause
            null,          // The values for the WHERE clause
            null,              // don't group the rows
            null,               // don't filter by row groups
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

}
