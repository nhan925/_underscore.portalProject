package com.example.login_portal.ui.course

data class ClassesOfCourse (
    var Id: String = "",
    var Name: String = "",
    var Day: String = "",
    var Time: String = "",
    var Room: String = "",
    var MaxSlot: Int = 0,
    var RegisteredSlot: Int = 0
)