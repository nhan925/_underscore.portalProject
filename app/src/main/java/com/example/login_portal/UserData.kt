package com.example.login_portal

data class UserData(
    val email: String,
    val password: String,
    val isRemembered: Boolean = false
)