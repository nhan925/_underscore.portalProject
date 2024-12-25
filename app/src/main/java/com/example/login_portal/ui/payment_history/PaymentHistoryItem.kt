package com.example.login_portal.ui.payment_history

data class PaymentHistoryItem (
    var Id: Int = 0,
    var Amount: Double = 0.0,
    var Date: String = "",
    var Type: String = "",
    var IsSuccess: Boolean = false
)