package com.example.login_portal.ui.admin_review_feedback

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.login_portal.R

class AdapterForListFeedbackApp (
    private var feedBackList : List<FeedbackApp>) :
    RecyclerView.Adapter<AdapterForListFeedbackApp.FeedbackAppViewHolder>(){
    lateinit var onItemClick: ((FeedbackApp) -> Unit)

    inner class FeedbackAppViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val feedBackType : TextView = itemView.findViewById(R.id.feedback_app_item_type)
        val feedBackSubmittedAt : TextView = itemView.findViewById(R.id.feedback_app_item_submitted_at)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION)
                    onItemClick.invoke(feedBackList[position])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedbackAppViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.feedback_app_item, parent, false)
        return FeedbackAppViewHolder(itemView)
    }

    override fun getItemCount() = feedBackList.size

    override fun onBindViewHolder(holder: FeedbackAppViewHolder, position: Int) {
        val feedbackApp = feedBackList[position]
        if (feedbackApp.type == "Error Report" || feedbackApp.type == "Báo lỗi") {
            holder.feedBackType.text = holder.itemView.context.getString(R.string.review_feedback_detail_category_error)
        } else if (feedbackApp.type == "Give feedback" || feedbackApp.type == "Góp ý") {
            holder.feedBackType.text = holder.itemView.context.getString(R.string.review_feedback_detail_category_feedback)
        }
        holder.feedBackSubmittedAt.text = feedbackApp.getFormattedDate()

    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newfeedBackList: List<FeedbackApp>) {
        feedBackList = newfeedBackList
        notifyDataSetChanged()
    }
}