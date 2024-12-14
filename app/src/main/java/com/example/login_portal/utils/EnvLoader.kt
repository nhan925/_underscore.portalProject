package com.example.login_portal.utils

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.IOException

object EnvLoader {

    private lateinit var appContext: Context

    // Call this in your Application class to set the context
    fun init(context: Context) {
        appContext = context.applicationContext
    }

    // Function to load environment variables from a .env file
    fun loadEnv(): Map<String, String> {
        val envMap = mutableMapOf<String, String>()

        try {
            val inputStream = appContext.assets.open("env")
            val reader = BufferedReader(InputStreamReader(inputStream))
            reader.forEachLine { line ->
                if (line.isNotBlank() && line.contains("=")) {
                    val (key, value) = line.split("=", limit = 2)
                    envMap[key.trim()] = value.trim()
                }
            }
            reader.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return envMap
    }
}

