package com.example.login_portal.ui.course

data class ChooseCourseInformations (
    var MaxCredits: Int = 0,
    var Major: String = "",
    var RegisteredCoursesCount: Int = 0,
    var RegisteredCredits: Int = 0,
    var UnregisteredCourses: List<Course> = listOf(),
    var RegisteredCourses: List<Course> = listOf(),
    var StudiedCourses: List<Course> = listOf()
)