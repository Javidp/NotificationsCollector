package com.jd.notificationscollector

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var db: NotificationsCollectorDatabase
    private lateinit var contentTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.i("NLS", "main activity on create")

        db = NotificationsCollectorDatabase(applicationContext)

        contentTextView = findViewById(R.id.content_text_view)
        refresh()

        findViewById<Button>(R.id.btn_clear).setOnClickListener {
            clearNotifications()
        }

        findViewById<Button>(R.id.btn_refresh).setOnClickListener {
            refresh()
        }
    }

    private fun refresh() {
        contentTextView.text = getContent()
    }

    private fun getContent(): String {
        val notifications = db.getAllNotifications()
        notifications.reverse()
        Log.i("MA", "notifications count: ${notifications.size}")

        var content = ""
        notifications.forEach {
            Log.i("MA", "notification: title=${it.title}, text=${it.text}, bt=${it.bigText}")
            content += "${it.title}\n${it.text}\n${it.bigText}\n\n\n"
        }

        Log.i("MA", "content: $content")

        return content
    }

    private fun clearNotifications() {
        Log.i("MA", "clearing notifications")
        db.clearNotificationsTable()
        refresh()

    }

}
