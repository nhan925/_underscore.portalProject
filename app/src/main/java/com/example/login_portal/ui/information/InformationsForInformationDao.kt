package com.example.login_portal.ui.information

import android.util.Log
import com.example.login_portal.ui.information.InformationsForInformation
import com.example.login_portal.utils.ApiServiceHelper
import com.google.gson.Gson

object InformationsForInformationDao {
    fun getInformation(callback: (InformationsForInformation) -> Unit) {
        ApiServiceHelper.get("/rpc/get_informations_info") { response ->
            if (response != null) {
                    val gson = Gson()
                    val result = gson.fromJson(response.string(), InformationsForInformation::class.java)
                    callback(result)
            } else {
                callback(InformationsForInformation())
            }
        }
    }
}