package com.example.login_portal.ui.admin_manage_class

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.login_portal.R
import com.example.login_portal.databinding.FragmentAdminManageClassBinding

class AdminManageClassFragment : Fragment() {

    private var _binding: FragmentAdminManageClassBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val adminClassViewModel =
            ViewModelProvider(this).get(AdminManageClassViewModel::class.java)

        _binding = FragmentAdminManageClassBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textAdminClass
        adminClassViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}