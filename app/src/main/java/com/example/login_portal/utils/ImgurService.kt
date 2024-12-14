package com.example.login_portal.utils

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import org.json.JSONObject
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import java.io.File
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import java.io.InputStream
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.absolutePathString
import kotlin.io.path.pathString
import android.os.Environment;

object ImgurService {
    val dotenv = EnvLoader.loadEnv()

    private val httpClient: OkHttpClient = OkHttpClient()
    private val clientId: String by lazy { dotenv["IMGUR_CLIENT_ID"] ?: throw IllegalStateException("CLIENT_ID not found") }

    suspend fun uploadImageAsync(inputStream: InputStream, fileName: String, mimeType: String): String? {
        // Tạo request body từ InputStream
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "image",
                fileName,
                RequestBody.create(mimeType.toMediaType(), inputStream.readBytes())
            )
            .build()
        // Xây dựng yêu cầu với header Authorization và phương thức POST
        val request = Request.Builder()
            .url("https://api.imgur.com/3/image")
            .addHeader("Authorization", "Client-ID ${clientId}")
            .post(requestBody)
            .build()

        // Thực hiện yêu cầu trong IO context để tránh block main thread
        return withContext(Dispatchers.IO) {
            var response: Response? = null
            try {
                response = httpClient.newCall(request).execute()
                if (response.isSuccessful) {
                    val responseBody = response.body?.string() // Lấy nội dung phản hồi dưới dạng chuỗi

                    // Phân tích phản hồi JSON và lấy link ảnh
                    val jsonResponse = JSONObject(responseBody)
                    val link = jsonResponse.getJSONObject("data").getString("link")
                    return@withContext link // Trả về link của ảnh nếu thành công
                } else {
                    throw IOException("Upload failed with code: ${response.code}")
                }
            } catch (e: Exception) {
                throw e // Bắt và ném lại ngoại lệ
            } finally {
                response?.body?.close() // Đảm bảo đóng response body để giải phóng tài nguyên
            }
        }
    }

    private fun getMimeType(file: File): String {
        val extension = file.extension.lowercase()
        return when (extension) {
            "jpg", "jpeg" -> "image/jpeg"
            "png" -> "image/png"
            "gif" -> "image/gif"
            "bmp" -> "image/bmp"
            "tiff" -> "image/tiff"
            else -> throw IllegalArgumentException("Unsupported image format: $extension")
        }
    }
}
