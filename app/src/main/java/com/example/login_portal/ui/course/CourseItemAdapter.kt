package com.example.login_portal.ui.course

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.login_portal.R

class CourseItemAdapter(
    private var itemList: List<Course>,
    val context: Context,
    var isRegistered: Boolean = false,
    var clickable: Boolean = true) : RecyclerView.Adapter<CourseItemAdapter.ItemViewHolder>() {
    lateinit var onItemClick: ((Course) -> Unit)

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.course_item_name)
        val code: TextView = itemView.findViewById(R.id.course_item_code)
        val credits: TextView = itemView.findViewById(R.id.course_item_credits)
        val classLabel: TextView = itemView.findViewById(R.id.course_item_class_label)
        val className: TextView = itemView.findViewById(R.id.course_item_class)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION)
                    onItemClick.invoke(itemList[position])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.course_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = itemList[position]
        holder.name.text = item.Name
        holder.code.text = item.Id
        holder.credits.text = item.Credits.toString()

        if (isRegistered) {
            holder.className.text = item.ClassName
        }
        else {
            holder.classLabel.visibility = View.INVISIBLE
            holder.className.visibility = View.INVISIBLE
        }

        if (!clickable) {
            holder.itemView.isEnabled = false
            holder.itemView.alpha = 0.5F
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun resetSource(newItemList: List<Course>) {
        itemList = newItemList
        notifyDataSetChanged()
    }
}