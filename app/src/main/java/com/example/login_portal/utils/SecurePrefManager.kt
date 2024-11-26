package com.example.login_portal.utils

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.login_portal.UserData

class SecurePrefManager(context: Context)  {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveUserCredentials(email: String, password: String, isRemembered: Boolean) {
        sharedPreferences.edit()
            .putString("email", email)
            .putString("password", password)
            .putBoolean("isRemembered", isRemembered)
            .apply()

    }

    fun getUserData(): UserData? {
        val isRemembered = sharedPreferences.getBoolean("isRemembered", false)
        if (!isRemembered) {
            clearUserData()
            return null
        }
        val email = sharedPreferences.getString("email", null)
        val password = sharedPreferences.getString("password", null)
        return if (email != null && password != null) {
            UserData(email, password, isRemembered)
        } else null
    }

    fun clearUserData() {
        sharedPreferences.edit().clear().apply()
    }

}