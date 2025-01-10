package com.example.login_portal.utils

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.ai.client.generativeai.GenerativeModel
import okhttp3.OkHttpClient
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import kotlin.collections.ArrayList

object GeminiService {
    private val dotenv = EnvLoader.loadEnv()

    private val httpClient: OkHttpClient = OkHttpClient()
    private val geminiModel: String by lazy { dotenv["GEMINI_MODEL_ID"] ?: throw IllegalStateException("GEMINI_MODEL_ID not found") }
    private val geminiApiKey: String by lazy { dotenv["GEMINI_API_KEY"] ?: throw IllegalStateException("GEMINI_API_KEY not found") }

    private var generativeModel : GenerativeModel? = null
    private val maxChatHistoryMessages = 100

    init {
        generativeModel = GenerativeModel(
            modelName = geminiModel,
            apiKey = geminiApiKey
        )
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
}
