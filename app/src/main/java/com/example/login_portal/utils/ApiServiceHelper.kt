package com.example.login_portal.utils

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.google.gson.Gson
import com.google.gson.GsonBuilder

object ApiServiceHelper {
    const val BASE_URL = "http://10.0.2.2:3001"
    var jwtToken: String? = null

    // Login using username and password
    fun login(username: String, password: String, callback: (Boolean) -> Unit) {
        val loginData = Gson().toJson(mapOf("username" to username, "password" to password))

        Fuel.post("$BASE_URL/rpc/login")
            .jsonBody(loginData)
            .responseString { _, response, result ->
                result.fold(
                    success= { data ->
                        if (data.isBlank() || data == "null") {
                            Handler(Looper.getMainLooper()).post {
                                callback(false)
                            }
                            return@fold
                        }
                        try {
                            jwtToken = data.trim('"')
                            Handler(Looper.getMainLooper()).post {
                                callback(true)
                            }
                        }
                        catch (e: Exception){
                            Handler(Looper.getMainLooper()).post {
                                callback(false)
                            }
                        }
                    },
                    failure = {
                        Handler(Looper.getMainLooper()).post {
                            callback(false)
                        }
            }
                )
            }
    }

    // Login using Outlook access token
    fun loginWithOutlook(accessToken: String, callback: (Boolean) -> Unit) {
        val outlookData = Gson().toJson(mapOf("v_token" to accessToken))

        Fuel.post("$BASE_URL/rpc/login_with_outlook")
            .jsonBody(outlookData)
            .responseString { _, response, result ->
                result.fold(
                    success= { data ->
                        if (data.isNullOrBlank() || data == "null") {
                            Handler(Looper.getMainLooper()).post {
                                callback(false)
                            }
                            return@fold
                        }
                        try {
                            jwtToken = data.trim('"')
                            Handler(Looper.getMainLooper()).post {
                                callback(true)
                            }
                        }
                        catch (e: Exception){
                            Handler(Looper.getMainLooper()).post {
                                callback(false)
                            }
                        }
                    },
                    failure = {
                        Handler(Looper.getMainLooper()).post {
                            callback(false)
                        }
                    }
                )
            }
    }

//    fun checkUsernameAndSendOTP(username: String, callback: (Boolean) -> Unit) {
//        val userData = Gson().toJson(mapOf("v_user_name" to username))
//
//        Log.d("ApiServiceHelper", "Sending request to check username and send OTP: $userData")
//
//        Fuel.post("$BASE_URL/rpc/check_username_and_send_otp")
//            .jsonBody(userData)
//            .responseString { request, response, result ->
//                Log.d("ApiServiceHelper", "Request URL: ${request.url}")
//                Log.d("ApiServiceHelper", "Response Status: ${response.statusCode}")
//                Log.d("ApiServiceHelper", "Response Headers: ${response.headers}")
//
//                result.fold(
//                    success = { data ->
//                        Log.d("ApiServiceHelper", "Raw Response: $data")
//                        try {
//                            val success = data.toBoolean()
//                            Log.d("ApiServiceHelper", "Parsed success value: $success")
//                            Handler(Looper.getMainLooper()).post {
//                                callback(success)
//                            }
//                        } catch (e: Exception) {
//                            Log.e("ApiServiceHelper", "Error parsing response", e)
//                            Handler(Looper.getMainLooper()).post {
//                                callback(false)
//                            }
//                        }
//                    },
//                    failure = { error ->
//                        Log.e("ApiServiceHelper", "API call failed. Error: ${error.message}", error)
//                        Handler(Looper.getMainLooper()).post {
//                            callback(false)
//                        }
//                    }
//                )
//            }
//    }

