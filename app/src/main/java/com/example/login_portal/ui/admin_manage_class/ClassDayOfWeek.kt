package com.example.login_portal.ui.admin_manage_class

object DayOfWeekUtil {
    // For display
    val daysOfWeekEn = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
    val daysOfWeekVi = listOf("Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7")

    // Database format mapping
    private val dbValues = listOf("Thứ 2", "Thứ 3", "Thứ 4", "Thứ 5", "Thứ 6", "Thứ 7")

    fun getDisplayDays(isEnglish: Boolean): List<String> {
        return if (isEnglish) daysOfWeekEn else daysOfWeekVi
    }

    fun getDbValue(index: Int): String {
        return dbValues.getOrElse(index) { dbValues.first() }
    }

    fun getDisplayIndex(dbValue: String): Int {
        return dbValues.indexOf(dbValue).takeIf { it >= 0 } ?: 0
    }
}