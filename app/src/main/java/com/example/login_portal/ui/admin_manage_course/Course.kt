package com.example.login_portal.ui.admin_manage_course

import com.google.gson.annotations.SerializedName


data class Course(
    @SerializedName("course_id") val id: String,
    @SerializedName("course_name") val name: String,
    @SerializedName("credits") val credits: Int,
    @SerializedName("tuition_fee") val tuitionFee: Int,
    @SerializedName("course_outline_url") val outlineUrl: String,
    @SerializedName("prerequisite_course") val prerequisiteCourse: String?,
    @SerializedName("major_id") val majorId: String?
)

data class Major(
    @SerializedName("major_id") val id: String,
    @SerializedName("major_name") val name: String,
    @SerializedName("credits") val credits: Int,
    @SerializedName("faculty_id") val facultyId: String
)

data class ManageCourseRequest(
    @SerializedName("p_operation") val operation: String,
    @SerializedName("p_course_id") val courseId: String,
    @SerializedName("p_course_name") val courseName: String? = null,
    @SerializedName("p_credits") val credits: Int? = null,
    @SerializedName("p_tuition_fee") val tuitionFee: Int? = null,
    @SerializedName("p_course_outline_url") val outlineUrl: String? = null,
    @SerializedName("p_major_id") val majorId: String? = null
)