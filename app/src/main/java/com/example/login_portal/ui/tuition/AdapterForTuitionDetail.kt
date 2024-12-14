package com.example.login_portal.ui.tuition

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.login_portal.R
import java.text.DecimalFormat
class AdapterForTuitionDetail(
    private var courseList: List<TuitionDetail>
) : RecyclerView.Adapter<AdapterForTuitionDetail.TuitionDetailViewHolder>() {

    // ViewHolder class
    class TuitionDetailViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val courseNameTextView: TextView = itemView.findViewById(R.id.tuition_detail_course_name)
        val courseCodeTextView: TextView = itemView.findViewById(R.id.tuition_detail_course_code)
        val courseFeeTextView: TextView = itemView.findViewById(R.id.tuition_detail_course_fee)
        val courseCreditTextView: TextView = itemView.findViewById(R.id.tuition_detail_course_credit)
    }

    // Inflate the item layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TuitionDetailViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.tuition_detail_list_item, parent, false)
        return TuitionDetailViewHolder(itemView)
    }

    // Bind data to the item views
    override fun onBindViewHolder(holder: TuitionDetailViewHolder, position: Int) {
        val course = courseList[position]
        holder.courseNameTextView.text = course.courseName

        holder.courseCodeTextView.text = course.courseID
        val formatedCourseFee = formatTuitionFee(course.courseFee)
        holder.courseFeeTextView.text = formatedCourseFee

        holder.courseCreditTextView.text = course.courseCredit.toString()
    }

    // Return the size of your dataset
    override fun getItemCount() = courseList.size

    fun formatTuitionFee(tuitionFee: Int): String {
        val formatter = DecimalFormat("#,###")
        return formatter.format(tuitionFee)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newCourseList: List<TuitionDetail>) {
        courseList = newCourseList
        notifyDataSetChanged()
    }
}