package com.example.login_portal.ui.admin_manage_class

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

class AdminManageClassViewModel : ViewModel() {
    // LiveData for class list - initialize with empty list instead of null
    private val _classes = MutableLiveData<List<ClassInfo>?>(emptyList())
    val classes: MutableLiveData<List<ClassInfo>?> = _classes

    private var originalClasses: List<ClassInfo> = emptyList()


    // Loading state - default to false
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    // Error handling - use sealed class for error states
    sealed class ErrorState {
        object None : ErrorState()
        data class Error(val message: String) : ErrorState()
    }

    private val _error = MutableLiveData<ErrorState>(ErrorState.None)
    val error: LiveData<ErrorState> = _error

    // Import progress - use a data class for progress state
    data class ImportProgress(
        val current: Int = 0,
        val total: Int = 0,
        val message: String = ""
    )

    private val _importProgress = MutableLiveData(ImportProgress())
    val importProgress: LiveData<ImportProgress> = _importProgress

    // Selected Excel file - handled internally
    private var selectedExcelUri: Uri? = null

    init {
        loadClasses()
    }

    fun loadClasses() {
        _isLoading.value = true
        ClassDAO.getClasses { classList ->
            _isLoading.value = false

            if (classList != null) {
                if (classList.isNotEmpty()) {
                    originalClasses = classList // Store the fetched data in originalClasses
                    _classes.value = classList // Update LiveData with the fetched data
                } else {
                    _error.value = ErrorState.Error("No classes found")
                    _classes.value = emptyList()
                    originalClasses = emptyList()
                }
            } else {
                _error.value = ErrorState.Error("Failed to load classes. API returned null.")
                _classes.value = emptyList()
                originalClasses = emptyList()
            }
        }
    }

    fun filterClasses(predicate: (ClassInfo) -> Boolean) {
        _classes.value = originalClasses.filter(predicate)
    }


    fun resetClassFilter() {
        _classes.value = originalClasses
    }


    fun searchClasses(query: String) {
        val currentClasses = _classes.value.orEmpty()
        if (query.isEmpty()) {
            _classes.value = currentClasses
            return
        }

        val filteredClasses = currentClasses.filter { classInfo ->
            classInfo.className.contains(query, ignoreCase = true) ||
                    classInfo.classId.contains(query, ignoreCase = true)
        }
        _classes.value = filteredClasses
    }

    fun setSelectedExcelFile(uri: Uri) {
        selectedExcelUri = uri
        _error.value = ErrorState.None // Reset error state
    }

    fun importClassesFromExcel(context: Context) {
        val uri = selectedExcelUri
        if (uri == null) {
            _error.value = ErrorState.Error("No file selected")
            return
        }

        _isLoading.value = true
        ExcelImportHandler.importClassesFromExcel(
            context = context,
            uri = uri,
            onProgress = { current, total ->
                _importProgress.value = ImportProgress(
                    current = current,
                    total = total,
                    message = "$current/$total classes imported"
                )
            },
            onComplete = { success, message ->
                _isLoading.value = false
                if (success) {
                    loadClasses() // Reload the class list after successful import
                    _error.value = ErrorState.None
                } else {
                    _error.value = ErrorState.Error(message ?: "Import failed")
                }
                selectedExcelUri = null // Reset selected file
            }
        )
    }

    fun clearError() {
        _error.value = ErrorState.None
    }

    override fun onCleared() {
        super.onCleared()
        _isLoading.value = false
        selectedExcelUri = null
        _classes.value = emptyList()
        _error.value = ErrorState.None
        _importProgress.value = ImportProgress()
    }
}
