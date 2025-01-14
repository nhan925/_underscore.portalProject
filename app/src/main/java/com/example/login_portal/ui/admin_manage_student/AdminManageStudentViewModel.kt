package com.example.login_portal.ui.admin_manage_student

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import android.util.Log

class AdminManageStudentViewModel : ViewModel() {

    private val _studentList = MutableLiveData<List<StudentInfo>>()
    val studentList: LiveData<List<StudentInfo>> get() = _studentList

    private val _selectedStudent = MutableLiveData<StudentInfo?>()
    val selectedStudent: LiveData<StudentInfo?> get() = _selectedStudent

    private val _isEditing = MutableLiveData<Boolean>()
    val isEditing: LiveData<Boolean> get() = _isEditing

    private val _showDatePickerEvent = MutableLiveData<Boolean>()
    val showDatePickerEvent: LiveData<Boolean> get() = _showDatePickerEvent

    private val _majorList = MutableLiveData<List<Major>>()
    val majorList: LiveData<List<Major>> get() = _majorList

    val selectedMajorId = MutableLiveData<String?>()
    private val majorIdToNameMap = mutableMapOf<String, String>()

    val editFullName = MutableLiveData<String?>()

    val editYearOfAdmissionText = MutableLiveData<String>()  // Changed to String
    val editAcademicProgram = MutableLiveData<String?>()
    val editGender = MutableLiveData<String?>()

    val editDateOfBirth = MutableLiveData<String?>()
    val editNationality = MutableLiveData<String?>()
    val editEthnicity = MutableLiveData<String?>()
    val editIdentityCardNumber = MutableLiveData<String?>()
    val editSchoolEmail = MutableLiveData<String?>()
    val editPersonalEmail = MutableLiveData<String?>()
    val editPhoneNumber = MutableLiveData<String?>()
    val editAddress = MutableLiveData<String?>()

    private val allStudents = mutableListOf<StudentInfo>()
    private var currentEditingStudent: StudentInfo? = null

    init {
        _isEditing.value = false
    }

    private fun getYearOfAdmissionAsInt(): Int? {
        return editYearOfAdmissionText.value?.toIntOrNull()
    }

    fun fetchMajorList() {
        UpdateStudentInfoDAO.fetchAllMajors { majors ->
            _majorList.postValue(majors)
            majorIdToNameMap.clear()
            majors.forEach { major ->
                majorIdToNameMap[major.major_id] = major.major_name
            }
        }
    }

    fun getMajorName(majorId: String?): String {
        return majorIdToNameMap[majorId] ?: majorId ?: ""
    }

    fun fetchStudentList() {
        Log.d("AdminManageStudentViewModel", "Fetching student list...")

        UpdateStudentInfoDAO.fetchAListOfStudents { students ->
            if (students.isNullOrEmpty()) {
                Log.e("AdminManageStudentViewModel", "Failed to fetch students or list is empty")
            } else {
                Log.d("AdminManageStudentViewModel", "Fetched ${students.size} students successfully")
            }

            allStudents.clear()
            allStudents.addAll(students)
            _studentList.postValue(allStudents)
        }
    }

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

    fun loadStudentInfoForEditing(studentId: String) {
        Log.d("AdminManageStudentViewModel", "Loading student info for ID: $studentId")

        val student = allStudents.find { it.studentId == studentId } ?: createDefaultStudent()
        currentEditingStudent = student.copy()
        _selectedStudent.postValue(student)
        selectedMajorId.value = student.majorId
        initializeEditingFields(student)
    }

    private fun createDefaultStudent() = StudentInfo(
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
        newStudentId = "",
        avatarUrl = ""
    )

    private fun initializeEditingFields(student: StudentInfo) {
        editFullName.value = student.fullName
        editYearOfAdmissionText.value = student.yearOfAdmission?.toString() ?: ""  // Changed
        editAcademicProgram.value = student.academicProgram
        editGender.value = student.gender
        editDateOfBirth.value = student.dateOfBirth
        editNationality.value = student.nationality
        editEthnicity.value = student.ethnicity
        editIdentityCardNumber.value = student.identityCardNumber
        editSchoolEmail.value = student.schoolEmail
        editPersonalEmail.value = student.personalEmail
        editPhoneNumber.value = student.phoneNumber
        editAddress.value = student.address
    }

