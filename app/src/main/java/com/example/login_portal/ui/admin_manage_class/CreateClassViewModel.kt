package com.example.login_portal.ui.admin_manage_class

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.login_portal.R

class CreateClassViewModel : ViewModel() {
    // Input fields with two-way binding
    val classId = MutableLiveData<String>()
    val className = MutableLiveData<String>()
    val room = MutableLiveData<String>()
    val startPeriod = MutableLiveData<String>()
    val endPeriod = MutableLiveData<String>()
    val maxEnrollment = MutableLiveData<String>()

    // Selected values for dropdowns
    private var selectedRegistrationPeriod: RegistrationPeriod? = null
    private var selectedCourse: Course? = null
    private var selectedLecturer: Lecturer? = null
    private var selectedDayOfWeek: String? = null

    // Dropdown data
    private val _registrationPeriods = MutableLiveData<List<RegistrationPeriod>>(emptyList())
    val registrationPeriods: LiveData<List<RegistrationPeriod>> = _registrationPeriods

    private val _courses = MutableLiveData<List<Course>>(emptyList())
    val courses: LiveData<List<Course>> = _courses

    private val _lecturers = MutableLiveData<List<Lecturer>>(emptyList())
    val lecturers: LiveData<List<Lecturer>> = _lecturers

    // Loading and error states
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    init {
        loadDropdownData()
    }

    private fun loadDropdownData() {
        _isLoading.value = true

        // Load registration periods
        ClassDAO.getRegistrationPeriods { periods ->
            _registrationPeriods.value = periods ?: emptyList()
        }

        // Load courses
        ClassDAO.getCourses { courseList ->
            _courses.value = courseList ?: emptyList()
        }

        // Load lecturers
        ClassDAO.getLecturers { lecturerList ->
            _lecturers.value = lecturerList ?: emptyList()
            _isLoading.value = false
        }
    }

    // Setters for dropdown selections
    fun setRegistrationPeriod(period: RegistrationPeriod) {
        selectedRegistrationPeriod = period
    }

    fun setCourse(course: Course) {
        selectedCourse = course
    }

    fun setLecturer(lecturer: Lecturer) {
        selectedLecturer = lecturer
    }

    fun setDayOfWeek(day: String) {
        selectedDayOfWeek = day
    }

    fun validateInputs(context: Context): Map<String, String> {
        val errors = mutableMapOf<String, String>()

        // Required field validation
        if (classId.value.isNullOrBlank()) errors["classId"] = context.getString(R.string.validation_class_id_required)
        if (className.value.isNullOrBlank()) errors["className"] = context.getString(R.string.validation_class_name_required)
        if (selectedRegistrationPeriod == null) errors["registrationPeriod"] = context.getString(R.string.validation_registration_period_required)
        if (selectedCourse == null) errors["course"] = context.getString(R.string.validation_course_required)
        if (selectedLecturer == null) errors["lecturer"] = context.getString(R.string.validation_lecturer_required)
        if (selectedDayOfWeek == null) errors["dayOfWeek"] = context.getString(R.string.validation_day_required)
        if (room.value.isNullOrBlank()) errors["room"] = context.getString(R.string.validation_room_required)

        // Number validation
        startPeriod.value?.toIntOrNull()?.let {
            if (it < 1 || it > 10) errors["startPeriod"] = context.getString(R.string.validation_period_range)
        } ?: run { errors["startPeriod"] = context.getString(R.string.validation_invalid_start_period) }

        endPeriod.value?.toIntOrNull()?.let {
            if (it < 1 || it > 10) errors["endPeriod"] = context.getString(R.string.validation_period_range)
        } ?: run { errors["endPeriod"] = context.getString(R.string.validation_invalid_end_period) }

        maxEnrollment.value?.toIntOrNull()?.let {
            if (it < 1) errors["maxEnrollment"] = context.getString(R.string.validation_enrollment_positive)
        } ?: run { errors["maxEnrollment"] = context.getString(R.string.validation_invalid_enrollment) }

        return errors
    }

    fun createClass(context: Context, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val validationErrors = validateInputs(context)
        if (validationErrors.isNotEmpty()) {
            // Combine validation errors into a single localized message
            val errorMessage = validationErrors.values.joinToString("\n")
            onError(errorMessage)
            return
        }

        val newClass = ClassInfo(
            classId = classId.value!!,
            className = className.value!!,
            startPeriod = startPeriod.value!!.toInt(),
            endPeriod = endPeriod.value!!.toInt(),
            dayOfWeek = selectedDayOfWeek!!,
            room = room.value!!,
            enrollment = 0,
            maxEnrollment = maxEnrollment.value!!.toInt(),
            registrationPeriodId = selectedRegistrationPeriod!!.registrationPeriodId,
            courseId = selectedCourse!!.courseId,
            lecturerId = selectedLecturer!!.lecturerId
        )

        _isLoading.value = true
        ClassDAO.manageClass("add", newClass) { success ->
            _isLoading.value = false
            if (success) {
                onSuccess()
            } else {
                // Use a localized error message if the class creation fails
                onError(context.getString(R.string.create_class_failed))
            }
        }
    }

}
