package com.example.login_portal.ui.feedback_course

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.login_portal.R

// Define the adapter for course list
class AdapterForListCourse(
    private val courseList: List<Course>,
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
        holder.courseNameTextView.text = course.name
        holder.courseCodeTextView.text = course.code
        holder.teacherNameTextView.text = course.teacherName
        holder.courseStatusTextView.text = if (course.status) "Đã đánh giá" else "Chưa đánh giá"

        // Set color for course status
        when (course.status) {
            true -> holder.courseStatusTextView.setTextColor(holder.itemView.resources.getColor(R.color.green))
            false -> holder.courseStatusTextView.setTextColor(holder.itemView.resources.getColor(R.color.red))
        }

        // Set OnClickListener
        holder.itemView.setOnClickListener {
            onItemClicked(course)
        }
    }

    // Return the size of your dataset
    override fun getItemCount() = courseList.size
}
