package com.example.login_portal.ui.payment_history

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.login_portal.R
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale

class AdapterForListPaymentHistory (
    private var paymentHistoryList: List<PaymentHistoryItem>
): RecyclerView.Adapter<AdapterForListPaymentHistory.PaymentHistoryViewHolder>() {

    // ViewHolder class
    class PaymentHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.payment_history_type)
        val amountTextView: TextView = itemView.findViewById(R.id.payment_history_amount)
        val dateTextView: TextView = itemView.findViewById(R.id.payment_history_date)
        val statusTextView: TextView = itemView.findViewById(R.id.payment_history_status)
    }

    // Inflate the item layout
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentHistoryViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.payment_history_item, parent, false)
        return PaymentHistoryViewHolder(itemView)
    }

    // Bind data to the item views
    override fun onBindViewHolder(holder: PaymentHistoryViewHolder, position: Int) {
        val payment_history_item = paymentHistoryList[position]

        if (payment_history_item.Type == "TUITION") {
            holder.titleTextView.text =
                holder.itemView.context.resources.getString(R.string.payment_history_tuition_title)
        } else {
            holder.titleTextView.text = "Unknown"
            // Add more type of payment here
        }


        val formattedTuitionFee = formatAmount(payment_history_item.Amount)
        holder.amountTextView.text = "${formattedTuitionFee} VND"

        holder.dateTextView.text = formatDate(payment_history_item.Date)

        val successStatusTitle =
            holder.itemView.context.resources.getString(R.string.payment_history_success_status)
        val failedStatusTitle =
            holder.itemView.context.resources.getString(R.string.payment_history_failed_status)
        holder.statusTextView.text =
            if (payment_history_item.IsSuccess) successStatusTitle else failedStatusTitle

        // Set màu
        if (payment_history_item.IsSuccess) {
            holder.statusTextView.setTextColor(
                ContextCompat.getColor(holder.itemView.context, R.color.green)
            )
        } else {
            holder.statusTextView.setTextColor(
                ContextCompat.getColor(holder.itemView.context, R.color.red)
            )
        }
    }


    fun formatAmount(amount: Double): String {
        val formatter = DecimalFormat("#,###")
        return formatter.format(amount)
    }

    fun formatDate(date: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss:aa", Locale.getDefault())
        return try {
            val parsedDate = inputFormat.parse(date)
            outputFormat.format(parsedDate ?: "")
        } catch (e: Exception) {
            date
        }
    }

    override fun getItemCount() = paymentHistoryList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: List<PaymentHistoryItem>) {
        paymentHistoryList = newList
        notifyDataSetChanged()
    }
}