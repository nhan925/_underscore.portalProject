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

class AdapterForListTuition(
    private var tuitionList: List<Tuition>,
    private val onItemClicked: (Tuition) -> Unit
) : RecyclerView.Adapter<AdapterForListTuition.TuitionViewHolder>() {

    // ViewHolder class
    class TuitionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val yearSemesterTextView: TextView = itemView.findViewById(R.id.tuition_year_semester)
        val courseCountTextView: TextView = itemView.findViewById(R.id.tuition_course_count)
        val feeTextView: TextView = itemView.findViewById(R.id.tuition_fee)
        val statusTextView: TextView = itemView.findViewById(R.id.tuition_status)
    }

    // Inflate the item layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TuitionViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.tuition_list_item, parent, false)
        return TuitionViewHolder(itemView)
    }

    // Bind data to the item views
    override fun onBindViewHolder(holder: TuitionViewHolder, position: Int) {
        val tuition = tuitionList[position]

        val yearTitle = holder.itemView.context.resources.getString(R.string.tuition_year)
        val semesterTitle = holder.itemView.context.resources.getString(R.string.tuition_semester)
        holder.yearSemesterTextView.text = "$yearTitle ${tuition.year} | $semesterTitle ${tuition.semester}"

        val courseCountTitle = holder.itemView.context.resources.getString(R.string.tuition_number_of_subject)
        holder.courseCountTextView.text = "$courseCountTitle ${tuition.totalCourse}"

        val formattedTuitionFee = formatTuitionFee(tuition.totalTuitionFee)
        val feeTitle = holder.itemView.context.resources.getString(R.string.tuition_fee)
        holder.feeTextView.text = "$feeTitle ${formattedTuitionFee} VND"

        val paidTitle = holder.itemView.context.resources.getString(R.string.tuition_status_paid)
        val notPaidTitle = holder.itemView.context.resources.getString(R.string.tuition_status_not_paid)
        val notYetTitle = holder.itemView.context.resources.getString(R.string.tuition_status_not_yet)
        val overdueTitle = holder.itemView.context.resources.getString(R.string.tuition_status_overdue)

        if(tuition.status == "PAID"){
            holder.statusTextView.text = paidTitle
        }
        else if(tuition.status == "NOT_PAID"){
            holder.statusTextView.text = notPaidTitle
        }
        else if (tuition.status == "NOT_YET"){
            holder.statusTextView.text = notYetTitle
        }
        else if (tuition.status == "OVERDUE"){
            holder.statusTextView.text = overdueTitle
        }

        // Set màu và trạng thái click
        if (tuition.status == "PAID") {
            holder.statusTextView.setTextColor(
                ContextCompat.getColor(holder.itemView.context, R.color.green)
            )
            holder.itemView.alpha = 0.5F
        }
        else if (tuition.status == "NOT_PAID") {
            holder.statusTextView.setTextColor(
                ContextCompat.getColor(holder.itemView.context, R.color.red)
            )
            holder.itemView.alpha = 1.0F
        }
        else if (tuition.status == "NOT_YET") {
            holder.statusTextView.setTextColor(
                ContextCompat.getColor(holder.itemView.context, R.color.item_dark_overlay)
            )
            holder.itemView.alpha = 0.5F
        }
        else if (tuition.status == "OVERDUE") {
            holder.statusTextView.setTextColor(
                ContextCompat.getColor(holder.itemView.context, R.color.orange)
            )
            holder.itemView.alpha = 0.5F
        }

        holder.itemView.setOnClickListener {
            if (tuition.status == "NOT_PAID") {
                onItemClicked(tuition)
            }
        }
    }

    fun formatTuitionFee(tuitionFee: Int): String {
        val formatter = DecimalFormat("#,###")
        return formatter.format(tuitionFee)
    }

    // Return the size of your dataset
    override fun getItemCount() = tuitionList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newCourseList: List<Tuition>) {
        tuitionList = newCourseList
        notifyDataSetChanged()
    }
}