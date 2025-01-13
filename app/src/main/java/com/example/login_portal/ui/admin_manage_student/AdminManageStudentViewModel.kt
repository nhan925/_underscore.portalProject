package com.example.login_portal.ui.admin_manage_student

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AdminManageStudentViewModel : ViewModel() {

    private val _studentList = MutableLiveData<List<StudentInfo>>()
    val studentList: LiveData<List<StudentInfo>> get() = _studentList

    private val _selectedStudent = MutableLiveData<StudentInfo?>()
    val selectedStudent: MutableLiveData<StudentInfo?> get() = _selectedStudent

    private val _isEditing = MutableLiveData<Boolean>()
    val isEditing: LiveData<Boolean> get() = _isEditing

    private val _editPersonalEmail = MutableLiveData<String?>() // Bind email editing
    val editPersonalEmail: MutableLiveData<String?> get() = _editPersonalEmail

    private val _editPhoneNumber = MutableLiveData<String?>() // Bind phone editing
    val editPhoneNumber: MutableLiveData<String?> get() = _editPhoneNumber

    private val _editAddress = MutableLiveData<String?>() // Bind address editing
    val editAddress: MutableLiveData<String?> get() = _editAddress

    private val _showDatePickerEvent = MutableLiveData<Boolean>() // Trigger date picker
    val showDatePickerEvent: LiveData<Boolean> get() = _showDatePickerEvent

    private var currentEditingStudent: StudentInfo? = null
    private val allStudents = mutableListOf<StudentInfo>()

    init {
        _isEditing.value = false
    }

    // Fetch the list of students from the DAO
    fun fetchStudentList() {
        UpdateStudentInfoDAO.fetchAListOfStudents { students ->
            allStudents.clear()
            allStudents.addAll(students)
            _studentList.postValue(allStudents)
        }
    }

    // Search for students by query
    fun searchStudent(query: String?) {
        if (query.isNullOrBlank()) {
            _studentList.postValue(allStudents)
        } else {
            val filteredStudents = allStudents.filter {
                it.fullName.contains(query, ignoreCase = true) || it.studentId.contains(query)
            }
            _studentList.postValue(filteredStudents)
        }
    }

    // Load specific student info for editing
    fun loadStudentInfoForEditing(studentId: String) {
        val student = allStudents.find { it.studentId == studentId } ?: StudentInfo(
            studentId = "Unknown",
            userId = "Unknown",
            identityCardNumber = "",
            fullName = "Unknown Student",
            address = "",
            gender = "",
            dateOfBirth = "",
            phoneNumber = "",
            academicProgram = "",
            personalEmail = "",
            schoolEmail = "",
            yearOfAdmission = 0,
            nationality = "",
            ethnicity = "",
            majorId = "",
            newStudentId = ""
        )
        currentEditingStudent = student.copy()
        _selectedStudent.postValue(student)
        // Set initial values for editable fields
        _editPersonalEmail.value = student.personalEmail
        _editPhoneNumber.value = student.phoneNumber
        _editAddress.value = student.address
    }

    // Enable editing mode
    fun startEditing() {
        _isEditing.value = true
    }

    // Cancel editing and revert changes
    fun cancelEditing() {
        _isEditing.value = false
        _selectedStudent.value = currentEditingStudent // Revert to original
        // Revert editable fields
        _editPersonalEmail.value = currentEditingStudent?.personalEmail
        _editPhoneNumber.value = currentEditingStudent?.phoneNumber
        _editAddress.value = currentEditingStudent?.address
    }

    // Accept changes and update the student info
    fun acceptChanges() {
        _isEditing.value = false
        currentEditingStudent?.let { updatedStudent ->
            val newStudent = updatedStudent.copy(
                personalEmail = _editPersonalEmail.value,
                phoneNumber = _editPhoneNumber.value,
                address = _editAddress.value
            )
            _selectedStudent.value = newStudent
            updateStudentInfo(newStudent) { success ->
                // Optionally handle success or failure
            }
        }
    }

    // Show date picker (for date of birth)
    fun showDatePicker() {
        _showDatePickerEvent.value = true
    }

    // Reset the date picker event after it's been handled
    fun onDatePickerShown() {
        _showDatePickerEvent.value = false
    }

    // Update student information in the backend and local list
    private fun updateStudentInfo(updatedStudent: StudentInfo, callback: (Boolean) -> Unit) {
        UpdateStudentInfoDAO.updateStudentInfo(
            studentId = updatedStudent.studentId,
            updatedFields = mapOf(
                "student_id_input" to updatedStudent.studentId,
                "full_name_input" to updatedStudent.fullName,
                "academic_program_input" to updatedStudent.academicProgram,
                "year_of_admission_input" to updatedStudent.yearOfAdmission,
                "gender_input" to updatedStudent.gender,
                "date_of_birth_input" to updatedStudent.dateOfBirth,
                "nationality_input" to updatedStudent.nationality,
                "ethnicity_input" to updatedStudent.ethnicity,
                "identity_card_number_input" to updatedStudent.identityCardNumber,
                "personal_email_input" to updatedStudent.personalEmail,
                "phone_number_input" to updatedStudent.phoneNumber,
                "address_input" to updatedStudent.address,
                "major_id_input" to updatedStudent.majorId,
                "new_student_id_input" to updatedStudent.newStudentId
            )
        ) { success ->
            if (success) {
                val index = allStudents.indexOfFirst { it.studentId == updatedStudent.studentId }
                if (index != -1) {
                    allStudents[index] = updatedStudent
                    _studentList.postValue(allStudents)
                }
            }
            callback(success)
        }
    }
}
