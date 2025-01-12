package com.example.login_portal.ui.chatbot

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.getString
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.login_portal.R
import com.example.login_portal.utils.GeminiService


class ChatbotViewModel : ViewModel()  {
    private val _messages = MutableLiveData<List<Message>>()
    val messages : LiveData<List<Message>> get()= _messages

    suspend fun sendMessage (context: Context, message : String, images : List<Bitmap>){
        val updatedMessage = if (images.isNotEmpty()) {
            val imageCountMessage = context.getString(R.string.picture_attach_chatbot, images.size)
            "$message\n$imageCountMessage"
        } else {
            message
        }
        addMessage(Message(updatedMessage, images, true))

        val fullResponse = GeminiService.sendTextWithImages(message, images)
        addMessage(Message(fullResponse, images, false))
        Log.e("CHAT BOT", fullResponse)
    }

    fun addMessage(newMessage: Message) {
        val messageList = _messages.value?.toMutableList() ?: mutableListOf()
        messageList.add(newMessage)
        _messages.postValue(messageList)

    }
}