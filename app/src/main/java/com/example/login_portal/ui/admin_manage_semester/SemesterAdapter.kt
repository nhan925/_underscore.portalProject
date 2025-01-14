package com.example.login_portal.ui.admin_manage_semester

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.login_portal.R


class SemesterAdapter(
    private var semesters: List<Semester>,
    private val onSemesterClicked: (Semester) -> Unit
) : RecyclerView.Adapter<SemesterAdapter.SemesterViewHolder>() {

    class SemesterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.semester_title)
        val startDate: TextView = view.findViewById(R.id.semester_start_date)
        val endDate: TextView = view.findViewById(R.id.semester_end_date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SemesterViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.admin_semester_item, parent, false)
        return SemesterViewHolder(view)
    }

    override fun onBindViewHolder(holder: SemesterViewHolder, position: Int) {
        val semester = semesters[position]
        val context = holder.itemView.context

        holder.title.text = context.getString(
            R.string.semester_title_format,
            semester.semesterNum,
            semester.year
        )

        holder.startDate.text = context.getString(
            R.string.semester_start_date_format,
            semester.startDate
        )

        holder.endDate.text = context.getString(
            R.string.semester_end_date_format,
            semester.endDate
        )


        holder.itemView.setOnClickListener {
            onSemesterClicked(semester)
        }
    }

    override fun getItemCount() = semesters.size

    fun updateSemesters(newSemesters: List<Semester>) {
        semesters = newSemesters
        notifyDataSetChanged()
    }
}