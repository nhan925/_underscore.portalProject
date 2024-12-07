package com.example.login_portal.ui.schedule


data class Lecturer(
    val LecturerId: String?,
    val FullName: String?,
    val Gender: String?,
    val PhoneNumber: String?,
    val Email: String?,
    val AcademicRank: String?,
    val AcademicDegree: String?,
    val FacultyName: String?
)

data class Course(
    val CourseId: String?,
    val CourseName: String?,
    val CourseUrl: String?,
    val ClassId: String?,
    val StartPeriod: Int?,
    val EndPeriod: Int?,
    val Day: String?,
    val Room: String?,
    val Lecturer: Lecturer?
)
