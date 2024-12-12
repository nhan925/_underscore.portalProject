package com.example.login_portal.ui.information


import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class InformationViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InformationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InformationViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}