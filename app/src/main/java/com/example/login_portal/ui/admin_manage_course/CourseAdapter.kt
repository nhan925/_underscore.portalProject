package com.example.login_portal.ui.admin_manage_course

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.login_portal.R



class CourseAdapter(
    private var courses: List<Course>,
    private val onCourseClicked: (Course) -> Unit
) : RecyclerView.Adapter<CourseAdapter.CourseViewHolder>() {

    class CourseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.course_item_name)
        val code: TextView = view.findViewById(R.id.course_item_code)
        val credits: TextView = view.findViewById(R.id.course_item_credits)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.course_item, parent, false)
        return CourseViewHolder(view)
    }


    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        val course = courses[position]

        val context = holder.itemView.context

        holder.name.text = course.name
        holder.code.text = course.id
        holder.credits.text = course.credits.toString()

        holder.itemView.setOnClickListener {
            onCourseClicked(course)
        }
    }

    override fun getItemCount() = courses.size

    fun updateCourses(newCourses: List<Course>) {
        courses = newCourses
        notifyDataSetChanged()
    }
}