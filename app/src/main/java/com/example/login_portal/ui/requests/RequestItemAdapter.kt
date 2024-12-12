package com.example.login_portal.ui.requests

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.login_portal.R
import java.text.Format
import java.text.SimpleDateFormat
import java.util.Locale

class RequestItemAdapter(private var itemList: List<RequestItem>, val context: Context) : RecyclerView.Adapter<RequestItemAdapter.ItemViewHolder>() {
    lateinit var onItemClick: ((RequestItem) -> Unit)

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTV: TextView = itemView.findViewById(R.id.request_item_title)
        val timeTV: TextView = itemView.findViewById(R.id.request_item_time)
        val statusTV: TextView = itemView.findViewById(R.id.request_item_status)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION)
                    onItemClick.invoke(itemList[position])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.request_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = itemList[position]
        holder.titleTV.text = item.getRequestName()
        holder.timeTV.text = item.getFormattedDate()
        holder.statusTV.text = item.Status

        when(item.Status) {
            context.resources.getString(R.string.request_status_processing) ->
                holder.statusTV.setTextColor(context.getColor(R.color.orange))
            context.resources.getString(R.string.request_status_canceled) ->
                holder.statusTV.setTextColor(context.getColor(R.color.red))
            else -> holder.statusTV.setTextColor(context.getColor(R.color.green))
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun resetSource(newItemList: List<RequestItem>) {
        itemList = newItemList
        notifyDataSetChanged()
    }
}