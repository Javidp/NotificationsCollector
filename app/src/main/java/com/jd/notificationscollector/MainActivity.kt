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

private const val NUMBER_OF_NOTIFICATIONS_PER_PAGE = 50
private const val INITIAL_NUMBER_OF_NOTIFICATIONS = 100

class MainActivity : AppCompatActivity() {

    private lateinit var notificationsRecyclerAdapter: RecyclerView.Adapter<*>
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

    private val onGoToTheTopClick = View.OnClickListener {
        val recyclerLayoutManager = notifications_recycler.layoutManager as? LinearLayoutManager
        recyclerLayoutManager?.smoothScrollToPosition(notifications_recycler, null, 0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = NcDatabase.create(this)

        go_top_fab.setOnClickListener(onGoToTheTopClick)
        notifications_swipe_container.setOnRefreshListener(onSwipeRefresh)

        notificationsRecyclerAdapter = NotificationsRecyclerAdapter(dataset, onLoadMoreClick, this)
        notifications_recycler.setHasFixedSize(true)
        notifications_recycler.layoutManager = LinearLayoutManager(this)
        notifications_recycler.adapter = notificationsRecyclerAdapter

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
            notificationsRecyclerAdapter.notifyDataSetChanged()
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
        notificationsRecyclerAdapter.notifyDataSetChanged()
    }

}
