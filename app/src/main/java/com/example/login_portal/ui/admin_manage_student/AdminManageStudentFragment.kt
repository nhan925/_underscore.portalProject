package com.example.login_portal.ui.admin_manage_student

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.login_portal.R
import com.example.login_portal.databinding.FragmentAdminManageStudentBinding

class AdminManageStudentFragment : Fragment() {

    private var _binding: FragmentAdminManageStudentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val adminSemesterViewModel =
            ViewModelProvider(this).get(AdminManageStudentViewModel::class.java)

        _binding = FragmentAdminManageStudentBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textAdminStudent
        adminSemesterViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}