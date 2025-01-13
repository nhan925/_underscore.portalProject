package com.example.login_portal.ui.admin_manage_student

import com.google.gson.annotations.SerializedName

data class StudentInfo(
    @SerializedName("student_id") val studentId: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("identity_card_number") val identityCardNumber: String?,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("address") val address: String?,
    @SerializedName("gender") val gender: String?,
    @SerializedName("date_of_birth") val dateOfBirth: String?,
    @SerializedName("phone_number") val phoneNumber: String?,
    @SerializedName("academic_program") val academicProgram: String?,
    @SerializedName("personal_email") val personalEmail: String?,
    @SerializedName("school_email") val schoolEmail: String?,
    @SerializedName("year_of_admission") val yearOfAdmission: Int?,
    @SerializedName("nationality") val nationality: String?,
    @SerializedName("ethnicity") val ethnicity: String?,
    @SerializedName("major_id") val majorId: String?,
    @SerializedName("new_student_id") val newStudentId: String?
)
