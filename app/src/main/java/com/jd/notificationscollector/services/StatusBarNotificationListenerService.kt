package com.jd.notificationscollector.services

import android.graphics.drawable.Drawable
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.jd.notificationscollector.NotificationsCollectorDatabase
import com.jd.notificationscollector.model.AppInfo
import com.jd.notificationscollector.model.Notification


class StatusBarNotificationListenerService: NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        try {
            val extras = sbn?.notification?.extras
            val title = extras?.get("android.title")
            val text = extras?.get("android.text")
            val bigText = extras?.get("android.bigText")

            val appInfo = applicationContext.packageManager.getApplicationInfo(sbn?.packageName, 0)
            val appName = applicationContext.packageManager.getApplicationLabel(appInfo).toString()
            val appIcon = applicationContext.packageManager.getApplicationIcon(sbn?.packageName)

            var notificationIcon: Drawable? = null
            extras?.getInt("android.icon")?.let {
                notificationIcon = applicationContext.createPackageContext(sbn.packageName, 0).resources.getDrawable(it, applicationContext.theme)
            }

            val db = NotificationsCollectorDatabase(applicationContext)
            db.saveAppInfoIfNotExists(AppInfo(sbn?.packageName, appName, appIcon))
            db.saveNotification(
                Notification(
                    title.toString(),
                    text.toString(),
                    bigText.toString(),
                    sbn?.packageName,
                    sbn?.postTime,
                    notificationIcon
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
