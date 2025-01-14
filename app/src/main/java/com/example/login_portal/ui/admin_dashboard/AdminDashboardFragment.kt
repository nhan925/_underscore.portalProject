package com.example.login_portal.ui.admin_dashboard

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieAnimationView
import com.example.login_portal.R
import com.example.login_portal.databinding.FragmentAdminDashboardBinding
import com.example.login_portal.databinding.FragmentDashboardBinding

class AdminDashboardFragment : Fragment() {

    private var _binding: FragmentAdminDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AdminDashboardViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminDashboardBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(AdminDashboardViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        navigateFragment()
    }

    private fun navigateFragment(){
        binding.CVNotiAdmin.setOnClickListener {
            findNavController().navigate(R.id.action_nav_AdminDashboard_to_nav_AdminNoti)
        }

        binding.CVManageEnrollmentPeriod.setOnClickListener {
            findNavController().navigate(R.id.action_nav_AdminDashboard_to_nav_AdminManageEnrollmentPeriod)
        }

        binding.CVManageClass.setOnClickListener {
            findNavController().navigate(R.id.action_nav_AdminDashboard_to_nav_AdminManageClass)
        }

        binding.CVManageCourse.setOnClickListener {
            findNavController().navigate(R.id.action_nav_AdminDashboard_to_nav_AdminManageCourse)
        }

        binding.CVManageSemester.setOnClickListener {
            findNavController().navigate(R.id.action_nav_AdminDashboard_to_nav_AdminManageSemester)
        }

        binding.CVManageStudent.setOnClickListener {
            findNavController().navigate(R.id.action_nav_AdminDashboard_to_nav_AdminManageStudent)
        }

        binding.CVReviewFeedback.setOnClickListener {
            findNavController().navigate(R.id.action_nav_AdminDashboard_to_nav_AdminReviewFeedback)
        }
    }


}