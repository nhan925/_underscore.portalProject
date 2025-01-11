package com.example.login_portal.ui.chatbot

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.InflateException
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.login_portal.R
import com.example.login_portal.ui.payment_history.PaymentHistoryItem

class AdapterForChatbot (
    private var messageHistoryList : List<Message>
): RecyclerView.Adapter<AdapterForChatbot.MessageHistoryViewHolder>() {

    class MessageHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val textMessage = itemView.findViewById<TextView>(R.id.tv_message_chatbot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageHistoryViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.fragment_chatbot_message_item,parent,false)
        return MessageHistoryViewHolder(itemView)
    }

    override fun getItemCount() = messageHistoryList.size

    override fun onBindViewHolder(holder: MessageHistoryViewHolder, position: Int) {
        val message = messageHistoryList[position]

        holder.textMessage.text = message.message

        if (message.isUser == true) {
            holder.textMessage.setBackgroundDrawable(ContextCompat.getDrawable(holder.itemView.context, R.drawable.send_round_box))
            holder.textMessage.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.white))
            (holder.textMessage.layoutParams as FrameLayout.LayoutParams).gravity = Gravity.END
        } else {
            holder.textMessage.setBackgroundDrawable(ContextCompat.getDrawable(holder.itemView.context, R.drawable.receive_round_box))
            holder.textMessage.setTextColor(ContextCompat.getColor(holder.itemView.context, android.R.color.black))
            (holder.textMessage.layoutParams as FrameLayout.LayoutParams).gravity = Gravity.START
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: List<Message>) {
        messageHistoryList = newList
        notifyDataSetChanged()
    }
}