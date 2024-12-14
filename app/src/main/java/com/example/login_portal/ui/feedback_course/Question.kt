package com.example.login_portal.ui.feedback_course

data class Question(
    val content: String,     // Nội dung câu hỏi
    var rating: Int,         // Giá trị đánh giá (1-5)
    var isAnswered: Boolean = false
)