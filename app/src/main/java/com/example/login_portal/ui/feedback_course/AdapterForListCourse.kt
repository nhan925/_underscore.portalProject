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
        val ratedString = holder.itemView.context.resources.getString(R.string.rated)
        val notRatedString = holder.itemView.context.resources.getString(R.string.not_rated)
        val notYetString = holder.itemView.context.resources.getString(R.string.course_feedback_not_yet_status)

        if(course.status == "DONE"){
            holder.courseStatusTextView.text = ratedString
        } else if(course.status == "NOT_FEEDBACK"){
            holder.courseStatusTextView.text = notRatedString
        } else if (course.status == "NOT_YET"){
            holder.courseStatusTextView.text = notYetString
        } else {
            holder.courseStatusTextView.text = course.status
        }

        // Set màu và trạng thái click
        if (course.status == "DONE") {
            holder.courseStatusTextView.setTextColor(
                ContextCompat.getColor(holder.itemView.context, R.color.green)
            )
            holder.itemView.alpha = 0.5F
        } else if (course.status == "NOT_FEEDBACK") {
            holder.courseStatusTextView.setTextColor(
                ContextCompat.getColor(holder.itemView.context, R.color.red)
            )
        } else if (course.status == "NOT_YET") {
            holder.courseStatusTextView.setTextColor(
                ContextCompat.getColor(holder.itemView.context, R.color.item_dark_overlay)
            )
            holder.itemView.alpha = 0.5F
        }

        holder.itemView.setOnClickListener {
            if (course.status == "NOT_FEEDBACK") {
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
