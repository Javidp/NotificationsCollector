package com.jd.notificationscollector.notifications

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.jd.notificationscollector.BitmapDrawableConverter
import com.jd.notificationscollector.R
import com.jd.notificationscollector.database.NcDatabase
import com.jd.notificationscollector.model.AppInfo
import com.jd.notificationscollector.model.Notification
import java.text.SimpleDateFormat
import java.util.*

class NotificationsRecyclerAdapter(private val notifications: MutableList<Notification>,
                                   private val onLoadMoreClickListener: View.OnClickListener,
                                   private val context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val NOTIFICATION_VIEW_TYPE = 0
        private const val LOAD_MORE_VIEW_TYPE = 1
    }

    class NotificationCardViewHolder(val notificationView: CardView) : RecyclerView.ViewHolder(notificationView)
    class LoadMoreViewHolder(val loadMoreView: CardView) : RecyclerView.ViewHolder(loadMoreView)

    @SuppressLint("SimpleDateFormat")
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm")

    private val db = NcDatabase.create(context)
    private val appsInfo: MutableMap<String, AppInfo> = mutableMapOf()
    private val bitmapDrawableConverter =
        BitmapDrawableConverter(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == NOTIFICATION_VIEW_TYPE) {
            val notificationCard = LayoutInflater.from(parent.context).inflate(R.layout.notification_item, parent, false) as CardView
            return NotificationCardViewHolder(
                notificationCard
            )
        }
        val loadMoreItem = LayoutInflater.from(parent.context).inflate(R.layout.notifications_load_more_item, parent, false) as CardView
        return LoadMoreViewHolder(
            loadMoreItem
        )
    }

    override fun getItemCount(): Int = notifications.size + 1

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == NOTIFICATION_VIEW_TYPE) {
            holder as NotificationCardViewHolder
            holder.notificationView.findViewById<TextView>(R.id.notification_title).text = notifications[position].title
            holder.notificationView.findViewById<TextView>(R.id.notification_text).text = notifications[position].text
            holder.notificationView.findViewById<TextView>(R.id.notification_big_text).text = notifications[position].bigText
            holder.notificationView.findViewById<TextView>(R.id.notification_timestamp).text = dateFormat.format(Date(notifications[position].timestamp ?: 0))

            val tintColor = notifications[position].color?.let {if (it == 0) ContextCompat.getColor(context,
                R.color.defaultNotificationTintColor
            ) else it}
            notifications[position].icon?.let {iconBlob ->
                val icon = bitmapDrawableConverter.toDrawable(iconBlob)
                tintColor?.let { icon.setColorFilter(it, PorterDuff.Mode.SRC_ATOP) }
                holder.notificationView.findViewById<ImageView>(R.id.notification_icon).setImageDrawable(icon)
            }

            notifications[position].packageName?.let {packageName ->
                getAppInfo(packageName)?.let {appInfo ->
                    val appNameTv = holder.notificationView.findViewById<TextView>(
                        R.id.app_name
                    )
                    appNameTv.text = appInfo.appName
                    tintColor?.let { appNameTv.setTextColor(it) }
                }
            }

            holder.notificationView.setOnClickListener {
                val notificationLogIntent = Intent(context, NotificationLogsActivity::class.java).apply {
                    putExtra("notificationId", notifications[position].id)
                }
                context.startActivity(notificationLogIntent)
            }
        } else if (holder.itemViewType == LOAD_MORE_VIEW_TYPE) {
            holder as LoadMoreViewHolder
            holder.loadMoreView.setOnClickListener(onLoadMoreClickListener)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position == notifications.size) return LOAD_MORE_VIEW_TYPE
        return NOTIFICATION_VIEW_TYPE
    }

    private fun getAppInfo(packageName: String): AppInfo? {
        if (!appsInfo.containsKey(packageName)) {
            db.appsInfoDao().findByPackageName(packageName)?.let {
                appsInfo.put(packageName, it)
            }
        }

        return appsInfo[packageName]
    }

}
