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
        chat = generativeModel?.startChat(history = emptyList())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getMimeType(filePath: String): String {
        val extension = Paths.get(filePath).toFile().extension.lowercase(Locale.ROOT)
        return when (extension) {
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "gif" -> "image/gif"
            "bmp" -> "image/bmp"
            "tiff" -> "image/tiff"
            else -> throw UnsupportedOperationException("Unsupported image format")
        }
    }

    suspend fun sendTextWithImages(prompt: String, images: List<Bitmap>): String {
        if (images.size > 4) throw IllegalArgumentException("Maximum number of images is 4")

        val inputContent = content {
            images.forEach { image(it) }
            text(prompt)
        }

        chat?.history?.add(content(role = "user") { text(prompt) })

        val fullResponse = chat?.sendMessage(inputContent)?.text ?: ""
        if (fullResponse.isNotEmpty()) {
            chat?.history?.add(content(role = "model") { text(fullResponse) })
        }

        return fullResponse
    }

}

