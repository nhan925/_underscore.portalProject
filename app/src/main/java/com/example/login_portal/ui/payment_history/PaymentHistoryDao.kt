package com.example.login_portal.ui.payment_history

import com.example.login_portal.utils.ApiServiceHelper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object PaymentHistoryDao {
    fun getAllPaymentHistory(callback: (List<PaymentHistoryItem>) -> Unit) {
        ApiServiceHelper.get("/rpc/get_payment_history") { response ->
            if (response != null) {
                val result: List<PaymentHistoryItem> = Gson().fromJson(response, object : TypeToken<List<PaymentHistoryItem>>() {}.type)
                callback(result) // Pass the result to the callback
            } else {
                callback(listOf()) // Pass empty list if no response
            }
        }
    }

}