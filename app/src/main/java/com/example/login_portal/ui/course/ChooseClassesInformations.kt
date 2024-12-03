package com.example.login_portal.ui.course

data class ChooseClassesInformations (
    var CourseId: String = "",
    var CourseName: String = "",
    var Status: String = "",
    var Classes: List<ClassesOfCourse> = listOf(),
    var RegisteredClassId: String = "",
    var PeriodId: Int = 0
) {
    val NumberOfClasses: Int
        get() = Classes.size
}