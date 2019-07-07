package com.jd.notificationscollector.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.jd.notificationscollector.model.AppInfo

@Dao
interface AppsInfoDao: BaseDao<AppInfo> {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertIfNotExists(appInfo: AppInfo)

    @Query("UPDATE apps_info SET isNotificationsCollectingActive = :isNotificationsCollectingActive WHERE packageName = :packageName")
    fun updateIsNotificationsCollectingActiveByPackageName(packageName: String, isNotificationsCollectingActive: Boolean)

    @Query("SELECT * FROM apps_info")
    fun findAll(): List<AppInfo>

    @Query("SELECT * FROM apps_info WHERE packageName = :packageName LIMIT 1")
    fun findByPackageName(packageName: String): AppInfo?

    @Query("DELETE FROM apps_info")
    fun clearAll()

}
