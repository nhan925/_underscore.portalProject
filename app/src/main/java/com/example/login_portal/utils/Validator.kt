package com.example.login_portal.utils

import com.example.login_portal.R

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

    private const val MIN_LENGTH = 8
    private const val PATTERN_LOWERCASE = ".*[a-z].*"
    private const val PATTERN_UPPERCASE = ".*[A-Z].*"
    private const val PATTERN_DIGIT = ".*\\d.*"
    private const val PATTERN_SPECIAL = ".*[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*"

    fun validateNewPassword(password: String): PasswordStrength {
        if (password.length < MIN_LENGTH) {
            return PasswordStrength.TOO_SHORT
        }

        var strength = 0
        if (password.matches(Regex(PATTERN_LOWERCASE))) strength++
        if (password.matches(Regex(PATTERN_UPPERCASE))) strength++
        if (password.matches(Regex(PATTERN_DIGIT))) strength++
        if (password.matches(Regex(PATTERN_SPECIAL))) strength++

        return when (strength) {
            0, 1 -> PasswordStrength.WEAK
            2 -> PasswordStrength.MEDIUM
            3 -> PasswordStrength.STRONG
            4 -> PasswordStrength.VERY_STRONG
            else -> PasswordStrength.WEAK
        }
    }
}

enum class PasswordStrength(val message: Int) {
    TOO_SHORT(R.string.error_password_too_short),
    WEAK(R.string.error_password_weak),
    MEDIUM(R.string.error_password_medium),
    STRONG(R.string.error_password_strong),
    VERY_STRONG(R.string.error_password_very_strong)
}

data class ValidationResult(
    val isValid: Boolean,
    val errorMessage: String? = null
)