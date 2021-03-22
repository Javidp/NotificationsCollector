package com.jd.notificationscollector.delete

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jd.notificationscollector.R
import com.jd.notificationscollector.database.NcDatabase
import kotlinx.android.synthetic.main.activity_delete_notifications.view.*


private const val DAY = 1000 * 60 * 60 * 24

class DeleteNotificationsFragment: Fragment() {

    private lateinit var rootView: View
    private lateinit var db: NcDatabase

    private val deleteNotificationsManager = DeleteNotificationsManager()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.activity_delete_notifications, container, false)
        rootView = root

        db = NcDatabase.create(this.requireContext())

        root.delete_all_notifications_btn.setOnClickListener { view -> onDeleteAllNotifications(view) }
        root.delete_old_notifications_btn.setOnClickListener { view -> onDeleteOldNotifications(view) }

        return root
    }

    private fun onDeleteAllNotifications(view: View) {
        val now = System.currentTimeMillis()

        deleteNotificationsManager.showDeleteNotificationsPopups(
            requireActivity(),
            { db.notificationsDao().countByTimestampBefore(now).toLong() },
            { startDeleteNotificationsService(now) },
            {})
    }

    private fun onDeleteOldNotifications(view: View) {
        val numberOfDays = rootView.delete_old_notifications_number_of_days_input.text.toString()
        val timestamp = System.currentTimeMillis() - (numberOfDays.toLongOrNull() ?: 0) * DAY

        deleteNotificationsManager.showDeleteNotificationsPopups(
            requireActivity(),
            { db.notificationsDao().countByTimestampBefore(timestamp).toLong()},
            { startDeleteNotificationsService(timestamp) },
            {})
    }

    private fun startDeleteNotificationsService(timestamp: Long) {
        val intent = Intent(requireContext(), DeleteNotificationsService::class.java)
        intent.action = "ACTION_START_FOREGROUND_SERVICE"
        intent.putExtra("mode", DeleteNotificationsService.Mode.TIME_BEFORE)
        intent.putExtra("timestamp", timestamp);
        context?.startService(intent)
    }

}
