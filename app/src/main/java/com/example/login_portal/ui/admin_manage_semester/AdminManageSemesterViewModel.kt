package com.example.login_portal.ui.admin_manage_semester

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AdminManageSemesterViewModel : ViewModel() {
    private val _semesters = MutableLiveData<List<Semester>>(emptyList())
    val semesters: LiveData<List<Semester>> = _semesters

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData("")
    val error: LiveData<String> = _error

    init {
        fetchSemesters()
    }

    // Hàm sắp xếp danh sách kỳ học
    private fun sortSemesters(semesters: List<Semester>): List<Semester> {
        return semesters.sortedWith(
            compareBy<Semester> { it.year.split(" - ")[0].toInt() }
                .thenBy { it.semesterNum }
        )
    }

    fun fetchSemesters() {
        _isLoading.value = true
        SemesterDAO.getSemesters { result ->
            val sortedResult = result?.let { sortSemesters(it) } ?: emptyList()
            _semesters.postValue(sortedResult)
            _isLoading.postValue(false)
            if (result == null) {
                _error.postValue("Failed to load semesters")
            }
        }
    }

    fun createSemester(request: CreateSemesterRequest, callback: (Boolean) -> Unit) {
        _isLoading.value = true
        SemesterDAO.createSemester(request) { success ->
            _isLoading.postValue(false)
            if (success) {
                fetchSemesters() // Sẽ tự động sắp xếp khi fetch lại
                _error.postValue("")
            } else {
                _error.postValue("Failed to create semester")
            }
            callback(success)
        }
    }

    fun updateSemester(request: ManageSemesterRequest, callback: (Boolean) -> Unit) {
        _isLoading.value = true
        SemesterDAO.updateSemester(request) { success ->
            _isLoading.postValue(false)
            if (success) {
                fetchSemesters() // Sẽ tự động sắp xếp khi fetch lại
                _error.postValue("")
            } else {
                _error.postValue("Failed to update semester")
            }
            callback(success)
        }
    }

    fun deleteSemester(semesterId: Int, callback: (Boolean) -> Unit) {
        _isLoading.value = true
        SemesterDAO.deleteSemester(semesterId) { success ->
            _isLoading.postValue(false)
            if (success) {
                fetchSemesters() // Sẽ tự động sắp xếp khi fetch lại
                _error.postValue("")
            } else {
                _error.postValue("Failed to delete semester")
            }
            callback(success)
        }
    }

    fun clearError() {
        _error.value = ""
    }
}