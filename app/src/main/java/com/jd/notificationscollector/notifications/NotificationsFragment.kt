package com.jd.notificationscollector.notifications

import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.jd.notificationscollector.R
import com.jd.notificationscollector.database.NcDatabase
import com.jd.notificationscollector.model.Notification
import com.jd.notificationscollector.services.StatusBarNotificationListenerService
import kotlinx.android.synthetic.main.activity_notifications.*
import kotlinx.android.synthetic.main.activity_notifications.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

private const val NUMBER_OF_NOTIFICATIONS_PER_PAGE = 50
private const val INITIAL_NUMBER_OF_NOTIFICATIONS = 100

class NotificationsFragment: Fragment() {

    private lateinit var notificationsRecyclerAdapter: RecyclerView.Adapter<*>
    private lateinit var db: NcDatabase
    private lateinit var notificationsManager: NotificationsManager

    private var dataset: MutableList<Notification> = mutableListOf()
    private var notificationsCount =
        INITIAL_NUMBER_OF_NOTIFICATIONS

    private val onLoadMoreClick = View.OnClickListener {
        loadMoreNotifications()
    }

    private val onSwipeRefresh = SwipeRefreshLayout.OnRefreshListener {
        refresh()
    }

    private val onGoToTheTopClick = View.OnClickListener {
        val recyclerLayoutManager = notifications_recycler.layoutManager as? LinearLayoutManager
        recyclerLayoutManager?.smoothScrollToPosition(notifications_recycler, null, 0)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        val root = inflater.inflate(R.layout.activity_notifications, container, false)

        checkNotificationAccessPermission()

        db = NcDatabase.create(this.requireContext())

        root.go_top_fab.setOnClickListener(onGoToTheTopClick)
        root.notifications_swipe_container.setOnRefreshListener(onSwipeRefresh)

        notificationsManager =
            NotificationsManager(
                this.requireContext(),
                db
            )

        notificationsRecyclerAdapter =
            NotificationsRecyclerAdapter(
                dataset,
                onLoadMoreClick,
                this.requireContext()
            )
        root.notifications_recycler.setHasFixedSize(true)
        root.notifications_recycler.layoutManager = LinearLayoutManager(this.requireContext())
        root.notifications_recycler.adapter = notificationsRecyclerAdapter

        return root
    }

    override fun onStart() {
        super.onStart()
        refresh()
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.top_bar_menu_notifications, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.menu_btn_filter -> {
            showFilterView()
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun refresh() {
        loadNotifications(INITIAL_NUMBER_OF_NOTIFICATIONS)
    }

    private fun loadMoreNotifications() {
        loadNotifications(notificationsCount + NUMBER_OF_NOTIFICATIONS_PER_PAGE)
    }

    private fun loadNotifications(count: Int) {
        val view = requireView()

        view.notifications_swipe_container.isRefreshing = true
        GlobalScope.launch {
            notificationsCount = count

            val notifications = notificationsManager.getNotifications(notificationsCount)
            dataset.clear()
            dataset.addAll(notifications)

            requireActivity().runOnUiThread {
                notificationsRecyclerAdapter.notifyDataSetChanged()
                view.notifications_swipe_container.isRefreshing = false
            }
        }
    }

    private fun checkNotificationAccessPermission() {
        if (Build.VERSION.SDK_INT >= 22 && !isNotificationPermissionGranted()) {
            AlertDialog.Builder(this.requireContext())
                .setTitle(R.string.permission_request_title)
                .setMessage(R.string.notification_listener_permission_request)
                .setPositiveButton(R.string.permission_request_open_settings) { _, _ ->
                    startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
        }
    }

    private fun isNotificationPermissionGranted(): Boolean {
        val notificationListenerServiceComponentName = ComponentName(this.requireContext(), StatusBarNotificationListenerService::class.java)
        val flat = Settings.Secure.getString(requireActivity().contentResolver, "enabled_notification_listeners")
        return flat?.contains(notificationListenerServiceComponentName.flattenToString()) == true
    }

    private fun showFilterView() {
        val filterItems = notificationsManager.getFilterItems()
        val checkedItems = filterItems.map { it.checked }.toTypedArray().toBooleanArray()

        AlertDialog.Builder(this.requireContext())
            .setTitle(R.string.filter_view_title)
            .setPositiveButton(R.string.filter) { _, _ ->
                refresh()
            }
            .setNegativeButton(R.string.cancel, null)
            .setNeutralButton(R.string.filter_show_all) { _, _ ->
                notificationsManager.resetFilters()
                refresh()
            }
            .setMultiChoiceItems(filterItems.map { it.appName }.toTypedArray(), checkedItems) { _, which, isChecked ->
                notificationsManager.setFilterItemChecked(which, isChecked)
            }
            .show()
    }

}
