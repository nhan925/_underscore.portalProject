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
import com.example.login_portal.ui.course.Period
import java.text.Format
import java.text.SimpleDateFormat
import java.util.Locale

class RegistrationPeriodItemAdapter(private var itemList: List<Period>, val context: Context)
    : RecyclerView.Adapter<RegistrationPeriodItemAdapter.ItemViewHolder>() {
    lateinit var onItemClick: ((Period) -> Unit)

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTV: TextView = itemView.findViewById(R.id.course_register_period_title)
        val openTV: TextView = itemView.findViewById(R.id.course_register_period_open)
        val closeTV: TextView = itemView.findViewById(R.id.course_register_period_close)
        val statusTV: TextView = itemView.findViewById(R.id.course_register_period_status)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION)
                    onItemClick.invoke(itemList[position])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.registration_period_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = itemList[position]
        holder.titleTV.text = item.Title
        holder.openTV.text = item.getFormattedDate(item.OpenDate)
        holder.closeTV.text = item.getFormattedDate(item.CloseDate)
        holder.statusTV.text = item.statusWithItsColor(context).first

        holder.statusTV.setTextColor(item.statusWithItsColor(context).second)

        if (!item.canRegister) {
            holder.itemView.isEnabled = false
            holder.itemView.alpha = 0.5F
        }
        else {
            holder.itemView.isEnabled = true
            holder.itemView.alpha = 1F
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun resetSource(newItemList: List<Period>) {
        itemList = newItemList
        notifyDataSetChanged()
    }
}