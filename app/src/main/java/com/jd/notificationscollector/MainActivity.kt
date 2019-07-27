package com.jd.notificationscollector

import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS
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
import com.jd.notificationscollector.services.StatusBarNotificationListenerService
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

private const val NUMBER_OF_NOTIFICATIONS_PER_PAGE = 50
private const val INITIAL_NUMBER_OF_NOTIFICATIONS = 100

class MainActivity : AppCompatActivity() {

    private lateinit var notificationsRecyclerAdapter: RecyclerView.Adapter<*>
    private lateinit var db: NcDatabase
    private lateinit var notificationsManager: NotificationsManager

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

        checkNotificationAccessPermission()

        db = NcDatabase.create(this)

        go_top_fab.setOnClickListener(onGoToTheTopClick)
        notifications_swipe_container.setOnRefreshListener(onSwipeRefresh)

        notificationsManager = NotificationsManager(this, db)

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
        R.id.menu_btn_clear -> {
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
        R.id.menu_btn_filter -> {
            showFilterView()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun refresh() {
        notificationsCount = INITIAL_NUMBER_OF_NOTIFICATIONS
        dataset.clear()
        dataset.addAll(notificationsManager.getNotifications(notificationsCount))

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
        dataset.addAll(notificationsManager.getNotifications(notificationsCount))
        notificationsRecyclerAdapter.notifyDataSetChanged()
    }

    private fun checkNotificationAccessPermission() {
        if (Build.VERSION.SDK_INT >= 22 && !isNotificationPermissionGranted()) {
            AlertDialog.Builder(this)
                .setTitle(R.string.permission_request_title)
                .setMessage(R.string.notification_listener_permission_request)
                .setPositiveButton(R.string.permission_request_open_settings) { _, _ ->
                    startActivity(Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS))
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
        }
    }

    private fun isNotificationPermissionGranted(): Boolean {
        val notificationListenerServiceComponentName = ComponentName(this, StatusBarNotificationListenerService::class.java)
        val flat = Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
        return flat?.contains(notificationListenerServiceComponentName.flattenToString()) == true
    }

    private fun showFilterView() {
        val filterItems = notificationsManager.getFilterItems()
        val checkedItems = filterItems.map { it.checked }.toTypedArray().toBooleanArray()

        AlertDialog.Builder(this)
            .setTitle(R.string.filter_view_title)
            .setPositiveButton(R.string.filter) { _, _ ->
                refresh()
            }
            .setNegativeButton(R.string.cancel, null)
            .setNeutralButton(R.string.filter_show_all) {_, _ ->
                notificationsManager.resetFilters()
                refresh()
            }
            .setMultiChoiceItems(filterItems.map { it.appName }.toTypedArray(), checkedItems) { _, which, isChecked ->
                notificationsManager.setFilterItemChecked(which, isChecked)
            }
            .show()
    }

}
