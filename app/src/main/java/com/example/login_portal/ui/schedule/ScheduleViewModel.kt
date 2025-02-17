package com.example.login_portal.ui.schedule



import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScheduleViewModel : ViewModel() {
    private val _semestersLiveData = MutableLiveData<List<String>>()
    val semestersLiveData: LiveData<List<String>> get() = _semestersLiveData

    private val _coursesLiveData = MutableLiveData<List<Course>>()
    val coursesLiveData: LiveData<List<Course>> get() = _coursesLiveData

    val DayOfWeek = mapOf(
        "Thứ 2" to 1,
        "Thứ 3" to 2,
        "Thứ 4" to 3,
        "Thứ 5" to 4,
        "Thứ 6" to 5,
        "Thứ 7" to 6,
        "Chủ Nhật" to 7
    )

    init {
        viewModelScope.launch {
            loadSemesters()
        }
    }

    fun getSchedulePage(selectedSemester : String){
        viewModelScope.launch {
            getSchedule(selectedSemester)
        }
        Log.e("Course2",_coursesLiveData.value.toString())
    }

    private suspend fun loadSemesters() {
        withContext(Dispatchers.IO) {
            SchedulePageDao.getSemester { semesters ->
                _semestersLiveData.value = semesters
            }
        }
    }

    suspend fun getSchedule(semester : String){
        withContext(Dispatchers.IO) {
            val parts = semester.split("/")
            SchedulePageDao.getScheduleOfSemester(parts[0], parts[1].toInt()) { data ->
                _coursesLiveData.value = data
            }
        }
    }

    fun formattedCourseForCell(course : Course) : String{
        return "${course.CourseName} \n ${course.ClassId} - P.${course.Room}"
    }
}
