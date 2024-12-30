package com.example.login_portal.ui.scholarship

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.login_portal.R

class ScholarshipItemAdapter(private var itemList: List<Scholarship>, val context: Context)
    : RecyclerView.Adapter<ScholarshipItemAdapter.ItemViewHolder>() {
    lateinit var onItemClick: ((Scholarship) -> Unit)

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTV: TextView = itemView.findViewById(R.id.scholarship_item_name)
        val sponsorTV: TextView = itemView.findViewById(R.id.scholarship_item_sponsor)
        val statusTV: TextView = itemView.findViewById(R.id.scholarship_item_status)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION)
                    onItemClick.invoke(itemList[position])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.scholarship_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = itemList[position]
        holder.nameTV.text = item.Name
        holder.sponsorTV.text = item.Sponsor
        holder.statusTV.text = item.statusWithItsColor(context).first

        holder.statusTV.setTextColor(item.statusWithItsColor(context).second)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    fun resetSource(newItemList: List<Scholarship>) {
        itemList = newItemList
        notifyDataSetChanged()
    }
}