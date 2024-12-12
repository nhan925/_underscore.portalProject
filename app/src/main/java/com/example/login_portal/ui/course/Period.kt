package com.example.login_portal.ui.course

import android.content.Context
import com.example.login_portal.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Period (
    var Id: Int = 0,
    var Title: String = "",
    var OpenDate: Date = Date(),
    var CloseDate: Date = Date()
) {
    val canRegister: Boolean
        get() = Date() in OpenDate..CloseDate

    fun statusWithItsColor(context: Context): Pair<String, Int> {
        return when {
            OpenDate > Date() -> Pair(context.getString(R.string.course_register_status_not_opened_yet),
                context.getColor(R.color.orange))
            CloseDate < Date() -> Pair(context.getString(R.string.course_register_status_closed),
                context.getColor(R.color.red))
            else -> Pair(context.getString(R.string.course_register_status_opening),
                context.getColor(R.color.green))
        }
    }

    fun getFormattedDate(date: Date): String {
        return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)
    }
}