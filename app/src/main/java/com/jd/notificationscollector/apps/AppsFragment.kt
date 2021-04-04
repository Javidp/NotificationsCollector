package com.jd.notificationscollector.apps

import android.os.Bundle
import android.view.View
import androidx.core.os.ConfigurationCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jd.notificationscollector.R
import com.jd.notificationscollector.database.NcDatabase
import com.jd.notificationscollector.model.AppInfo
import kotlinx.android.synthetic.main.content_apps_settings.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.Collator

class AppsFragment: Fragment(R.layout.activity_apps_settings) {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private lateinit var db: NcDatabase

    private var dataset: MutableList<AppInfo> = mutableListOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = NcDatabase.create(requireContext())

        view.apps_swipe_container.setOnRefreshListener {
            loadApps()
        }

        viewManager = LinearLayoutManager(requireContext())
        viewAdapter = AppsSettingsRecyclerAdapter(dataset, requireContext())

        recyclerView = view.findViewById<RecyclerView>(R.id.apps_recycler).apply {
            this.setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        loadApps()
    }

    private fun loadApps() {
        view?.apps_swipe_container?.isRefreshing = true
        GlobalScope.launch {
            dataset.clear()
            dataset.addAll(sortApps(db.appsInfoDao().findAll()))

            requireActivity().runOnUiThread {
                viewAdapter.notifyDataSetChanged()
                view?.apps_swipe_container?.isRefreshing = false
            }
        }
    }

    private fun sortApps(apps: List<AppInfo>): List<AppInfo> {
        val locale = ConfigurationCompat.getLocales(resources.configuration)[0]
        val collator = Collator.getInstance(locale).apply { strength = Collator.PRIMARY }
        return apps.sortedWith { app1, app2 -> collator.compare(app1.appName, app2.appName) }
    }

}
