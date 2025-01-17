package com.example.login_portal.ui.admin_manage_enrollment_period
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.login_portal.R
import java.text.SimpleDateFormat


class RegistrationPeriodAdapter(
    private var registrationPeriods: List<RegistrationPeriod>,
    private val onPeriodClicked: (RegistrationPeriod) -> Unit
) : RecyclerView.Adapter<RegistrationPeriodAdapter.PeriodViewHolder>() {

    class PeriodViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.course_register_period_title)
        val status: TextView = view.findViewById(R.id.course_register_period_status)
        val openDate: TextView = view.findViewById(R.id.course_register_period_open)
        val closeDate: TextView = view.findViewById(R.id.course_register_period_close)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeriodViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.registration_period_item, parent, false)
        return PeriodViewHolder(view)
    }

    override fun onBindViewHolder(holder: PeriodViewHolder, position: Int) {
        val period = registrationPeriods[position]
        val context = holder.itemView.context

        holder.title.text = period.name
        holder.openDate.text = period.startDate
        holder.closeDate.text = period.endDate

        // Calculate status based on current date
        val currentDate = System.currentTimeMillis()
        val startDate = SimpleDateFormat("yyyy-MM-dd").parse(period.startDate).time
        val endDate = SimpleDateFormat("yyyy-MM-dd").parse(period.endDate).time

        holder.status.text = when {
            currentDate < startDate -> context.getString(R.string.status_upcoming)
            currentDate > endDate -> context.getString(R.string.status_closed)
            else -> context.getString(R.string.status_open)
        }

        holder.itemView.setOnClickListener {
            onPeriodClicked(period)
        }
    }

    override fun getItemCount() = registrationPeriods.size

    fun updatePeriods(newPeriods: List<RegistrationPeriod>) {
        registrationPeriods = newPeriods
        notifyDataSetChanged()
    }
}