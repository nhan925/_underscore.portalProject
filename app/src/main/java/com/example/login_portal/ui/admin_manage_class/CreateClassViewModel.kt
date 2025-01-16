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

    fun validateInputs(): Map<String, String?> {
        val errors = mutableMapOf<String, String?>()

        // Required field validation
        if (classId.value.isNullOrBlank()) errors["classId"] = "Class ID is required"
        if (className.value.isNullOrBlank()) errors["className"] = "Class Name is required"
        if (selectedRegistrationPeriod == null) errors["registrationPeriod"] = "Registration Period is required"
        if (selectedCourse == null) errors["course"] = "Course is required"
        if (selectedLecturer == null) errors["lecturer"] = "Lecturer is required"
        if (selectedDayOfWeek == null) errors["dayOfWeek"] = "Day of Week is required"
        if (room.value.isNullOrBlank()) errors["room"] = "Room is required"

        // Number validation
        startPeriod.value?.toIntOrNull()?.let {
            if (it < 1 || it > 10) errors["startPeriod"] = "Start Period must be between 1 and 10"
        } ?: run { errors["startPeriod"] = "Invalid Start Period" }

        endPeriod.value?.toIntOrNull()?.let {
            if (it < 1 || it > 10) errors["endPeriod"] = "End Period must be between 1 and 10"
        } ?: run { errors["endPeriod"] = "Invalid End Period" }

        maxEnrollment.value?.toIntOrNull()?.let {
            if (it < 1) errors["maxEnrollment"] = "Maximum Enrollment must be positive"
        } ?: run { errors["maxEnrollment"] = "Invalid Maximum Enrollment" }

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