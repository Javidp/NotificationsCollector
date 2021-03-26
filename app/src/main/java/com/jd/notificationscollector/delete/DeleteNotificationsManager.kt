package com.jd.notificationscollector.delete

import android.app.Activity
import android.app.AlertDialog
import com.jd.notificationscollector.R
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DeleteNotificationsManager {

    fun showDeleteNotificationsPopups(context: Activity, countFunction: () -> Long, onConfirmFunction: () -> Unit, onCancelFunction: () -> Unit) {
        val preparingDataDialog = AlertDialog.Builder(context)
            .setView(R.layout.popup_progress_bar_circle)
            .setTitle(R.string.delete_notifications_counting_title)
            .setMessage(context.getString(R.string.delete_notifications_counting_description))
            .create()
        preparingDataDialog.show()

        GlobalScope.launch {
            val count = countFunction()

            preparingDataDialog.cancel()

            context.runOnUiThread {
                if (count > 0) {
                    AlertDialog.Builder(context)
                        .setTitle(R.string.delete_old_notifications_alert_title)
                        .setMessage(context.getString(R.string.delete_old_notifications_alert_message, count))
                        .setPositiveButton(R.string.confirm_positive) { _, _ -> onConfirmFunction() }
                        .setNegativeButton(R.string.confirm_negative) { _, _ -> onCancelFunction() }
                        .create()
                        .show()
                } else {
                    AlertDialog.Builder(context)
                        .setTitle(R.string.delete_notifications_nothing_to_delete_title)
                        .setMessage(R.string.delete_notifications_nothing_to_delete_description)
                        .setPositiveButton(R.string.ok, null)
                        .create()
                        .show()
                }
            }
        }
    }

}
