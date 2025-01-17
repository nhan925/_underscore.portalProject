package com.example.login_portal.ui.admin_manage_enrollment_period

import android.util.Log
import com.example.login_portal.utils.ApiServiceHelper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object RegistrationPeriodDAO {
    fun getRegistrationPeriods(callback: (List<RegistrationPeriod>?) -> Unit) {
        ApiServiceHelper.get("/RegistrationPeriod") { response ->
            if (response != null) {
                try {
                    val periods = Gson().fromJson<List<RegistrationPeriod>>(
                        response,
                        object : TypeToken<List<RegistrationPeriod>>() {}.type
                    )
                    callback(periods)
                } catch (e: Exception) {
                    Log.e("RegistrationPeriodDAO", "Error parsing periods: ${e.message}")
                    callback(null)
                }
            } else {
                callback(null)
            }
        }
    }

    fun getSemesters(callback: (List<Semester>?) -> Unit) {
        ApiServiceHelper.get("/Semester") { response ->
            if (response != null) {
                try {
                    val semesters = Gson().fromJson<List<Semester>>(
                        response,
                        object : TypeToken<List<Semester>>() {}.type
                    )
                    callback(semesters)
                } catch (e: Exception) {
                    Log.e("RegistrationPeriodDAO", "Error parsing semesters: ${e.message}")
                    callback(null)
                }
            } else {
                callback(null)
            }
        }
    }

    fun manageRegistrationPeriod(request: ManageRegistrationPeriodRequest, callback: (Boolean) -> Unit) {
        // Log request để debug
        Log.d("RegistrationPeriodDAO", "Request: $request")

        ApiServiceHelper.post("/rpc/manage_registration_period", request) { response ->
            if (response != null) {
                // Log response để debug
                Log.d("RegistrationPeriodDAO", "Response: $response")
                callback(true)
            } else {
                Log.e("RegistrationPeriodDAO", "Failed to manage registration period")
                callback(false)
            }
        }
    }
}
