package com.example.login_portal.utils

import java.text.SimpleDateFormat
import java.util.Locale

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