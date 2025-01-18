package com.example.login_portal.ui.admin_manage_enrollment_period

import com.google.gson.annotations.SerializedName


data class RegistrationPeriod(
    @SerializedName("registration_period_id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("semester_id") val semesterId: Int,
    @SerializedName("start_date") val startDate: String,
    @SerializedName("end_date") val endDate: String,
    @SerializedName("max_credit_can_register") val maxCreditCanRegister: Int
)

data class Semester(
    @SerializedName("semester_id") val id: Int,
    @SerializedName("semester_num") val semesterNum: Int,
    @SerializedName("year") val year: String,
    @SerializedName("start_date") val startDate: String,
    @SerializedName("end_date") val endDate: String,
    @SerializedName("payment_start_date") val paymentStartDate: String,
    @SerializedName("payment_end_date") val paymentEndDate: String
)

data class ManageRegistrationPeriodRequest(
    @SerializedName("p_operation") val operation: String,
    @SerializedName("p_registration_period_id") val registrationPeriodId: Int? = null,
    @SerializedName("p_name") val name: String? = null,
    @SerializedName("p_semester_id") val semesterId: Int? = null,
    @SerializedName("p_start_date") val startDate: String? = null,
    @SerializedName("p_end_date") val endDate: String? = null,
    @SerializedName("p_max_credit_can_register") val maxCreditCanRegister: Int? = null
)