package com.example.login_portal.ui.admin_manage_class

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ClassDetailsViewModel : ViewModel() {
    // Error states using sealed class
    sealed class ErrorState {
        object None : ErrorState()
        data class Error(val message: String) : ErrorState()
    }

    // Loading state
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    // Error state
    private val _error = MutableLiveData<ErrorState>(ErrorState.None)
    val error: LiveData<ErrorState> = _error

    // Class information
    private val _classInfo = MutableLiveData<ClassInfo>()
    val classInfo: LiveData<ClassInfo> = _classInfo

    // Class details form fields
    val classId = MutableLiveData<String>()
    val className = MutableLiveData<String>()

    val startPeriod = MutableLiveData<String>()
    val endPeriod = MutableLiveData<String>()
    val room = MutableLiveData<String>()
    val maxEnrollment = MutableLiveData<String>()
    val enrollment = MutableLiveData(0)

    // Dropdown data
    private val _registrationPeriods = MutableLiveData<List<RegistrationPeriod>>(emptyList())
    val registrationPeriods: LiveData<List<RegistrationPeriod>> = _registrationPeriods

    private val _courses = MutableLiveData<List<Course>>(emptyList())
    val courses: LiveData<List<Course>> = _courses

    private val _lecturers = MutableLiveData<List<Lecturer>>(emptyList())
    val lecturers: LiveData<List<Lecturer>> = _lecturers

    // Selected dropdown items
    private var selectedRegistrationPeriod: RegistrationPeriod? = null
    private var selectedCourse: Course? = null
    private var selectedLecturer: Lecturer? = null
    private var selectedDayOfWeek: String? = null

    fun loadClassInfo(classId: String) {
        _isLoading.value = true
        ClassDAO.getClasses { classList ->
            _isLoading.value = false
            classList?.find { it.classId == classId }?.let { foundClass ->
                _classInfo.value = foundClass
                updateFormFields(foundClass)
                loadDropdownData()
            } ?: run {
                _error.value = ErrorState.Error("Class not found")
            }
        }
    }

    private fun updateFormFields(classInfo: ClassInfo) {
        classId.value = classInfo.classId
        className.value = classInfo.className

        startPeriod.value = classInfo.startPeriod.toString()
        endPeriod.value = classInfo.endPeriod.toString()
        room.value = classInfo.room
        enrollment.value = classInfo.enrollment
        maxEnrollment.value = classInfo.maxEnrollment.toString()
    }

    private fun loadDropdownData() {
        // Load registration periods
        ClassDAO.getRegistrationPeriods { periods ->
            _registrationPeriods.value = periods ?: emptyList()
            // Pre-select current registration period
            classInfo.value?.let { currentClass ->
                selectedRegistrationPeriod = periods?.find {
                    it.registrationPeriodId == currentClass.registrationPeriodId
                }
            }
        }

        // Load courses
        ClassDAO.getCourses { courseList ->
            _courses.value = courseList ?: emptyList()
            // Pre-select current course
            classInfo.value?.let { currentClass ->
                selectedCourse = courseList?.find {
                    it.courseId == currentClass.courseId
                }
            }
        }

        // Load lecturers
        ClassDAO.getLecturers { lecturerList ->
            _lecturers.value = lecturerList ?: emptyList()
            // Pre-select current lecturer
            classInfo.value?.let { currentClass ->
                selectedLecturer = lecturerList?.find {
                    it.lecturerId == currentClass.lecturerId
                }
            }
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

    fun updateClass(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val currentClassId = classId.value ?: run {
            onError("Class ID is required")
            return
        }

        val updatedClass = ClassInfo(
            classId = currentClassId,
            className = className.value ?: "",
            startPeriod = startPeriod.value?.toIntOrNull() ?: 0,
            endPeriod = endPeriod.value?.toIntOrNull() ?: 0,
            dayOfWeek = selectedDayOfWeek ?: "",
            room = room.value ?: "",
            enrollment = enrollment.value ?: 0,
            maxEnrollment = maxEnrollment.value?.toIntOrNull() ?: 0,
            registrationPeriodId = selectedRegistrationPeriod?.registrationPeriodId ?: 0,
            courseId = selectedCourse?.courseId ?: "",
            lecturerId = selectedLecturer?.lecturerId ?: ""
        )

        _isLoading.value = true
        ClassDAO.manageClass("update", updatedClass) { success ->
            _isLoading.value = false
            if (success) {
                onSuccess()
            } else {
                onError("Failed to update class")
            }
        }
    }

    fun deleteClass(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val currentClass = classInfo.value ?: run {
            onError("No class selected")
            return
        }

        _isLoading.value = true
        ClassDAO.manageClass("delete", currentClass) { success ->
            _isLoading.value = false
            if (success) {
                onSuccess()
            } else {
                onError("Failed to delete class")
            }
        }
    }

    fun clearError() {
        _error.value = ErrorState.None
    }

    override fun onCleared() {
        super.onCleared()
        // Clean up any resources if needed
    }
}