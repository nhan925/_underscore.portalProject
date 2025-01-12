package com.example.login_portal.utils

import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import okhttp3.OkHttpClient
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import kotlin.collections.ArrayList
data class ResponseChunks(val chunks: MutableList<String> = mutableListOf())

object GeminiService {
    private val dotenv = EnvLoader.loadEnv()

    private val httpClient: OkHttpClient = OkHttpClient()
    private val geminiModel: String by lazy { dotenv["GEMINI_MODEL_ID"] ?: throw IllegalStateException("GEMINI_MODEL_ID not found") }
    private val geminiApiKey: String by lazy { dotenv["GEMINI_API_KEY"] ?: throw IllegalStateException("GEMINI_API_KEY not found") }

    private var generativeModel: GenerativeModel? = null
    private var chat : Chat? = null
    private val maxChatHistoryMessages = 100

    init {
        generativeModel = GenerativeModel(
            modelName = geminiModel,
            apiKey = geminiApiKey
        )
        creatNewChatHistory()
    }

    suspend fun sendTextWithImages(prompt: String, images: List<Bitmap>): String {
        if (images.size > 3) throw IllegalArgumentException("Maximum number of images is 4")

        if ((chat?.history?.size ?: 0) >= maxChatHistoryMessages) {
            throw IllegalStateException("Chat history exceeds maximum allowed messages")
        }

        val promptWithLanguage = "$prompt and reply based on the language of the user message"
        val inputContent = content {
            images.forEach { image(it) }
            text(promptWithLanguage)
        }

        chat?.history?.add(content(role = "user") { text(prompt) })

        val fullResponse = chat?.sendMessage(inputContent)?.text ?: ""
        if (fullResponse.isNotEmpty()) {
            chat?.history?.add(content(role = "model") { text(fullResponse) })
        }

        return fullResponse
    }

    fun creatNewChatHistory(){
        val history = listOf(
            content(role = "user") { text(
                "Dự án tên là 'Ứng dụng mobile giúp quản lý việc học của sinh viên', được phát triển bởi nhóm _underscore. Các thành viên trong nhóm: Mai Nhật Nam, Nguyễn Minh Nguyên, Nguyễn Trọng Nhân, Ma Thanh Nhi, Nguyễn Thành Phát. Ứng dụng này phục vụ cho hai đối tượng người dùng chính: sinh viên và admin.Chức năng cho sinh viên bao gồm: đăng nhập, xem thông tin cá nhân, đăng ký học phần, kiểm tra thời khóa biểu, thanh toán học phí, và đặc biệt tích hợp AI chatbot.Chức năng cho admin bao gồm: quản lý sinh viên, lớp học, nhập điểm, và xử lý các yêu cầu từ sinh viên.")},
            content(role = "model") { text("I got it")}
        )
        chat = generativeModel?.startChat(history = history)
    }
}

