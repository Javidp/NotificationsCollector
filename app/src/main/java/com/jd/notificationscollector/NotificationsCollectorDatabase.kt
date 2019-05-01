package com.jd.notificationscollector

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import com.jd.notificationscollector.model.Notification

class NotificationsCollectorDatabase(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "ncdb.db"
        private const val DATABASE_VERSION = 3

        private const val NOTIFICATIONS_TABLE_NAME = "notifications"

        object NotificationsTableColumns {
            const val TITLE = "title"
            const val TEXT = "text"
            const val BIG_TEXT = "bigText"
            const val TIMESTAMP = "timestamp"
        }

        private const val CREATE_NOTIFICATION_TABLE_QUERY = "CREATE TABLE IF NOT EXISTS $NOTIFICATIONS_TABLE_NAME (${BaseColumns._ID} INTEGER PRIMARY KEY, ${NotificationsTableColumns.TITLE} TEXT, ${NotificationsTableColumns.TEXT} TEXT, ${NotificationsTableColumns.BIG_TEXT} TEXT, ${NotificationsTableColumns.TIMESTAMP} INTEGER)"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_NOTIFICATION_TABLE_QUERY)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE $NOTIFICATIONS_TABLE_NAME")
        onCreate(db)
    }

    fun saveNotification(notification: Notification) {
        val values = ContentValues().apply {
            put(NotificationsTableColumns.TITLE, notification.title)
            put(NotificationsTableColumns.TEXT, notification.text)
            put(NotificationsTableColumns.BIG_TEXT, notification.bigText)
            put(NotificationsTableColumns.TIMESTAMP, notification.timestamp)
        }

        val rowId = writableDatabase.insert(NOTIFICATIONS_TABLE_NAME, null, values)
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
                val timestamp = getLong(getColumnIndexOrThrow(NotificationsTableColumns.TIMESTAMP))
                items.add(Notification(id, title, text, bigText, timestamp))
            }
        }
        return items
    }

    fun clearNotificationsTable() {
        writableDatabase.execSQL("DELETE FROM $NOTIFICATIONS_TABLE_NAME")
    }

}
