package com.jd.notificationscollector

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.TextView
import android.widget.Toast

class NotificationLogsActivity : AppCompatActivity() {

    private val onNotificationLogClick = View.OnClickListener {
        it as TextView
        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboardManager.primaryClip = ClipData.newPlainText("src", it.text)

        Toast.makeText(this, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_logs)

        val notificationId = intent.getLongExtra("notificationId", 0)
        NotificationsCollectorDatabase(this)
            .findNotificationLog(notificationId)?.let {log ->
            findViewById<TextView>(R.id.notification_log_tv).apply {
                text = getString(R.string.notification_log_template, log.sbn, log.notification, log.extras)
                setOnClickListener(onNotificationLogClick)
            }
        }
    }
}
