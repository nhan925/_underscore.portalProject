package com.example.login_portal.ui.admin_manage_class
import android.util.Log
import com.example.login_portal.utils.ApiServiceHelper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.gson.JsonSyntaxException


object ClassDAO {
    // Class Management
        // Fetch classes from API
        fun getClasses(callback: (List<ClassInfo>?) -> Unit) {
            ApiServiceHelper.get("/Class") { response ->
                if (response != null) {
                    try {
                        // Parse the JSON response
                        val result: List<ClassInfo> = Gson().fromJson(response, object : TypeToken<List<ClassInfo>>() {}.type)

                        // Validate and filter the response to avoid null values
                        val validatedClasses = result.filter { validateClassInfo(it) }

                        callback(validatedClasses)
                    } catch (e: JsonSyntaxException) {
                        Log.e("ClassDAO", "Error parsing classes: ${e.message}")
                        callback(null)
                    } catch (e: Exception) {
                        Log.e("ClassDAO", "Unexpected error: ${e.message}")
                        callback(null)
                    }
                } else {
                    Log.e("ClassDAO", "API response is null")
                    callback(null)
                }
            }
        }

        // Validate the ClassInfo object
        private fun validateClassInfo(classInfo: ClassInfo): Boolean {
            return try {
                require(classInfo.classId.isNotBlank()) { "Invalid classId" }
                require(classInfo.className.isNotBlank()) { "Invalid className" }
                require(classInfo.startPeriod in 1..10) { "Invalid startPeriod" }
                require(classInfo.endPeriod in 1..10) { "Invalid endPeriod" }
                require(classInfo.dayOfWeek.isNotBlank()) { "Invalid dayOfWeek" }
                require(classInfo.room.isNotBlank()) { "Invalid room" }
                require(classInfo.enrollment >= 0) { "Invalid enrollment" }
                require(classInfo.maxEnrollment > 0) { "Invalid maxEnrollment" }
                require(classInfo.registrationPeriodId > 0) { "Invalid registrationPeriodId" }
                require(classInfo.courseId.isNotBlank()) { "Invalid courseId" }
                require(classInfo.lecturerId.isNotBlank()) { "Invalid lecturerId" }
                true
            } catch (e: IllegalArgumentException) {
                Log.e("ClassDAO", "Validation error: ${e.message}")
                false
            }
        }



    // Course Management
    fun getCourses(callback: (List<Course>?) -> Unit) {
        ApiServiceHelper.get("/Course") { response ->
            handleResponse<List<Course>>(response, callback, "Error parsing courses")
        }
    }

    // Lecturer Management
    fun getLecturers(callback: (List<Lecturer>?) -> Unit) {
        ApiServiceHelper.get("/Lecturer") { response ->
            handleResponse<List<Lecturer>>(response, callback, "Error parsing lecturers")
        }
    }

    // Registration Period Management
    fun getRegistrationPeriods(callback: (List<RegistrationPeriod>?) -> Unit) {
        ApiServiceHelper.get("/RegistrationPeriod") { response ->
            handleResponse<List<RegistrationPeriod>>(response, callback, "Error parsing registration periods")
        }
    }

    fun manageClass(
        operation: String,
        classInfo: ClassInfo,
        callback: (Boolean) -> Unit
    ) {
        val request = mapOf(
            "p_operation" to operation,
            "p_class_id" to classInfo.classId,
            "p_class_name" to classInfo.className,
            "p_start_period" to classInfo.startPeriod,
            "p_end_period" to classInfo.endPeriod,
            "p_day_of_week" to classInfo.dayOfWeek,
            "p_room" to classInfo.room,
            "p_enrollment" to classInfo.enrollment,
            "p_max_enrollment" to classInfo.maxEnrollment,
            "p_registration_period_id" to classInfo.registrationPeriodId,
            "p_course_id" to classInfo.courseId,
            "p_lecturer_id" to classInfo.lecturerId
        )

        ApiServiceHelper.post("/rpc/manage_class", request) { response ->
            callback(response != null)
        }
    }

    // Student Management in Class
    fun getStudentsInClass(classId: String, callback: (List<Student>?) -> Unit) {
        val request = mapOf(
            "p_operation" to "list",
            "p_class_id" to classId
        )

        ApiServiceHelper.post("/rpc/manage_students_in_class", request) { response ->
            handleResponse<List<Student>>(response, callback, "Error parsing students")
        }
    }

    fun getStudentGrades(classId: String, callback: (List<StudentGrade>?) -> Unit) {
        val request = mapOf(
            "p_class_id" to classId
        )

        ApiServiceHelper.post("/rpc/get_student_grades", request) { response ->
            handleResponse<List<StudentGrade>>(response, callback, "Error parsing student grades")
        }
    }

    fun manageStudentInClass(
        operation: String,
        classId: String,
        studentId: String,
        callback: (Boolean) -> Unit
    ) {
        val request = mapOf(
            "p_operation" to operation,
            "p_class_id" to classId,
            "p_student_id" to studentId
        )

        ApiServiceHelper.post("/rpc/manage_students_in_class", request) { response ->
            callback(response != null)
        }
    }

    // Grade Management
    fun updateGrades(
        classId: String,
        studentId: String,
        grade: Double,
        note: String?,
        feedback: String?,
        callback: (Boolean) -> Unit
    ) {
        val request = mapOf(
            "p_class_id" to classId,
            "p_student_id" to studentId,
            "p_course_grade" to grade,
            "p_note" to note,
            "p_feedback" to feedback
        )

        ApiServiceHelper.post("/rpc/manage_grades", request) { response ->
            callback(response != null)
        }
    }

    // Helper function to handle JSON parsing
    private inline fun <reified T> handleResponse(
        response: String?,
        callback: (T?) -> Unit,
        errorMessage: String
    ) {
        if (response != null) {
            try {
                val result = Gson().fromJson<T>(
                    response,
                    object : TypeToken<T>() {}.type
                )
                callback(result)
            } catch (e: Exception) {
                Log.e("ClassDAO", "$errorMessage: ${e.message}")
                callback(null)
            }
        } else {
            callback(null)
        }
    }
}