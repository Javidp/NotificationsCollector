package com.jd.notificationscollector.apps

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.jd.notificationscollector.BitmapDrawableConverter
import com.jd.notificationscollector.R
import com.jd.notificationscollector.database.NcDatabase
import com.jd.notificationscollector.delete.DeleteNotificationsManager
import com.jd.notificationscollector.delete.DeleteNotificationsService
import com.jd.notificationscollector.model.AppInfo
import kotlinx.android.synthetic.main.activity_app_settings.*
import kotlinx.android.synthetic.main.content_app_settings.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AppSettings : AppCompatActivity() {

    private lateinit var db: NcDatabase
    private var app: AppInfo? = null

    private val deleteNotificationsManager = DeleteNotificationsManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_settings)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val appPackage = intent.getStringExtra("app_package_name")
        if (appPackage == null) {
            onAppNotFound()
            return
        }
        db = NcDatabase.create(this)
        app = db.appsInfoDao().findByPackageName(appPackage)
        if (app == null) {
            onAppNotFound()
            return
        }

        initDefaultValues()
        setupAppInfo()
        setupEnableCollecting()
        setupCollectedNotificationsInfo()

        app_settings_btn_delete_notifications.setOnClickListener {
            onDeleteNotifications()
        }

        delete_app_btn.setOnClickListener {
            onDeleteApp()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun onAppNotFound() {
        Toast.makeText(this, R.string.app_settings_app_not_found, Toast.LENGTH_SHORT).show()
        setResult(0)
        finish()
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
                runOnUiThread {
                    app_settings_collected_notifications_description.text = getString(R.string.app_settings_number_of_notifications_description, notificationsCount)
                }
            }
        }
    }

    private fun onDeleteNotifications() {
        app?.packageName?.let { packageName ->
            deleteNotificationsManager.showDeleteNotificationsPopups(
                this,
                { db.notificationsDao().countByPackageName(packageName).toLong() },
                { startDeleteNotificationsService(packageName) },
                {})
        }

    }

    private fun startDeleteNotificationsService(packageName: String) {
        val intent = Intent(this, DeleteNotificationsService::class.java)
        intent.action = "ACTION_START_FOREGROUND_SERVICE"
        intent.putExtra("mode", DeleteNotificationsService.Mode.APP)
        intent.putExtra("app_package", packageName);
        this.startService(intent)
    }

    private fun onDeleteApp() {
        AlertDialog.Builder(this)
            .setTitle(R.string.app_settings_delete_app_alert_title)
            .setMessage(R.string.app_settings_delete_app_alert_description)
            .setPositiveButton(R.string.confirm_positive) { _, _ ->
                GlobalScope.launch {
                    app?.let {
                        val appName = it.appName
                        db.appsInfoDao().delete(it)

                        runOnUiThread {
                            Toast.makeText(applicationContext, getString(R.string.app_delete_confirm_message, appName), Toast.LENGTH_SHORT).show()
                            setResult(1)
                            finish()
                        }
                    }
                }
            }
            .setNegativeButton(R.string.confirm_negative) { _, _ -> }
            .create()
            .show()
    }

}
