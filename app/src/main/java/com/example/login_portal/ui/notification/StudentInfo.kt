package com.example.login_portal.ui.notification

import android.util.Log
import com.example.login_portal.ui.information.InformationsForInformation
import com.example.login_portal.ui.information.InformationsForInformationDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object StudentInfo {
    private var userInfo: InformationsForInformation? = null

    fun getStudentId(): String? {
        if (userInfo == null) {
            Log.e("StudentInfo", "UserInfo is null, fetch information before accessing StudentId.")
        }
        return userInfo?.StudentId
    }

    suspend fun fetchAndUpdateUserInfo(): InformationsForInformation? {
        return withContext(Dispatchers.IO) {
            val result = kotlinx.coroutines.suspendCancellableCoroutine<InformationsForInformation?> { continuation ->
                InformationsForInformationDao.getInformation { data ->
                    if (data.StudentId != null) {
                        Log.d("StudentInfo", "Student information fetched successfully: $data")
                    } else {
                        Log.e("StudentInfo", "Failed to fetch student information or StudentId is null.")
                    }
                    userInfo = data
                    continuation.resume(data) {}
                }
            }
            result
        }
    }
}