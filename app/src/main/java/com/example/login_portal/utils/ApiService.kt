package com.example.login_portal.utils

import android.util.Log
import com.google.gson.Gson
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.DELETE
import retrofit2.http.Path

// Define the Retrofit interface
interface ApiService {

    // Login request with a Map payload
    @POST("/rpc/login")
    fun login(@Body loginData:Any): Call<String>

    // Login using Outlook with a Map payload
    @POST("/rpc/login_with_outlook")
    fun loginWithOutlook(@Body accessToken: Any): Call<String>

    // Example GET request with Authorization header
    @GET("{endpoint}")
    fun get(@Path("endpoint", encoded = true) endpoint: String, @Header("Authorization") authorization: String): Call<ResponseBody>

    // Example POST request with Authorization header
    @POST("{endpoint}")
    fun post(@Path("endpoint", encoded = true) endpoint: String, @Body data: Any, @Header("Authorization") authorization: String): Call<ResponseBody>

    // Example PUT request with Authorization header
    @PUT("{endpoint}")
    fun put(@Path("endpoint", encoded = true) endpoint: String, @Body data: Any, @Header("Authorization") authorization: String): Call<ResponseBody>

    // Example DELETE request with Authorization header
    @DELETE("{endpoint}")
    fun delete(@Path("endpoint", encoded = true) endpoint: String, @Header("Authorization") authorization: String): Call<ResponseBody>
}

object ApiServiceHelper {

    private const val BASE_URL = "http://10.0.2.2:3001" // Replace with your base URL
    var jwtToken: String? = null

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create()) // Use Gson converter
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)

    // Login using username and password (Map approach)
    fun login(username: String, password: String, callback: (Boolean) -> Unit) {
        val loginData = object {
            val username = username
            val password = password
        }

        apiService.login(loginData).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    jwtToken = response.body() // Assuming the JWT token is the response body
                    callback(true)
                } else {
                    callback(false)
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("ApiServiceHelper", "Login failed", t)
                callback(false)
            }
        })
    }

    // Login using Outlook access token (Map approach)
    fun loginWithOutlook(accessToken: String, callback: (Boolean) -> Unit) {
        val outlookData = object {
            val v_token = accessToken
        }

        apiService.loginWithOutlook(outlookData).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    jwtToken = response.body() // Store the JWT token
                    callback(true)
                } else {
                    callback(false)
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("ApiServiceHelper", "Outlook login failed", t)
                callback(false)
            }
        })
    }

    // Example of a GET request with Authorization
    fun get(endpoint: String, callback: (ResponseBody?) -> Unit) {
        jwtToken?.let { token ->
            apiService.get(endpoint, "Bearer $token").enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        callback(response.body())
                    } else {
                        callback(null)
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("ApiServiceHelper", "GET request failed", t)
                    callback(null)
                }
            })
        }
    }

    // Raw POST request with Authorization
    fun post(endpoint: String, data: Any, callback: (ResponseBody?) -> Unit) {
        jwtToken?.let { token ->
            val request = apiService.post(endpoint, data, "Bearer $token")
            request.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        callback(response.body()) // Return raw response body
                    } else {
                        callback(null)
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("ApiServiceHelper", "POST request failed", t)
                    callback(null)
                }
            })
        }
    }

    // Example of a PUT request with Authorization
    fun put(endpoint: String, data: Any, callback: (ResponseBody?) -> Unit) {
        jwtToken?.let { token ->
            apiService.put(endpoint, data, "Bearer $token").enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        callback(response.body())
                    } else {
                        callback(null)
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("ApiServiceHelper", "PUT request failed", t)
                    callback(null)
                }
            })
        }
    }

    // Example of a DELETE request with Authorization
    fun delete(endpoint: String, callback: (ResponseBody?) -> Unit) {
        jwtToken?.let { token ->
            apiService.delete(endpoint, "Bearer $token").enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        callback(response.body())
                    } else {
                        callback(null)
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e("ApiServiceHelper", "DELETE request failed", t)
                    callback(null)
                }
            })
        }
    }
}
