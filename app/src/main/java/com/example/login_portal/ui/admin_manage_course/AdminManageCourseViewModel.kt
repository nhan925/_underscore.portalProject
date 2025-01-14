package com.example.login_portal.ui.admin_manage_course

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CourseViewModel : ViewModel() {
    private val _courses = MutableLiveData<List<Course>>(emptyList())
    val courses: LiveData<List<Course>> = _courses

    private val _majors = MutableLiveData<List<Major>>(emptyList())
    val majors: LiveData<List<Major>> = _majors

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData("")
    val error: LiveData<String> = _error

    init {
        fetchCourses()
        fetchMajors()
    }

    private fun sortCourses(courses: List<Course>): List<Course> {
        return courses.sortedBy { it.id }
    }

    fun fetchCourses() {
        _isLoading.value = true
        CourseDAO.getCourses { result ->
            _courses.postValue(result?.let { sortCourses(it) } ?: emptyList())
            _isLoading.postValue(false)
            if (result == null) _error.postValue("Failed to load courses")
        }
    }

    fun fetchMajors() {
        CourseDAO.getMajors { result ->
            _majors.postValue(result ?: emptyList())
            if (result == null) _error.postValue("Failed to load majors")
        }
    }

    fun manageCourse(request: ManageCourseRequest, callback: (Boolean) -> Unit) {
        _isLoading.value = true
        CourseDAO.manageCourse(request) { success ->
            _isLoading.postValue(false)
            if (success) {
                fetchCourses()
                _error.postValue("")
            } else {
                _error.postValue("Failed to ${request.operation} course")
            }
            callback(success)
        }
    }
}