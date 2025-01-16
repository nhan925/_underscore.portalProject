package com.example.login_portal.ui.admin_manage_class

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.login_portal.databinding.ClassItemBinding

class ClassAdapter(private val onClassClick: (ClassInfo) -> Unit) :
    RecyclerView.Adapter<ClassAdapter.ClassViewHolder>() {

    private var classes = listOf<ClassInfo>()

    inner class ClassViewHolder(private val binding: ClassItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(classInfo: ClassInfo) {
            binding.apply {
                classItemId.text = classInfo.classId
                classItemName.text = classInfo.className
                classItemDay.text = classInfo.dayOfWeek
                classItemTime.text = "Tiết ${classInfo.startPeriod} - ${classInfo.endPeriod}"
                classItemRoom.text = classInfo.room
                classItemSlot.text = classInfo.maxEnrollment.toString()
                classItemRegistered.text = classInfo.enrollment.toString()
                classItemConstraintLayout.setOnClickListener {
                    onClassClick(classInfo)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClassViewHolder {
        val binding = ClassItemBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ClassViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClassViewHolder, position: Int) {
        holder.bind(classes[position])
    }

    override fun getItemCount() = classes.size

    fun updateClasses(newClasses: List<ClassInfo>) {
        classes = newClasses
        notifyDataSetChanged()
    }
}