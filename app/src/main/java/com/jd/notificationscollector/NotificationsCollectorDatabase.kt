package com.jd.notificationscollector

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.util.Log
import com.jd.notificationscollector.model.Notification

class NotificationsCollectorDatabase(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "ncdb.db"
        private const val DATABASE_VERSION = 1

        private const val NOTIFICATIONS_TABLE_NAME = "notifications"

        object NotificationsTableColumns {
            const val TITLE = "title"
            const val TEXT = "text"
            const val BIG_TEXT = "bigText"
        }
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL("CREATE TABLE IF NOT EXISTS $NOTIFICATIONS_TABLE_NAME (${BaseColumns._ID} INTEGER PRIMARY KEY, ${NotificationsTableColumns.TITLE} TEXT, ${NotificationsTableColumns.TEXT} TEXT, ${NotificationsTableColumns.BIG_TEXT} TEXT)")
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {}

    fun saveNotification(title: String?, text: String?, bigText: String?) {
        val values = ContentValues().apply {
            put(NotificationsTableColumns.TITLE, title)
            put(NotificationsTableColumns.TEXT, text)
            put(NotificationsTableColumns.BIG_TEXT, bigText)
        }

        val rowId = writableDatabase.insert(NOTIFICATIONS_TABLE_NAME, null, values)
        Log.i("DB", "saved, row id: $rowId")
    }

    fun getAllNotifications(): MutableList<Notification> {
        val cursor = readableDatabase.query(
            NOTIFICATIONS_TABLE_NAME,   // The table to query
            null,             // The array of columns to return (pass null to get all)
            null,             // The columns for the WHERE clause
            null,          // The values for the WHERE clause
            null,              // don't group the rows
            null,               // don't filter by row groups
            null
        )

        Log.i("DB", "items found: ${cursor.count}")

        val items = mutableListOf<Notification>()
        with(cursor) {
            while (moveToNext()) {
                val title = getString(getColumnIndexOrThrow(NotificationsTableColumns.TITLE))
                val text = getString(getColumnIndexOrThrow(NotificationsTableColumns.TEXT))
                val bigText = getString(getColumnIndexOrThrow(NotificationsTableColumns.BIG_TEXT))
                items.add(Notification(title, text, bigText))
            }
        }
        return items
    }

    fun clearNotificationsTable() {
        writableDatabase.execSQL("DELETE FROM $NOTIFICATIONS_TABLE_NAME")
    }

}
