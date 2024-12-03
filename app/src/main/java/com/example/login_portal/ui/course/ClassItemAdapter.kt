package com.example.login_portal.ui.course

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.login_portal.R

class ClassItemAdapter(
    private var itemList: List<ClassesOfCourse>,
    val context: Context,
    var clickable: Boolean = true) : RecyclerView.Adapter<ClassItemAdapter.ItemViewHolder>() {
    lateinit var onItemClick: ((ClassesOfCourse) -> Unit)

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val id: TextView = itemView.findViewById(R.id.class_item_id)
        val name: TextView = itemView.findViewById(R.id.class_item_name)
        val day: TextView = itemView.findViewById(R.id.class_item_day)
        val time: TextView = itemView.findViewById(R.id.class_item_time)
        val room: TextView = itemView.findViewById(R.id.class_item_room)
        val slot: TextView = itemView.findViewById(R.id.class_item_slot)
        val registered: TextView = itemView.findViewById(R.id.class_item_registered)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION)
                    onItemClick.invoke(itemList[position])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.class_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = itemList[position]
        holder.id.text = item.Id
        holder.name.text = item.Name
        holder.day.text = item.Day
        holder.time.text = item.Time
        holder.room.text = item.Room
        holder.slot.text = item.MaxSlot.toString()
        holder.registered.text = item.RegisteredSlot.toString()

        if (!clickable) {
            holder.itemView.isEnabled = false
            holder.itemView.alpha = 0.5F
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun resetSource(newItemList: List<ClassesOfCourse>) {
        itemList = newItemList
        notifyDataSetChanged()
    }
}