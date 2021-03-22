package com.jd.notificationscollector.delete

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.jd.notificationscollector.MainActivity
import com.jd.notificationscollector.R
import com.jd.notificationscollector.database.NcDatabase


const val NOTIFICATION_CHANNEL_ID = "DeleteNotificationsService"
const val NOTIFICATION_CHANNEL_NAME = "DeleteNotificationsService"

class DeleteNotificationsService : IntentService("DeleteNotificationsService") {

    enum class Mode {
        APP,
        TIME_BEFORE
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStart(intent: Intent?, startId: Int) {
        super.onStart(intent, startId)
    }

    override fun onHandleIntent(intent: Intent?) {
        val mode = intent?.getSerializableExtra("mode") as? Mode
        if (mode == null || (Mode.APP != mode && Mode.TIME_BEFORE != mode)) {
            return
        }

        var appPackage: String? = null
        var timestamp: Long? = null
        if (mode == Mode.APP) {
            appPackage = intent.getStringExtra("app_package")
            if (appPackage == null) {
                return
            }
        } else if (mode == Mode.TIME_BEFORE) {
            timestamp = intent.getLongExtra("timestamp", 0L)
            if (timestamp == 0L) {
                return
            }
        }

        try {
            startForegroundService()
            val numberOfDeletedNotifications = when (mode) {
                Mode.APP -> deleteAppNotifications(appPackage!!)
                Mode.TIME_BEFORE -> deleteNotificationsBefore(timestamp!!)
            }
            stopForegroundService()
            showCompleteNotification(numberOfDeletedNotifications)
        } catch (e: Exception) {
            Log.e("TEST", "Exception during deleting notifications", e)
            // TODO show error notification if possible>?
        }
    }

    private fun startForegroundService() {
        val notification: Notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        } else {
            createNotification()
        }

        startForeground(1, notification)
    }

    private fun deleteAppNotifications(appPackage: String): Int {
        return 0
    }

    private fun deleteNotificationsBefore(timestamp: Long): Int {
        val db = NcDatabase.create(applicationContext)
        val numberOfDeletedNotifications = db.notificationsDao().deleteByTimestampBefore(timestamp)
        db.close()

        return numberOfDeletedNotifications
    }

    private fun createNotification(): Notification {
        val intent = Intent()
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setOngoing(true)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.notification_icon)
            .setColor(Color.DKGRAY)
            .setContentTitle(getString(R.string.delete_notifications_in_progress_notification_title))
            .setContentText(getString(R.string.delete_notifications_in_progress_notification_description))
            .setCategory(Notification.CATEGORY_SERVICE)
            .setContentIntent(pendingIntent)
            .build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(): Notification {
        val resultIntent = Intent(this, MainActivity::class.java)
        val stackBuilder: TaskStackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addNextIntentWithParentStack(resultIntent)
        val resultPendingIntent: PendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        val chan = NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val manager = (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
        manager.createNotificationChannel(chan)

        return NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setOngoing(true)
            .setSmallIcon(R.drawable.notification_icon)
            .setColor(Color.DKGRAY)
            .setContentTitle(getString(R.string.delete_notifications_in_progress_notification_title))
            .setContentText(getString(R.string.delete_notifications_in_progress_notification_description))
            .setPriority(NotificationManager.IMPORTANCE_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setContentIntent(resultPendingIntent)
            .build()
    }

    private fun stopForegroundService() {
        stopForeground(true)
        stopSelf()
    }

    private fun showCompleteNotification(numberOfDeletedNotifications: Int) {
        val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        val notification: Notification = notificationBuilder
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.notification_icon)
            .setColor(Color.DKGRAY)
            .setContentTitle(getString(R.string.delete_notifications_completed_notification_title))
            .setContentText(getString(R.string.delete_notifications_completed_notification_description, numberOfDeletedNotifications))
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(1, notification)
    }

}
