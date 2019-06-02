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

    val all = arrayOf(MIGRATION_8_9, MIGRATION_9_10)

}
