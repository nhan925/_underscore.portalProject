package com.example.login_portal.utils

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

/**
 * Formats a given datetime string into a more readable format.
 *
 * @param dateTimeString The input datetime string in "yyyy-MM-dd HH:mm" format.
 * @param locale The locale for formatting (defaults to system locale).
 * @return The formatted datetime string in "hh:mm dd/MM/yyyy" format.
 */
fun formatDateTime(dateTimeString: String?, locale: Locale = Locale.getDefault()): String {
    return try {
        if (dateTimeString.isNullOrEmpty()) {
            return "Invalid Date"
        }

        val inputFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm", locale)
        val outputFormatter = SimpleDateFormat("hh:mm dd/MM/yyyy", locale)

        val date = inputFormatter.parse(dateTimeString)
        outputFormatter.format(date)
    } catch (e: Exception) {
        "Invalid Date"
    }
}

fun formatDateTimeVer2(dateTimeString: String?, locale: Locale = Locale.getDefault()): String {
    return try {
        if (dateTimeString.isNullOrEmpty()) {
            return "Invalid Date"
        }

        val inputFormatters = listOf(
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX", locale).apply { timeZone = TimeZone.getTimeZone("UTC") },
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", locale).apply { timeZone = TimeZone.getTimeZone("UTC") }
        )

        val outputFormatter = SimpleDateFormat("hh:mm dd/MM/yyyy", locale)

        val date = inputFormatters.asSequence()
            .mapNotNull { formatter ->
                try {
                    formatter.parse(dateTimeString)
                } catch (e: Exception) {
                    null
                }
            }
            .firstOrNull()

        // If successful, format the date
        date?.let { outputFormatter.format(it) } ?: "Invalid Date"
    } catch (e: Exception) {
        "Invalid Date"
    }
}