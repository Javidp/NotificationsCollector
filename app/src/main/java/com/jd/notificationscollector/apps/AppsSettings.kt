package com.jd.notificationscollector.apps

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jd.notificationscollector.R
import com.jd.notificationscollector.database.NcDatabase
import kotlinx.android.synthetic.main.activity_apps_settings.*

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
        val apps = db.appsInfoDao().findAll()

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

}
