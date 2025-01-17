package com.example.login_portal.ui.admin_manage_enrollment_period

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RegistrationPeriodViewModel : ViewModel() {
    private val _registrationPeriods = MutableLiveData<List<RegistrationPeriod>>(emptyList())
    val registrationPeriods: LiveData<List<RegistrationPeriod>> = _registrationPeriods

    private val _semesters = MutableLiveData<List<Semester>>(emptyList())
    val semesters: LiveData<List<Semester>> = _semesters

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData("")
    val error: LiveData<String> = _error

    init {
        fetchRegistrationPeriods()
        fetchSemesters()
    }

    private fun sortRegistrationPeriods(periods: List<RegistrationPeriod>): List<RegistrationPeriod> {
        return periods.sortedByDescending { it.id }
    }

    fun fetchRegistrationPeriods() {
        _isLoading.value = true
        RegistrationPeriodDAO.getRegistrationPeriods { result ->
            _registrationPeriods.postValue(result?.let { sortRegistrationPeriods(it) } ?: emptyList())
            _isLoading.postValue(false)
            if (result == null) _error.postValue("Failed to load registration periods")
        }
    }

    fun fetchSemesters() {
        RegistrationPeriodDAO.getSemesters { result ->
            _semesters.postValue(result ?: emptyList())
            if (result == null) _error.postValue("Failed to load semesters")
        }
    }

    fun manageRegistrationPeriod(request: ManageRegistrationPeriodRequest, callback: (Boolean) -> Unit) {
        _isLoading.value = true
        RegistrationPeriodDAO.manageRegistrationPeriod(request) { success ->
            _isLoading.postValue(false)
            if (success) {
                fetchRegistrationPeriods()
                _error.postValue("")
            } else {
                _error.postValue("Failed to ${request.operation} registration period")
            }
            callback(success)
        }
    }
}