    fun startEditing() {
        _isEditing.value = true
    }

    fun stopEditing() {
        _isEditing.value = false
        _selectedStudent.value = currentEditingStudent
        currentEditingStudent?.let { student ->
            initializeEditingFields(student)
            selectedMajorId.value = student.majorId
        }
    }

    fun acceptChanges() {
        currentEditingStudent?.let { originalStudent ->
            val updatedStudent = originalStudent.copy(
                fullName = editFullName.value.orEmpty(),
                yearOfAdmission = getYearOfAdmissionAsInt() ?: originalStudent.yearOfAdmission,  // Changed
                academicProgram = editAcademicProgram.value.orEmpty(),
                gender = editGender.value.orEmpty(),
                dateOfBirth = editDateOfBirth.value.orEmpty(),
                nationality = editNationality.value.orEmpty(),
                ethnicity = editEthnicity.value.orEmpty(),
                identityCardNumber = editIdentityCardNumber.value.orEmpty(),
                schoolEmail = editSchoolEmail.value.orEmpty(),
                personalEmail = editPersonalEmail.value.orEmpty(),
                phoneNumber = editPhoneNumber.value.orEmpty(),
                address = editAddress.value.orEmpty()

            )

            Log.d("acceptChanges", "Updated student object: $updatedStudent")

            val updatedFields = mapOf(

                "full_name_input" to updatedStudent.fullName,
                "academic_program_input" to updatedStudent.academicProgram,
                "year_of_admission_input" to getYearOfAdmissionAsInt(),  // Changed
                "gender_input" to updatedStudent.gender,
                "date_of_birth_input" to updatedStudent.dateOfBirth,
                "nationality_input" to updatedStudent.nationality,
                "ethnicity_input" to updatedStudent.ethnicity,
                "identity_card_number_input" to updatedStudent.identityCardNumber,
                "school_email_input" to updatedStudent.schoolEmail,
                "personal_email_input" to updatedStudent.personalEmail,
                "phone_number_input" to updatedStudent.phoneNumber,
                "address_input" to updatedStudent.address,
            )

            updateStudentInfo(updatedStudent) { success ->
                if (success) {
                    _selectedStudent.value = updatedStudent
                    val index = allStudents.indexOfFirst { it.studentId == originalStudent.studentId }
                    if (index != -1) {
                        allStudents[index] = updatedStudent
                        _studentList.postValue(allStudents)
                    }
                } else {
                    Log.e("acceptChanges", "Failed to update student info via API")
                }
            }
        }

        _isEditing.value = false
    }

    private fun updateStudentInfo(updatedStudent: StudentInfo, callback: (Boolean) -> Unit) {
        val updatedFields = mapOf(
            "student_id_input" to updatedStudent.studentId,
            "full_name_input" to updatedStudent.fullName,
            "academic_program_input" to updatedStudent.academicProgram,
            "year_of_admission_input" to getYearOfAdmissionAsInt(),  // Changed
            "gender_input" to updatedStudent.gender,
            "date_of_birth_input" to updatedStudent.dateOfBirth,
            "nationality_input" to updatedStudent.nationality,
            "ethnicity_input" to updatedStudent.ethnicity,
            "identity_card_number_input" to updatedStudent.identityCardNumber,
            "school_email_input" to updatedStudent.schoolEmail,
            "personal_email_input" to updatedStudent.personalEmail,
            "phone_number_input" to updatedStudent.phoneNumber,
            "address_input" to updatedStudent.address,
        )

        UpdateStudentInfoDAO.updateStudentInfo(
            studentId = updatedStudent.studentId,
            updatedFields = updatedFields
        ) { success ->
            callback(success)
        }
    }

    fun showDatePicker() {
        _showDatePickerEvent.value = true
    }

    fun onDatePickerShown() {
        _showDatePickerEvent.value = false
    }
}