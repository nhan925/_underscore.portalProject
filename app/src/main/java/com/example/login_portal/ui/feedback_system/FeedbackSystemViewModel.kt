package com.example.login_portal.ui.feedback_system



import android.content.res.Resources
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.login_portal.R
import com.example.login_portal.utils.Validator

class FeedbackSystemViewModel : ViewModel() {

    private val _selectedCategory = MutableLiveData<String>().apply {
        value = ""
    }

    val selectedCategory: MutableLiveData<String> = _selectedCategory

    val _content = MutableLiveData<String>().apply {
        value = ""
    }

    val content: MutableLiveData<String> = _content

    fun updateSelectedCategory(category: String) {
        _selectedCategory.value = category
    }

    fun updateFeedbackContent(content: String) {
       _content.value = content
    }

    fun postFeedback(){
        //Post database to feedback
        Log.d("FeedbackSystemViewModel", content.value.toString())
        Log.d("FeedbackSystemViewModel", selectedCategory.value.toString())
    }
}