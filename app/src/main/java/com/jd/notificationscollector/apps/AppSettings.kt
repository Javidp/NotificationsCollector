package com.jd.notificationscollector.apps

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.jd.notificationscollector.BitmapDrawableConverter
import com.jd.notificationscollector.R
import com.jd.notificationscollector.database.NcDatabase
import kotlinx.android.synthetic.main.activity_app_settings.*

class AppSettings : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_settings)
        setSupportActionBar(toolbar)

        val appPackage = intent.getStringExtra("app_package_name")
        val db = NcDatabase.create(this)
        val app = db.appsInfoDao().findByPackageName(appPackage)

        val bitmapDrawableConverter = BitmapDrawableConverter(this)

        app?.let {
            findViewById<TextView>(R.id.app_settings_app_name).text = it.appName
            findViewById<TextView>(R.id.app_settings_app_package).text = it.packageName
            it.appIcon?.let {iconBlob ->
                findViewById<ImageView>(R.id.app_settings_icon).setImageDrawable(bitmapDrawableConverter.toDrawable(iconBlob))
            }
        }
    }

}
