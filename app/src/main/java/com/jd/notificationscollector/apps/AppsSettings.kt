package com.jd.notificationscollector.apps

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.ConfigurationCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jd.notificationscollector.R
import com.jd.notificationscollector.database.NcDatabase
import com.jd.notificationscollector.model.AppInfo
import kotlinx.android.synthetic.main.activity_apps_settings.*
import java.text.Collator

class AppsSettings : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_apps_settings)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val db = NcDatabase.create(this)
        val apps = sortApps(db.appsInfoDao().findAll())

        viewManager = LinearLayoutManager(this)
        viewAdapter = AppsSettingsRecyclerAdapter(apps, this)
        recyclerView = findViewById<RecyclerView>(R.id.apps_recycler).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun sortApps(apps: List<AppInfo>): List<AppInfo> {
        val locale = ConfigurationCompat.getLocales(resources.configuration)[0]
        val collator = Collator.getInstance(locale).apply { strength = Collator.PRIMARY }
        return apps.sortedWith(Comparator { app1, app2 -> collator.compare(app1.appName, app2.appName) })
    }

}
