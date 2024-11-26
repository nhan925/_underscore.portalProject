package com.example.login_portal.utils

object Validator {
    private val EMAIL_PATTERN = "^([a-zA-Z0-9._%-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6})*$".toRegex()
    private val PASSWORD_PATTERN = "(?=(.*[0-9]))((?=.*[A-Za-z0-9])(?=.*[A-Z])(?=.*[a-z]))^.{8,}$".toRegex()

    fun validateEmail(email: String): ValidationResult {
        return when {
            email.isEmpty() -> ValidationResult(false, "Email không được để trống")
            !email.matches(EMAIL_PATTERN) -> ValidationResult(false, "Email không đúng định dạng")
            else -> ValidationResult(true)
        }
    }

    fun validatePassword(password: String): ValidationResult {
        return when {
            password.isEmpty() -> ValidationResult(false, "Mật khẩu không được để trống")
            !password.matches(PASSWORD_PATTERN) ->
                ValidationResult(false, "Mật khẩu phải có ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường và số")
            else -> ValidationResult(true)
        }
    }
}

data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null
)