package com.example.login_portal.ui.course

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.login_portal.R

class ClassItemAdapter(
    private var itemList: List<ClassesOfCourse>,
    val context: Context,
    var clickable: Boolean = true) : RecyclerView.Adapter<ClassItemAdapter.ItemViewHolder>() {
    var selectedPosition = RecyclerView.NO_POSITION

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val id: TextView = itemView.findViewById(R.id.class_item_id)
        val name: TextView = itemView.findViewById(R.id.class_item_name)
        val day: TextView = itemView.findViewById(R.id.class_item_day)
        val time: TextView = itemView.findViewById(R.id.class_item_time)
        val room: TextView = itemView.findViewById(R.id.class_item_room)
        val slot: TextView = itemView.findViewById(R.id.class_item_slot)
        val registered: TextView = itemView.findViewById(R.id.class_item_registered)

        private val selectableItemBackground: Int
        private val colorSurface: Int

        init {
            val typedArray = context.obtainStyledAttributes(intArrayOf(
                android.R.attr.colorBackground, // Fallback for unselected background
                com.google.android.material.R.attr.colorSurfaceVariant
            ))
            colorSurface = typedArray.getColor(0, Color.WHITE) // Default unselected color (WHITE)
            selectableItemBackground = typedArray.getColor(1, Color.LTGRAY) // Selectable Color
            typedArray.recycle()

            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION && selectedPosition != position) {
                    selectedPosition = position // Toggle selection
                    notifyDataSetChanged() // Update all items
                }
            }
        }

        fun bind(isSelected: Boolean) {
            itemView.isSelected = isSelected
            val constraintLayout = itemView.findViewById<ConstraintLayout>(R.id.class_item_constraint_layout)
            if (isSelected) {
                constraintLayout.setBackgroundColor(selectableItemBackground)
            } else {
                constraintLayout.setBackgroundColor(colorSurface)
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

        holder.bind(position == selectedPosition)

        if (!clickable) {
            holder.itemView.isEnabled = false
            holder.itemView.alpha = 0.5F
        }

        if (item.RegisteredSlot >= item.MaxSlot) {
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

    fun resetSource(newItemList: List<ClassesOfCourse>, selectedPos: Int = RecyclerView.NO_POSITION) {
        itemList = newItemList
        selectedPosition = selectedPos
        notifyDataSetChanged()
    }
}