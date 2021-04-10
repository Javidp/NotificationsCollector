package com.jd.notificationscollector.services

import android.database.sqlite.SQLiteDatabaseLockedException
import android.database.sqlite.SQLiteTableLockedException
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.jd.notificationscollector.BitmapDrawableConverter
import com.jd.notificationscollector.database.NcDatabase
import com.jd.notificationscollector.model.AppInfo
import com.jd.notificationscollector.model.Notification
import com.jd.notificationscollector.model.NotificationLog
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val MAX_WAITING_TIME = 300_000L
private const val NEXT_CHECK_DELAY_TIME = 10_000L

class StatusBarNotificationListenerService: NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        try {
            if ((sbn?.notification?.flags ?: 0x0) and android.app.Notification.FLAG_GROUP_SUMMARY != 0) {
                return
            }
            val packageName = sbn?.packageName ?: return

            val db = NcDatabase.create(applicationContext)
            val isAppExcluded = !(db.appsInfoDao().findByPackageName(packageName)?.isNotificationsCollectingActive ?: true)
            if (isAppExcluded) return

            val extras = sbn.notification?.extras
            val title = extras?.get("android.title")
            val text = extras?.get("android.text")
            val bigText = extras?.get("android.bigText")
            val color = sbn.notification?.color
            val notificationIcon = extras?.getInt("android.icon")?.let {
                applicationContext.createPackageContext(sbn.packageName, 0).resources.getDrawable(it, applicationContext.theme)
            }

            val applicationInfo = applicationContext.packageManager.getApplicationInfo(sbn.packageName, 0)
            val applicationName = applicationContext.packageManager.getApplicationLabel(applicationInfo).toString()
            val applicationIcon = applicationContext.packageManager.getApplicationIcon(sbn.packageName)

            val bitmapDrawableConverter = BitmapDrawableConverter(this)

            val applicationIconBlob = bitmapDrawableConverter.toByteArray(applicationIcon)
            val appInfo = sbn.packageName?.let { itPackageName ->
                AppInfo(
                    itPackageName,
                    applicationName,
                    applicationIconBlob,
                    true
                )
            }

            val notificationIconBlob = notificationIcon?.let {
                bitmapDrawableConverter.toByteArray(notificationIcon)
            }
            val notification = Notification(
                title.toString(),
                text.toString(),
                bigText.toString(),
                sbn.packageName,
                sbn.postTime,
                notificationIconBlob,
                color
            )
            val notificationLog = NotificationLog(
                0,
                sbn.toString(),
                sbn.notification.toString(),
                sbn.notification?.extras.toString()
            )

            save(db, appInfo, notification, notificationLog)
        } catch (e: Exception) {
            Log.e(javaClass.simpleName, "Exception during saving notification", e)
        }
    }

    private fun save(db: NcDatabase, appInfo: AppInfo?, notification: Notification, notificationLog: NotificationLog) {
        GlobalScope.launch {
            val expireTime = System.currentTimeMillis() + MAX_WAITING_TIME

            while (System.currentTimeMillis() < expireTime) {
                try {
                    db.runInTransaction {
                        appInfo?.let { db.appsInfoDao().insertIfNotExists(it) }

                        val savedNotificationId = db.notificationsDao().insert(notification)

                        notificationLog.notificationId = savedNotificationId
                        db.notificationsLogsDao().insert(notificationLog)
                    }
                    break
                } catch (e: java.lang.Exception) {
                    when (e) {
                        is SQLiteDatabaseLockedException,
                        is SQLiteTableLockedException -> {
                            if (System.currentTimeMillis() < expireTime) {
                                delay(NEXT_CHECK_DELAY_TIME)
                                continue
                            } else {
                                break
                            }
                        }
                        else -> {
                            Log.e(javaClass.simpleName, "Unknown exception during saving notification", e)
                            break
                        }
                    }
                }
            }
        }
    }

}
