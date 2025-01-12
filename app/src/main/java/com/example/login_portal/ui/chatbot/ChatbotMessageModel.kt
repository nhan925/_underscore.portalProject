package com.example.login_portal.ui.chatbot

import android.graphics.Bitmap

data class Message (
    var message : String = "",
    var images : List<Bitmap>? = null,
    var isUser : Boolean? = null,
)