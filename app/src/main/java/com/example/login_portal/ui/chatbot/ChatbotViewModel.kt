package com.example.login_portal.ui.chatbot

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.login_portal.utils.GeminiService


class ChatbotViewModel : ViewModel()  {
    private val _messages = MutableLiveData<List<Message>>()
    val messages : LiveData<List<Message>> get()= _messages

    suspend fun sendMessage (message : String, images : List<Bitmap>){
        addMessage(Message(message, null, true))

        val fullResponse = GeminiService.sendTextWithImages(message, images)
        addMessage(Message(fullResponse, null, false))
        Log.e("CHAT BOT", fullResponse)
    }

    fun addMessage(newMessage: Message) {
        val messageList = _messages.value?.toMutableList() ?: mutableListOf()
        messageList.add(newMessage)
        _messages.postValue(messageList)

    }
}