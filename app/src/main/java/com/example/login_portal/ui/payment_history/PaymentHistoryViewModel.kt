package com.example.login_portal.ui.payment_history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PaymentHistoryViewModel : ViewModel() {
    private val _paymenHistory = MutableLiveData<List<PaymentHistoryItem>>()

    val PaymentHistory: LiveData<List<PaymentHistoryItem>> = _paymenHistory

    fun getAllPaymentHistory() {
        PaymentHistoryDao.getAllPaymentHistory { response ->
            if (response.isNotEmpty()) {
                _paymenHistory.postValue(response)
            } else {
                _paymenHistory.postValue(emptyList())
            }
        }
    }

}