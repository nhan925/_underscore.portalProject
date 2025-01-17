package com.example.login_portal.ui.admin_manage_semester
import com.google.gson.annotations.SerializedName

data class Semester(
    @SerializedName("semester_id") val id: Int,
    @SerializedName("semester_num") val semesterNum: Int,
    @SerializedName("year") val year: String,
    @SerializedName("start_date") val startDate: String,
    @SerializedName("end_date") val endDate: String,
    @SerializedName("payment_start_date") val paymentStartDate: String,
    @SerializedName("payment_end_date") val paymentEndDate: String
)

data class CreateSemesterRequest(
    @SerializedName("p_semester_num") val semesterNum: Int,
    @SerializedName("p_year") val year: String,
    @SerializedName("p_start_date") val startDate: String,
    @SerializedName("p_end_date") val endDate: String,
    @SerializedName("p_payment_start_date") val paymentStartDate: String,
    @SerializedName("p_payment_end_date") val paymentEndDate: String
)

data class ManageSemesterRequest(
    @SerializedName("p_operation") val operation: String,
    @SerializedName("p_semester_id") val semesterId: Int,
    @SerializedName("p_semester_num") val semesterNum: Int? = null,
    @SerializedName("p_year") val year: String? = null,
    @SerializedName("p_start_date") val startDate: String? = null,
    @SerializedName("p_end_date") val endDate: String? = null,
    @SerializedName("p_payment_start_date") val paymentStartDate: String? = null,
    @SerializedName("p_payment_end_date") val paymentEndDate: String? = null
)