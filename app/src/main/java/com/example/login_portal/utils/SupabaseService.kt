package com.example.login_portal.utils

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.io.InputStream

object SupabaseService {
    private val httpClient: OkHttpClient = OkHttpClient()
    val dotenv = EnvLoader.loadEnv()

    private val supabaseUrl: String = dotenv["SUPABASE_URL"].toString()
    private val supabaseKey: String = dotenv["SUPABASE_KEY"].toString()
    private val bucketName: String = dotenv["SUPABASE_BUCKET_NAME"].toString()

    // Function to upload a file given the URI
    suspend fun uploadFile(context: Context, fileUri: Uri): String? {
        // Get the InputStream of the file
        val inputStream: InputStream? = context.contentResolver.openInputStream(fileUri)
        val fileName = getFileName(context, fileUri)
        val mimeType = context.contentResolver.getType(fileUri) ?: "application/octet-stream"

        // If the InputStream is not null, proceed to upload
        inputStream?.let {
            return uploadFileAsync(it, fileName, mimeType)
        }
        return null
    }

    private suspend fun uploadFileAsync(inputStream: InputStream, fileName: String, mimeType: String): String? {
        // Generate a unique file name using GUID-like timestamp for uniqueness
        val uniqueFileName = "${System.currentTimeMillis()}_${fileName}"

        // Read the file bytes from InputStream
        val fileBytes = inputStream.readBytes()

        // Create the request body with the file content
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "file",
                uniqueFileName,
                RequestBody.create(mimeType.toMediaType(), fileBytes)
            )
            .build()

        // Build the request with the appropriate Authorization header and POST method
        val request = Request.Builder()
            .url("$supabaseUrl/storage/v1/object/$bucketName/$uniqueFileName")
            .addHeader("Authorization", "Bearer $supabaseKey")
            .addHeader("apikey", supabaseKey)
            .post(requestBody)
            .build()

        // Execute the request in IO context to avoid blocking the main thread
        return withContext(Dispatchers.IO) {
            var response: Response? = null
            try {
                response = httpClient.newCall(request).execute()
                if (response.isSuccessful) {
                    // Construct the public URL for the uploaded file
                    return@withContext "$supabaseUrl/storage/v1/object/public/$bucketName/$uniqueFileName"
                } else {
                    throw IOException("Upload failed with code: ${response.code}")
                }
            } catch (e: Exception) {
                throw e // Catch and rethrow the exception
            } finally {
                response?.body?.close() // Ensure to close the response body to free resources
            }
        }
    }

    // Helper function to get the file name from the URI
    private fun getFileName(context: Context, uri: Uri): String {
        var fileName = ""
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val nameIndex = it.getColumnIndex(android.provider.MediaStore.Images.Media.DISPLAY_NAME)
            if (it.moveToFirst()) {
                fileName = it.getString(nameIndex)
            }
        }
        return fileName.ifEmpty { "default_filename" }
    }
}
