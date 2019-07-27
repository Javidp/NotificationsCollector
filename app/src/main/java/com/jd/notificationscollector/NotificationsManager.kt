package com.jd.notificationscollector

import android.content.Context
import androidx.core.os.ConfigurationCompat
import com.jd.notificationscollector.database.NcDatabase
import com.jd.notificationscollector.model.AppInfo
import com.jd.notificationscollector.model.Notification
import java.text.Collator

class NotificationsManager(private val context: Context, private val db: NcDatabase) {

    private var filterItems = listOf<FilterItem>()

    init {
        reloadFilterItems()
    }

    fun getNotifications(count: Int): List<Notification> {
        reloadFilterItems()
        val selectedApps = filterItems.filter { it.checked }
        return if (selectedApps.isEmpty()) {
            db.notificationsDao().findLast(count)
        } else {
            db.notificationsDao().findLastByPackagesNames(count, selectedApps.map { it.packageName })
        }
    }

    fun setFilterItemChecked(position: Int, checked: Boolean) {
        filterItems[position].checked = checked
    }

    fun getFilterItems(): List<FilterItem> {
        return filterItems
    }

    fun resetFilters() {
        filterItems.forEach { it.checked = false }
    }

    private fun reloadFilterItems() {
        filterItems = sortApps(db.appsInfoDao().findAll()).map {
            FilterItem(it.packageName, it.appName ?: "", filterItems.find { filterItem ->
                filterItem.packageName == it.packageName }?.checked ?: false
            )
        }
    }

    private fun sortApps(apps: List<AppInfo>): List<AppInfo> {
        val locale = ConfigurationCompat.getLocales(context.resources.configuration)[0]
        val collator = Collator.getInstance(locale).apply { strength = Collator.PRIMARY }
        return apps.sortedWith(Comparator { app1, app2 -> collator.compare(app1.appName, app2.appName) })
    }

}

data class FilterItem(var packageName: String, var appName: String, var checked: Boolean)
