package com.jd.notificationscollector.services

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.jd.notificationscollector.BitmapDrawableConverter
import com.jd.notificationscollector.database.NcDatabase
import com.jd.notificationscollector.model.AppInfo
import com.jd.notificationscollector.model.Notification
import com.jd.notificationscollector.model.NotificationLog

class StatusBarNotificationListenerService: NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        try {
            if ((sbn?.notification?.flags ?: 0x0) and android.app.Notification.FLAG_GROUP_SUMMARY != 0) return
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

            val appInfo = applicationContext.packageManager.getApplicationInfo(sbn.packageName, 0)
            val appName = applicationContext.packageManager.getApplicationLabel(appInfo).toString()
            val appIcon = applicationContext.packageManager.getApplicationIcon(sbn.packageName)

            val bitmapDrawableConverter = BitmapDrawableConverter(this)
            sbn.packageName?.let {
                db.appsInfoDao().insertIfNotExists(AppInfo(it, appName, bitmapDrawableConverter.toByteArray(appIcon), true))
            }
            var notificationIconBlob: ByteArray? = null
            notificationIcon?.let {
                notificationIconBlob = bitmapDrawableConverter.toByteArray(notificationIcon)
            }
            val savedNotificationId = db.notificationsDao().insert(
                Notification(
                    title.toString(),
                    text.toString(),
                    bigText.toString(),
                    sbn.packageName,
                    sbn.postTime,
                    notificationIconBlob,
                    color
                )
            )
            db.notificationsLogsDao().insert(
                NotificationLog(
                    savedNotificationId,
                    sbn.toString(),
                    sbn.notification.toString(),
                    sbn.notification?.extras.toString()
                )
            )
        } catch (e: Exception) {
            Log.e(javaClass.simpleName, "Exception during saving notification", e)
        }
    }

}
