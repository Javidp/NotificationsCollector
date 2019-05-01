package com.jd.notificationscollector

import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.jd.notificationscollector.model.Notification

class NotificationsRecyclerAdapter(private val notifications: MutableList<Notification>, private val onLoadMoreClickListener: View.OnClickListener): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val NOTIFICATION_VIEW_TYPE = 0
        private const val LOAD_MORE_VIEW_TYPE = 1
    }

    class NotificationCardViewHolder(val notificationView: CardView) : RecyclerView.ViewHolder(notificationView)
    class LoadMoreViewHolder(val loadMoreView: Button) : RecyclerView.ViewHolder(loadMoreView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == NOTIFICATION_VIEW_TYPE) {
            val notificationCard = LayoutInflater.from(parent.context).inflate(R.layout.notification_item, parent, false) as CardView
            return NotificationCardViewHolder(notificationCard)
        }
        val loadMoreItem = LayoutInflater.from(parent.context).inflate(R.layout.notifications_load_more_item, parent, false) as Button
        return LoadMoreViewHolder(loadMoreItem)
    }

    override fun getItemCount(): Int = notifications.size + 1

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == NOTIFICATION_VIEW_TYPE) {
            holder as NotificationCardViewHolder
            holder.notificationView.findViewById<TextView>(R.id.notification_title).text = notifications[position].title
            holder.notificationView.findViewById<TextView>(R.id.notification_text).text = notifications[position].text
            holder.notificationView.findViewById<TextView>(R.id.notification_big_text).text = notifications[position].bigText
        } else if (holder.itemViewType == LOAD_MORE_VIEW_TYPE) {
            holder as LoadMoreViewHolder
            holder.loadMoreView.setOnClickListener(onLoadMoreClickListener)
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position == notifications.size) return LOAD_MORE_VIEW_TYPE
        return NOTIFICATION_VIEW_TYPE
    }

}
