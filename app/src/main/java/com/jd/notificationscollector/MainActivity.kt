package com.jd.notificationscollector

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.jd.notificationscollector.apps.AppsSettings
import com.jd.notificationscollector.database.NcDatabase
import com.jd.notificationscollector.model.Notification
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    companion object {
        private const val NUMBER_OF_NOTIFICATIONS_PER_PAGE = 50
        private const val INITIAL_NUMBER_OF_NOTIFICATIONS = 100
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private lateinit var db: NcDatabase

    private var dataset: MutableList<Notification> = mutableListOf()
    private var notificationsCount = INITIAL_NUMBER_OF_NOTIFICATIONS

    private val onLoadMoreClick = View.OnClickListener {
        loadMoreNotifications()
    }

    private val onSwipeRefresh = SwipeRefreshLayout.OnRefreshListener {
        GlobalScope.launch {
            refresh()
            runOnUiThread {
                notifications_swipe_container.isRefreshing = false
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = NcDatabase.create(this)

        notifications_swipe_container.setOnRefreshListener(onSwipeRefresh)

        viewManager = LinearLayoutManager(this)
        viewAdapter = NotificationsRecyclerAdapter(dataset, onLoadMoreClick, this)
        recyclerView = findViewById<RecyclerView>(R.id.notifications_recycler).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter
        }

        refresh()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_bar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.btn_clear -> {
            AlertDialog.Builder(this)
                .setTitle(R.string.clear_confirm_title)
                .setMessage(R.string.clear_confirm_message)
                .setPositiveButton(R.string.confirm_positive) { _, _ ->
                    clearNotifications()
                }
                .setNegativeButton(R.string.confirm_negative, null)
                .show()
            true
        }
        R.id.menu_btn_apps_settings -> {
            val appsSettingsIntent = Intent(this, AppsSettings::class.java)
            startActivity(appsSettingsIntent)
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun refresh() {
        notificationsCount = INITIAL_NUMBER_OF_NOTIFICATIONS
        dataset.clear()
        dataset.addAll(db.notificationsDao().findLast(notificationsCount))
        runOnUiThread {
            viewAdapter.notifyDataSetChanged()
        }
    }

    private fun clearNotifications() {
        db.notificationsDao().clearAll()
        db.notificationsLogsDao().clearAll()
        db.appsInfoDao().clearAll()
        refresh()
    }

    private fun loadMoreNotifications() {
        notificationsCount += NUMBER_OF_NOTIFICATIONS_PER_PAGE
        dataset.clear()
        dataset.addAll(db.notificationsDao().findLast(notificationsCount))
        viewAdapter.notifyDataSetChanged()
    }

}
