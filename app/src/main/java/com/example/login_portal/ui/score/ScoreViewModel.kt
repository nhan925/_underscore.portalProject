package com.example.login_portal.ui.score

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.login_portal.utils.ApiServiceHelper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class ScoreData(
    val year: String,
    val semester: String,
    val courseCode: String,
    val subjectName: String,
    val credits: Int,
    val classCode: String,
    val score10: Double,
    val score4: Double,
    val notes: String
)

data class Statistics(
    val gpa4Scale: Double,
    val gpa10Scale: Double,
    val totalCredits: Int,
    val totalCourses: Int
)

class ScoreViewModel : ViewModel() {
    private val _scoreData = MutableLiveData<List<ScoreData>>()
    val scoreData: LiveData<List<ScoreData>> = _scoreData

    private val _statistics = MutableLiveData<Statistics>()
    val statistics: LiveData<Statistics> = _statistics

    private val _availableYears = MutableLiveData<List<String>>()
    val availableYears: LiveData<List<String>> = _availableYears

    private val _availableSemesters = MutableLiveData<List<String>>()
    val availableSemesters: LiveData<List<String>> = _availableSemesters

    // Trạng thái loading
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // Bắt lỗi
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    // Dữ liệu gốc
    private var originalScoreData: List<ScoreData> = listOf()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        _isLoading.value = true

        ApiServiceHelper.get("/rpc/get_course_info_by_semester?p_year=&p_semester_num=") { response ->
            _isLoading.value = false

            if (response != null) {
                try {
                    val type = object : TypeToken<List<ApiScoreResponse>>() {}.type
                    val apiScores = Gson().fromJson<List<ApiScoreResponse>>(response, type)

                    if (apiScores != null) {
                        originalScoreData = apiScores.map { apiScore ->
                            ScoreData(
                                year= apiScore.Year,
                                semester = apiScore.Semester,
                                courseCode = apiScore.CourseId,
                                subjectName = apiScore.CourseName,
                                credits = apiScore.CourseCredit,
                                classCode = apiScore.ClassId,
                                score10 = apiScore.GradeScaleTen,
                                score4 = calculateScore4(apiScore.GradeScaleTen),
                                notes = apiScore.Note ?: ""
                            )
                        }
                        _scoreData.value = originalScoreData
                        calculateStatistics(originalScoreData)
                        updateAvailableYears()
                    }
                } catch (e: Exception) {
                    _error.value = "Error parsing data: ${e.message}"
                }
            } else {
                _error.value = "Failed to load scores"
            }
        }
    }

    fun filterScores(year: String, semester: String) {
        val filteredScores = when {
            year == "All" && semester == "All" -> originalScoreData
            year == "All" -> originalScoreData.filter { it.semester == semester }
            semester == "All" -> {
                val fullYear = try {
                    val parts = year.split("-")
                    "20${parts[0]} - 20${parts[1]}"
                } catch (e: Exception) {
                    year
                }
                originalScoreData.filter { it.year == fullYear }
            }
            else -> {
                val fullYear = try {
                    val parts = year.split("-")
                    "20${parts[0]} - 20${parts[1]}"
                } catch (e: Exception) {
                    year
                }
                originalScoreData.filter { it.year == fullYear && it.semester == semester }
            }
        }

        _scoreData.value = filteredScores
        calculateStatistics(filteredScores)
    }

    fun updateAvailableSemesters(selectedYear: String) {
        val semesters = if (selectedYear == "All") {
            // Khi chọn All, hiển thị tất cả học kỳ có thể có
            listOf("All")
        } else {
            // Khi chọn một năm cụ thể
            val fullYear = try {
                val parts = selectedYear.split("-")
                "20${parts[0]} - 20${parts[1]}"
            } catch (e: Exception) {
                selectedYear
            }

            // Lấy các học kỳ thực tế có trong năm được chọn
            val semestersForYear = originalScoreData
                .filter { it.year == fullYear }
                .map { it.semester }
                .distinct()
                .sorted()

            listOf("All") + semestersForYear
        }



        _availableSemesters.value = semesters
    }

    private fun updateAvailableYears() {
        val years = _scoreData.value?.map { it.year }
            ?.distinct()
            ?.sorted()
            ?.map { year ->
                try {
                    // Kiểm tra xem năm có đúng format không
                    if (year.contains("-")) {
                        val parts = year.split("-")
                        // Đảm bảo lấy 2 số cuối của năm
                        val startYear = parts[0].trim().takeLast(2)
                        val endYear = parts[1].trim().takeLast(2)
                        "$startYear-$endYear"
                    } else {
                        // Nếu chỉ có một năm, tạo format năm học
                        val startYear = year.trim().takeLast(2)
                        val endYear = (year.toInt() + 1).toString().takeLast(2)
                        "$startYear-$endYear"
                    }
                } catch (e: Exception) {
                    println("Year Format Error: $year - ${e.message}")
                    year
                }
            } ?: listOf()



        _availableYears.value = listOf("All") + years
    }

    private fun calculateStatistics(scores: List<ScoreData>) {
        if (scores.isEmpty()) {
            _statistics.value = Statistics(0.0, 0.0, 0, 0)
            return
        }

        val totalCredits = scores.sumOf { it.credits }

        // Tính GPA theo thang điểm 10
        val weightedSum10Scale = scores.sumOf { it.score10 * it.credits }
        val gpa10Scale = weightedSum10Scale / totalCredits

        // Tính GPA theo thang điểm 4
        val weightedSum4Scale = scores.sumOf { it.score4 * it.credits }
        val gpa4Scale = weightedSum4Scale / totalCredits

        _statistics.value = Statistics(
            gpa4Scale = gpa4Scale,
            gpa10Scale = gpa10Scale,
            totalCredits = totalCredits,
            totalCourses = scores.size
        )
    }

    // Tính điểm hệ 4 từ điểm hệ 10
    private fun calculateScore4(score10: Double): Double {
        return when {
            score10 >= 9.0 -> 4.0
            score10 > 8.0 -> 1.0 + (score10 - 3.0) * 0.5
            score10 < 3.0 -> 0.0
            else -> 1.0 + (score10 - 3.0) * 0.5
        }
    }

    // Thêm data class để parse JSON response
    private data class ApiScoreResponse(
        val Year: String,
        val Semester: String,
        val CourseId: String,
        val CourseName: String,
        val CourseCredit: Int,
        val ClassId: String,
        val GradeScaleTen: Double,
        val Note: String?
    )
}