    // Add this function for public POST requests (no token required)
    private fun publicPost(endpoint: String, data: Any, callback: (String?) -> Unit) {
        val jsonData = GsonBuilder()
            .serializeNulls()
            .create()
            .toJson(data)

        Log.d("ApiServiceHelper", "Making public POST request to: $endpoint")
        Log.d("ApiServiceHelper", "Request data: $jsonData")

        Fuel.post("$BASE_URL$endpoint")
            .jsonBody(jsonData)
            .responseString { request, response, result ->
                Log.d("ApiServiceHelper", "Response status: ${response.statusCode}")

                result.fold(
                    success = { responseData ->
                        Log.d("ApiServiceHelper", "Response data: $responseData")
                        Handler(Looper.getMainLooper()).post {
                            callback(responseData)
                        }
                    },
                    failure = { error ->
                        Log.e("ApiServiceHelper", "Request failed", error)
                        Handler(Looper.getMainLooper()).post {
                            callback(null)
                        }
                    }
                )
            }
    }

    // Modify checkUsernameAndSendOTP to use publicPost
    fun checkUsernameAndSendOTP(username: String, callback: (Boolean) -> Unit) {
        val userData = mapOf("v_user_name" to username)

        Log.d("ApiServiceHelper", "Sending request to check username and send OTP: $userData")

        publicPost("/rpc/check_username_and_send_otp", userData) { response ->
            try {
                val success = response?.toBoolean() ?: false
                Log.d("ApiServiceHelper", "Response parsed. Success: $success")
                callback(success)
            } catch (e: Exception) {
                Log.e("ApiServiceHelper", "Error parsing response", e)
                callback(false)
            }
        }
    }

    fun getUserEmail(username: String, callback: (String?) -> Unit) {
        val userData = Gson().toJson(mapOf("v_user_name" to username))

        Fuel.post("$BASE_URL/rpc/get_user_email")
            .jsonBody(userData)
            .responseString { _, response, result ->
                result.fold(
                    success = { data ->
                        Handler(Looper.getMainLooper()).post {
                            // Remove quotes if present
                            callback(data.trim('"'))
                        }
                    },
                    failure = { error ->
                        Handler(Looper.getMainLooper()).post {
                            callback(null)
                        }
                    }
                )
            }
    }

    fun validateOTP(username: String, otp: String, callback: (String?) -> Unit) {
        val data = mapOf(
            "v_user_name" to username,
            "v_otp" to otp
        )

        Log.d("ApiServiceHelper", "Validating OTP for user: $username")

        publicPost("/rpc/validate_otp", data) { response ->
            Log.d("ApiServiceHelper", "Validate OTP response: $response")
            callback(response?.trim('"'))
        }
    }

    fun updateNewPassWord(username: String, resetToken: String, newPassword: String, callback: (Boolean) -> Unit){
        val data = mapOf(
            "v_user_name" to username,
            "v_reset_password_token" to resetToken,
            "v_new_password" to newPassword
        )
        publicPost("/rpc/update_new_password", data) { response ->
            try{
                val success = response?.toBoolean() ?: false
                callback(success)
            } catch (e: Exception){
                callback(false)
            }
        }
    }








    // Example GET request with Authorization
    fun get(endpoint: String, callback: (String?) -> Unit) {
        jwtToken?.let { token ->
            Fuel.get("$BASE_URL$endpoint")
                .header("Authorization", "Bearer $jwtToken")
                .responseString { _, response, result ->
                    if (response.statusCode == 200) {
                        // Switch to the main thread for UI update
                        Handler(Looper.getMainLooper()).post {
                            callback(result.get().trim('"'))
                        }
                    } else {
                        // Handle error response
                        Handler(Looper.getMainLooper()).post {
                            callback(null)
                        }
                    }
                }
        }
    }

    // Example POST request with Authorization
    fun post(endpoint: String, data: Any, callback: (String?) -> Unit) {
        jwtToken?.let { token ->
            val jsonData = GsonBuilder()
                .serializeNulls()
                .create()
                .toJson(data)
            Fuel.post("$BASE_URL$endpoint")
                .header("Authorization", "Bearer $token")
                .jsonBody(jsonData)
                .responseString { _, response, result ->
                    if (response.statusCode == 200) {
                        // Switch to the main thread for UI update
                        Handler(Looper.getMainLooper()).post {
                            callback(result.get().trim('"'))
                        }
                    } else {
                        // Handle error response
                        Handler(Looper.getMainLooper()).post {
                            callback(null)
                        }
                    }
                }
        }
    }
}