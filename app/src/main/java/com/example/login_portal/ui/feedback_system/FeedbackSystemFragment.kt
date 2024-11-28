package com.example.login_portal.ui.feedback_system

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.login_portal.R
import com.example.login_portal.databinding.FragmentFeedbackSystemBinding
import com.example.login_portal.utils.Validator


class FeedbackSystemFragment : Fragment() {

    private var _binding: FragmentFeedbackSystemBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val feedbackSystemViewModel =
            ViewModelProvider(this).get(FeedbackSystemViewModel::class.java)

        _binding = FragmentFeedbackSystemBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val categories = resources.getStringArray(R.array.feedback_system_category_items)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_feedback_system_categories, categories)
        binding.fbSystemCategoryList.setAdapter(arrayAdapter)


        binding.submitBtn.setOnClickListener {
            val selectedCategory = binding.fbSystemCategoryList.text.toString()
            val content = binding.textInputLayout2.editText?.text.toString()

            if(!validateFeedbackContent(content)){
                return@setOnClickListener
            }

            feedbackSystemViewModel.updateSelectedCategory(selectedCategory)
            feedbackSystemViewModel.updateFeedbackContent(content)
            feedbackSystemViewModel.postFeedback()
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun validateFeedbackContent(content: String): Boolean {
        var result = Validator.validateFeedbackContent(content)
        if(result.isValid){
            return true
        }
        else{
            Toast.makeText(context, result.errorMessage, Toast.LENGTH_SHORT).show()
            return false
        }
    }
}