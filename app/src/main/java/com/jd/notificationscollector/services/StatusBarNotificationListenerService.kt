package com.jd.notificationscollector.services

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.jd.notificationscollector.NotificationsCollectorDatabase
import com.jd.notificationscollector.model.Notification


class StatusBarNotificationListenerService: NotificationListenerService() {

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        try {
            Log.i("NLS", "notification posted: $sbn")
            Log.i("NLS", "notification posted: ${sbn?.notification}")

            val extras = sbn?.notification?.extras
            Log.i("NLS", "extras: $extras")

            val title = extras?.get("android.title")
            val text = extras?.get("android.text")
            val bigText = extras?.get("android.bigText")
            Log.i("NLS", "notification title: $title, message: $text, big text: $bigText")

            val db = NotificationsCollectorDatabase(applicationContext)
            db.saveNotification(Notification(title.toString(), text.toString(), bigText.toString(), System.currentTimeMillis()))
        } catch (e: Exception) {}
    }

}
