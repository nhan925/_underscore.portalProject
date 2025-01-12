package com.example.login_portal.ui.admin_noti

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.login_portal.R
import com.example.login_portal.ui.notification.Notification

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
        holder.date.text = notification.time

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
