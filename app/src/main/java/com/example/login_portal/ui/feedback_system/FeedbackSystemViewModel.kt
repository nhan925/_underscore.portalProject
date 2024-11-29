package com.example.login_portal.ui.feedback_system



import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.login_portal.R
import com.example.login_portal.utils.Validator

class FeedbackSystemViewModel : ViewModel() {

    private lateinit var selectedCategory: String

    private lateinit var content: String

    fun updatedSelectedCategory(value: String) {
        selectedCategory = value
    }

    fun updateFeedbackContent(value: String) {
       content = value
    }

    fun validateFeedbackContent(context: Context): Boolean {
        val result = Validator.validateFeedbackContent(content)
        if(result.isValid){
            return true
        }
        else{
            Toast.makeText(context, result.errorMessage, Toast.LENGTH_SHORT).show()
            return false
        }
    }

    fun postFeedback(callback: (Boolean) -> Unit) {
        FeedbackSystemDAO.postFeedback(content, selectedCategory) { response ->
            if (response.isNotEmpty()) {
                callback(true)  // Success
            } else {
                callback(false)  // Failure
            }
        }
    }
}