package com.example.login_portal.ui.scholarship

import android.content.Context
import com.example.login_portal.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Scholarship (
    var Id: Int = 0,
    var Name: String = "",
    var Description: String = "",
    var Amount: Double = 0.0,
    var Currency: String = "",
    var Slot: Int = 0,
    var Sponsor: String = "",
    var Criteria: String = "",
    var AnouncementDate: Date = Date(),
    var Deadline: Date = Date(),
    var Applied: Boolean = false,
    var Expired: Boolean = false,
    var ApplicationStatus: String = "",
    var SubmittedDate: Date = Date(),
    var Attachment: String = "",
    var RequiredDocuments: List<String> = listOf(),
) {
    val canApply: Boolean
        get() {
            return when {
                Expired -> false
                else -> !Applied
            }
        }

    val requiredDocumentsString: String
        get() = RequiredDocuments.joinToString("\n") { "    - $it" }

    fun statusWithItsColor(context: Context): Pair<String, Int> {
        return when {
            Expired -> Pair(context.getString(R.string.scholarship_expired),
                context.getColor(R.color.red))
            Applied -> Pair(context.getString(R.string.scholarship_applied),
                context.getColor(R.color.green))
            else -> Pair(context.getString(R.string.scholarship_not_applied),
                context.getColor(R.color.grey))
        }
    }

    fun getFormattedDate(date: Date): String {
        return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)
    }
}