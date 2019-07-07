package com.jd.notificationscollector.apps

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.jd.notificationscollector.BitmapDrawableConverter
import com.jd.notificationscollector.R
import com.jd.notificationscollector.database.NcDatabase
import com.jd.notificationscollector.model.AppInfo
import kotlinx.android.synthetic.main.activity_app_settings.*
import kotlinx.android.synthetic.main.content_app_settings.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AppSettings : AppCompatActivity() {

    private lateinit var db: NcDatabase
    private var app: AppInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_settings)
        setSupportActionBar(toolbar)

        val appPackage = intent.getStringExtra("app_package_name")
        db = NcDatabase.create(this)
        app = db.appsInfoDao().findByPackageName(appPackage)

        initDefaultValues()
        setupAppInfo()
        setupEnableCollecting()
        setupCollectedNotificationsInfo()
    }

    private fun initDefaultValues() {
        app_settings_collected_notifications_description.text = getString(R.string.app_settings_number_of_notifications_description, 0)
    }

    private fun setupAppInfo() {
        app?.let {
            app_settings_app_name.text = it.appName
            app_settings_app_package.text = it.packageName
            it.appIcon?.let {iconBlob ->
                val bitmapDrawableConverter = BitmapDrawableConverter(this)
                app_settings_icon.setImageDrawable(bitmapDrawableConverter.toDrawable(iconBlob))
            }
        }
    }

    private fun setupEnableCollecting() {
        app?.let {
            app_settings_enable_collecting_switch.isChecked = it.isNotificationsCollectingActive

            app_settings_enable_collecting_switch.setOnCheckedChangeListener { _, isChecked ->
                it.isNotificationsCollectingActive = isChecked
                db.appsInfoDao().updateIsNotificationsCollectingActiveByPackageName(it.packageName, isChecked)
            }
        }
    }

    private fun setupCollectedNotificationsInfo() {
        app?.packageName?.let {
            GlobalScope.launch {
                val notificationsCount = db.notificationsDao().countByPackageName(it)
                app_settings_collected_notifications_description.text = getString(R.string.app_settings_number_of_notifications_description, notificationsCount)
            }
        }

        app_settings_btn_delete_notifications.setOnClickListener {
            showDeleteNotificationsDialog()
        }
    }

    private fun showDeleteNotificationsDialog() {
        AlertDialog.Builder(this)
            .setTitle(R.string.clear_confirm_title)
            .setMessage(R.string.clear_confirm_message)
            .setPositiveButton(R.string.confirm_positive) { _, _ ->
                deleteNotifications()
                setupCollectedNotificationsInfo()
            }
            .setNegativeButton(R.string.confirm_negative, null)
            .show()
    }

    private fun deleteNotifications() {
        app?.packageName?.let {
            db.notificationsDao().deleteByPackageName(it)
        }
    }

}
