package com.jd.notificationscollector.notifications

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jd.notificationscollector.R
import com.jd.notificationscollector.database.NcDatabase

class NotificationLogsActivity : AppCompatActivity() {

    private val onNotificationLogClick = View.OnClickListener {
        it as TextView
        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboardManager.setPrimaryClip(ClipData.newPlainText("src", it.text))

        Toast.makeText(this, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_logs)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val notificationId = intent.getLongExtra("notificationId", 0)
        val db = NcDatabase.create(this)
        db.notificationsLogsDao().findByNotificationId(notificationId)?.let {log ->
            findViewById<TextView>(R.id.notification_log_tv).apply {
                text = getString(R.string.notification_log_template, log.sbn, log.notification, log.extras)
                setOnClickListener(onNotificationLogClick)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}
