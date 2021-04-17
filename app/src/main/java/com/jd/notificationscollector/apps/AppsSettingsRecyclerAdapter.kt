package com.jd.notificationscollector.apps

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.jd.notificationscollector.BitmapDrawableConverter
import com.jd.notificationscollector.R
import com.jd.notificationscollector.model.AppInfo

private const val APP_VIEW_TYPE = 0

class AppsSettingsRecyclerAdapter(private val apps: List<AppInfo>,
                                  private val context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val bitmapDrawableConverter = BitmapDrawableConverter(context)

    class AppItemViewHolder(val appItemView: CardView) : RecyclerView.ViewHolder(appItemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val appItemView = LayoutInflater.from(parent.context).inflate(R.layout.apps_recycler_item, parent, false) as CardView
        return AppItemViewHolder(appItemView)
    }

    override fun getItemCount(): Int = apps.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder as AppItemViewHolder
        holder.appItemView.findViewById<TextView>(R.id.apps_recycler_app_name).text = apps[position].appName

        apps[position].appIcon?.let {
            val icc = bitmapDrawableConverter.toDrawable(it)
            holder.appItemView.findViewById<ImageView>(R.id.apps_recycler_app_icon).setImageDrawable(icc)
        }

        holder.appItemView.setOnClickListener {
            val appSettingsIntent = Intent(context, AppSettings::class.java)
            appSettingsIntent.putExtra("app_package_name", apps[position].packageName)

            when (context) {
                is Activity -> context.startActivityForResult(appSettingsIntent, 200)
                is Fragment -> context.startActivityForResult(appSettingsIntent, 200)
                else -> context.startActivity(appSettingsIntent)
            }
        }
    }

}
