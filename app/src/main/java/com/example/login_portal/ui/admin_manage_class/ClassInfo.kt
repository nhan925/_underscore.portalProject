package com.example.login_portal.ui.admin_manage_class
import com.google.gson.annotations.SerializedName
import android.widget.EditText

data class Course(
    @SerializedName("course_id") val courseId: String,
    @SerializedName("course_name") val courseName: String,
    @SerializedName("credits") val credits: Int,
    @SerializedName("tuition_fee") val tuitionFee: Double,
    @SerializedName("course_outline_url") val courseOutlineUrl: String?,
    @SerializedName("prerequisite_course") val prerequisiteCourse: String?,
    @SerializedName("major_id") val majorId: String
)

data class Lecturer(
    @SerializedName("lecturer_id") val lecturerId: String,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("gender") val gender: String,
    @SerializedName("phone_number") val phoneNumber: String,
    @SerializedName("email") val email: String,
    @SerializedName("academic_rank") val academicRank: String?,
    @SerializedName("academic_degree") val academicDegree: String,
    @SerializedName("faculty_id") val facultyId: String
)

data class RegistrationPeriod(
    @SerializedName("registration_period_id") val registrationPeriodId: Int,
    @SerializedName("name") val name: String,
    @SerializedName("semester_id") val semesterId: Int,
    @SerializedName("start_date") val startDate: String,
    @SerializedName("end_date") val endDate: String,
    @SerializedName("max_credit_can_register") val maxCreditCanRegister: Int
)

data class ClassInfo(
    @SerializedName("class_id") val classId: String,
    @SerializedName("class_name") val className: String,
    @SerializedName("start_period") val startPeriod: Int,
    @SerializedName("end_period") val endPeriod: Int,
    @SerializedName("day_of_week") val dayOfWeek: String,
    @SerializedName("room") val room: String,
    @SerializedName("enrollment") val enrollment: Int,
    @SerializedName("max_enrollment") val maxEnrollment: Int,
    @SerializedName("registration_period_id") val registrationPeriodId: Int,
    @SerializedName("course_id") val courseId: String,
    @SerializedName("lecturer_id") val lecturerId: String
)

data class Student(
    @SerializedName("student_id") val studentId: String,
    @SerializedName("full_name") val fullName: String
)

data class StudentGrade(
    @SerializedName("student_id") val studentId: String,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("grade") val grade: Double?,
    @SerializedName("note") val note: String?,
    @SerializedName("feedback") val feedback: String?
)


data class StudentGradeInputs(
    val studentId: String,
    val gradeInput: EditText,
    val feedbackInput: EditText,
    val noteInput: EditText
)
