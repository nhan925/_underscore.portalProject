package com.example.login_portal.ui.admin_notification

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.login_portal.R
import com.example.login_portal.ui.notification.Notification
import androidx.core.content.ContextCompat
import com.example.login_portal.utils.formatDateTimeVer2

class AdminNotificationAdapter(
    private var notifications: List<Notification>,
    private val onNotificationClicked: (Notification) -> Unit
) : RecyclerView.Adapter<AdminNotificationAdapter.AdminNotificationViewHolder>() {

    class AdminNotificationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.tv_notification_title)
        val date: TextView = view.findViewById(R.id.tv_notification_time)
        val important: TextView = view.findViewById(R.id.tv_notification_important)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminNotificationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.noti_admin_item, parent, false)
        return AdminNotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdminNotificationViewHolder, position: Int) {
        val notification = notifications[position]
        holder.title.text = notification.title
        val formattedDate = formatDateTimeVer2(notification.time)
        holder.date.text = formattedDate

        if (notification.isImportant) {
            holder.important.text = "Quan Trọng"
            holder.important.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.holo_red_dark))
            holder.important.visibility = View.VISIBLE
        } else {
            holder.important.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            onNotificationClicked(notification)
        }
    }

    override fun getItemCount(): Int = notifications.size

    fun updateNotifications(newNotifications: List<Notification>) {
        notifications = newNotifications
        notifyDataSetChanged()
    }
}
