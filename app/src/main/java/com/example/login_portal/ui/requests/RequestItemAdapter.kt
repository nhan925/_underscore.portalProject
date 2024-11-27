package com.example.login_portal.ui.requests

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.login_portal.R
import java.text.Format
import java.text.SimpleDateFormat
import java.util.Locale

class RequestItemAdapter(private var itemList: List<RequestItem>, val context: Context) : RecyclerView.Adapter<RequestItemAdapter.ItemViewHolder>() {
    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTV: TextView = itemView.findViewById(R.id.request_item_title)
        val timeTV: TextView = itemView.findViewById(R.id.request_item_time)
        val statusTV: TextView = itemView.findViewById(R.id.request_item_status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.request_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = itemList[position]
        holder.titleTV.text = item.getRequestName(context)
        holder.timeTV.text = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(item.SubmittedAt)
        holder.statusTV.text = item.Status
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun resetSource(newItemList: List<RequestItem>) {
        itemList = newItemList
        notifyDataSetChanged()
    }
}