package com.jd.notificationscollector.apps

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.ConfigurationCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jd.notificationscollector.R
import com.jd.notificationscollector.database.NcDatabase
import com.jd.notificationscollector.model.AppInfo
import java.text.Collator

class AppsFragment: Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.activity_apps_settings, container, false)

        val db = NcDatabase.create(this.requireContext())
        val apps = sortApps(db.appsInfoDao().findAll())

        viewManager = LinearLayoutManager(this.requireContext())
        viewAdapter = AppsSettingsRecyclerAdapter(apps, this.requireContext())

        recyclerView = root.findViewById<RecyclerView>(R.id.apps_recycler).apply {
            this.setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        return root
    }

    private fun sortApps(apps: List<AppInfo>): List<AppInfo> {
        val locale = ConfigurationCompat.getLocales(resources.configuration)[0]
        val collator = Collator.getInstance(locale).apply { strength = Collator.PRIMARY }
        return apps.sortedWith(Comparator { app1, app2 -> collator.compare(app1.appName, app2.appName) })
    }

}
