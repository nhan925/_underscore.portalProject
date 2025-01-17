package com.example.login_portal.ui.admin_manage_class

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

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

    companion object {
        // Validation message keys
        const val KEY_CLASS_ID_REQUIRED = "validation_class_id_required"
        const val KEY_CLASS_NAME_REQUIRED = "validation_class_name_required"
        const val KEY_REGISTRATION_REQUIRED = "validation_registration_period_required"
        const val KEY_COURSE_REQUIRED = "validation_course_required"
        const val KEY_LECTURER_REQUIRED = "validation_lecturer_required"
        const val KEY_DAY_REQUIRED = "validation_day_required"
        const val KEY_ROOM_REQUIRED = "validation_room_required"
        const val KEY_PERIOD_RANGE = "validation_period_range"
        const val KEY_INVALID_START_PERIOD = "validation_invalid_start_period"
        const val KEY_INVALID_END_PERIOD = "validation_invalid_end_period"
        const val KEY_ENROLLMENT_POSITIVE = "validation_enrollment_positive"
        const val KEY_INVALID_ENROLLMENT = "validation_invalid_enrollment"
        const val KEY_CREATE_FAILED = "msg_create_class_failed"
    }

    fun validateInputs(): Map<String, String> {
        val errors = mutableMapOf<String, String>()

        // Required field validation
        if (classId.value.isNullOrBlank()) errors["classId"] = KEY_CLASS_ID_REQUIRED
        if (className.value.isNullOrBlank()) errors["className"] = KEY_CLASS_NAME_REQUIRED
        if (selectedRegistrationPeriod == null) errors["registrationPeriod"] = KEY_REGISTRATION_REQUIRED
        if (selectedCourse == null) errors["course"] = KEY_COURSE_REQUIRED
        if (selectedLecturer == null) errors["lecturer"] = KEY_LECTURER_REQUIRED
        if (selectedDayOfWeek == null) errors["dayOfWeek"] = KEY_DAY_REQUIRED
        if (room.value.isNullOrBlank()) errors["room"] = KEY_ROOM_REQUIRED

        // Number validation
        startPeriod.value?.toIntOrNull()?.let {
            if (it < 1 || it > 10) errors["startPeriod"] = KEY_PERIOD_RANGE
        } ?: run { errors["startPeriod"] = KEY_INVALID_START_PERIOD }

        endPeriod.value?.toIntOrNull()?.let {
            if (it < 1 || it > 10) errors["endPeriod"] = KEY_PERIOD_RANGE
        } ?: run { errors["endPeriod"] = KEY_INVALID_END_PERIOD }

        maxEnrollment.value?.toIntOrNull()?.let {
            if (it < 1) errors["maxEnrollment"] = KEY_ENROLLMENT_POSITIVE
        } ?: run { errors["maxEnrollment"] = KEY_INVALID_ENROLLMENT }

        return errors
    }

    fun createClass(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val validationErrors = validateInputs()
        if (validationErrors.isNotEmpty()) {
            onError(validationErrors.values.filterNotNull().joinToString("\n"))
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
                onError("Failed to create class")
            }
        }
    }
}