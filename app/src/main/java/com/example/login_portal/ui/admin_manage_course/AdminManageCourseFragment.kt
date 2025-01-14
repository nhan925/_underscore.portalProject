package com.example.login_portal.ui.admin_manage_course

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.login_portal.R
import com.example.login_portal.databinding.FragmentAdminManageCourseBinding

class AdminManageCourseFragment : Fragment() {

    private var _binding: FragmentAdminManageCourseBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val adminEnrollmentViewModel =
            ViewModelProvider(this).get(AdminManageCourseViewModel::class.java)

        _binding = FragmentAdminManageCourseBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textAdminCourse
        adminEnrollmentViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}