package com.example.login_portal.utils

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.google.gson.Gson

object ApiServiceHelper {
    const val BASE_URL = "http://10.0.2.2:3001"
    var jwtToken: String? = null

    // Login using username and password
    fun login(username: String, password: String, callback: (Boolean) -> Unit) {
        val loginData = Gson().toJson(mapOf("username" to username, "password" to password))

        Fuel.post("$BASE_URL/rpc/login")
            .jsonBody(loginData)
            .responseString { _, response, result ->
                if (response.statusCode == 200) {
                    jwtToken = result.get().trim('"')
                    callback(true)
                } else {
                    callback(false)
                }
            }
    }

    // Login using Outlook access token
    fun loginWithOutlook(accessToken: String, callback: (Boolean) -> Unit) {
        val outlookData = Gson().toJson(mapOf("v_token" to accessToken))

        Fuel.post("$BASE_URL/rpc/login_with_outlook")
            .jsonBody(outlookData)
            .responseString { _, response, result ->
                if (response.statusCode == 200) {
                    jwtToken = result.get().trim('"')
                    callback(true)
                } else {
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
            val jsonData = Gson().toJson(data)
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