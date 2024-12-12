package com.example.login_portal.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.login_portal.utils.ApiServiceHelper
import com.google.gson.Gson


class DashboardViewModel : ViewModel() {
    private val _dashboardData = MutableLiveData<StudentDashboard>()
    val dashboardData: LiveData<StudentDashboard> = _dashboardData

    init {
        loadDashboardData()
    }

    data class Semester(
        val name: String,
        val gpa: Float
    )

    data class StudentDashboard(
        val studentFullName: String,
        val semesters: List<Semester>,
        val cgpa: Float,
        val currentCredit: Int,
        val totalCredit: Int
    )

    private data class ApiScoreResponse(
        val StudentFullName: String,
        val GPAs: Map<String, Float>,
        val CGPA: Float,
        val CurrentCredit: Int,
        val TotalCredit: Int
    )

    fun loadDashboardData() {
        ApiServiceHelper.get("/rpc/get_dashboard_info") { response ->
            response?.let {
                try {
                    val dashboardInfo = Gson().fromJson(it, ApiScoreResponse::class.java)
                    val semesters = dashboardInfo.GPAs.map { entry ->
                        Semester(entry.key, entry.value)
                    }.sortedWith(compareBy<Semester> { 
                        val yearStr = it.name.split("/")[1].trim()
                        val startYear = yearStr.split("-")[0].trim().toInt()
                        startYear
                    }.thenBy { 
                        it.name.split("/")[0].replace("HK", "").trim().toInt()
                    })

                    val studentDashboard = StudentDashboard(
                        studentFullName = dashboardInfo.StudentFullName,
                        semesters = semesters,
                        cgpa = dashboardInfo.CGPA,
                        currentCredit = dashboardInfo.CurrentCredit,
                        totalCredit = dashboardInfo.TotalCredit
                    )
                    _dashboardData.postValue(studentDashboard)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

}