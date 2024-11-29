package com.example.login_portal.ui.feedback_course

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.login_portal.R

// Define the adapter for course list
class AdapterForListCourse(
    private var courseList: List<Course>,
    private val onItemClicked: (Course) -> Unit
) : RecyclerView.Adapter<AdapterForListCourse.CourseViewHolder>() {

    // ViewHolder class
    class CourseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val courseNameTextView: TextView = itemView.findViewById(R.id.fb_course_name)
        val courseCodeTextView: TextView = itemView.findViewById(R.id.fb_course_code)
        val teacherNameTextView: TextView = itemView.findViewById(R.id.fb_couse_teacher_name)
        val courseStatusTextView: TextView = itemView.findViewById(R.id.fb_course_status)
    }

    // Inflate the item layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.course_item_list, parent, false)
        return CourseViewHolder(itemView)
    }

    // Bind data to the item views
    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val course = courseList[position]
        holder.courseNameTextView.text = course.courseName
        holder.courseCodeTextView.text = course.courseID
        holder.teacherNameTextView.text = course.teacherName
        holder.courseStatusTextView.text = if (course.status) "Đã đánh giá" else "Chưa đánh giá"

        // Set màu và trạng thái click
        if (course.status) {
            holder.courseStatusTextView.setTextColor(
                ContextCompat.getColor(holder.itemView.context, R.color.green)
            )
            holder.itemView.setBackgroundColor(
                ContextCompat.getColor(holder.itemView.context, com.google.android.material.R.color.material_dynamic_tertiary100)
            )
        } else {
            holder.courseStatusTextView.setTextColor(
                ContextCompat.getColor(holder.itemView.context, R.color.red)
            )
            holder.itemView.setBackgroundColor(
                ContextCompat.getColor(holder.itemView.context, R.color.white)
            )
        }

        // Xử lý sự kiện click
        holder.itemView.setOnClickListener {
            if (!course.status) {
                onItemClicked(course)
            }
        }
    }

    // Return the size of your dataset
    override fun getItemCount() = courseList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newCourseList: List<Course>) {
        courseList = newCourseList
        notifyDataSetChanged()
    }
}
