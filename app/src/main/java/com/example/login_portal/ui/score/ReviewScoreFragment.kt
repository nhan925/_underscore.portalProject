package com.example.login_portal.ui.score

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.login_portal.R
import com.example.login_portal.databinding.FragmentReviewScoreBinding
import com.example.login_portal.ui.score.fragments.SemesterFragment
import com.example.login_portal.ui.score.fragments.SummaryFragment
import com.example.login_portal.ui.score.fragments.TotalFragment
import com.example.login_portal.ui.score.fragments.YearFragment
import com.example.login_portal.utils.ApiServiceHelper
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ReviewScoreFragment : Fragment() {
    private var _binding: FragmentReviewScoreBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: ScoreViewModel
    private lateinit var pagerAdapter: ReviewPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReviewScoreBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[ScoreViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
        setupTabLayout()
        setupButtons()
        observeChanges()
    }

    private fun setupViewPager() {
        pagerAdapter = ReviewPagerAdapter(this)
        binding.viewPager.apply {
            adapter = pagerAdapter
            isUserInputEnabled = true // Allow swiping
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position))
                    updateSummary()
                }
            })
        }
    }

    private fun setupTabLayout() {
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Thông tin chung"
                1 -> "Tổng kết"
                2 -> "Học kỳ"
                3 -> "Năm học"
                else -> ""
            }
        }.attach()

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    binding.viewPager.currentItem = it.position
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun setupButtons() {
        binding.submitButton.setOnClickListener {

                showConfirmationDialog()

        }

        binding.cancelButton.setOnClickListener {
            val slideOut = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_out_right)
            binding.root.startAnimation(slideOut)
            slideOut.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationEnd(animation: Animation?) {
                    findNavController().navigateUp()
                }
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationRepeat(animation: Animation?) {}
            })
        }
    }

    private fun observeChanges() {
        // Observe changes in each fragment and update summary
        viewModel.availableYears.observe(viewLifecycleOwner) { updateSummary() }
    }

     fun updateSummary() {
        val summaryFragment = pagerAdapter.getFragment(0) as? SummaryFragment ?: return
         //summaryFragment.clearAll()
        when (binding.viewPager.currentItem) {
            1 -> { // Total
                val totalFragment = pagerAdapter.getFragment(1) as? TotalFragment ?: return
                val (vnCount, enCount) = totalFragment.getTranscriptCounts()
                summaryFragment.updateTotal(vnCount, enCount)
            }
            2 -> { // Semester
                val semesterFragment = pagerAdapter.getFragment(2) as? SemesterFragment ?: return
                semesterFragment.getTranscriptInfo()?.let { (year, semester, counts) ->
                    summaryFragment.updateSemester(year, semester, counts.first, counts.second)
                }
            }
            3 -> { // Year
                val yearFragment = pagerAdapter.getFragment(3) as? YearFragment ?: return
                yearFragment.getTranscriptInfo()?.let { (year, counts) ->
                    summaryFragment.updateYear(year, counts.first, counts.second)
                }
            }
//            else -> summaryFragment.clearAll()
        }
    }


    private fun validateForm(): Boolean {
        return when (binding.viewPager.currentItem) {
            1 -> { // Total
                val totalFragment = pagerAdapter.getFragment(1) as? TotalFragment ?: return false
                val (vnCount, enCount) = totalFragment.getTranscriptCounts()
                validateCounts(vnCount, enCount)
            }
            2 -> { // Semester
                val semesterFragment = pagerAdapter.getFragment(2) as? SemesterFragment ?: return false
                val info = semesterFragment.getTranscriptInfo()
                if (info == null) {
                    showError("Vui lòng chọn năm học và học kỳ")
                    false
                } else {
                    validateCounts(info.third.first, info.third.second)
                }
            }
            3 -> { // Year
                val yearFragment = pagerAdapter.getFragment(3) as? YearFragment ?: return false
                val info = yearFragment.getTranscriptInfo()
                if (info == null) {
                    showError("Vui lòng chọn năm học")
                    false
                } else {
                    validateCounts(info.second.first, info.second.second)
                }
            }
            else -> false
        }
    }

    private fun validateCounts(vnCount: Int, enCount: Int): Boolean {
        if (vnCount == 0 && enCount == 0) {
            showError("Vui lòng nhập số lượng bảng điểm")
            return false
        }
        return true
    }

    private fun showConfirmationDialog() {
        //val summaryFragment = pagerAdapter.getFragment(0) as? SummaryFragment ?: return

        AlertDialog.Builder(requireContext())
            .setTitle("Xác nhận yêu cầu")
            .setMessage("Bạn có chắc chắn muốn xin bảng điểm " )

            .setPositiveButton("Xác nhận") { _, _ ->
                submitRequest()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun submitRequest() {
        // TODO: Implement API call to submit transcript request
        //loading
        binding.progressBar.visibility = View.VISIBLE
        binding.root.alpha = 0.5f
        binding.submitButton.isEnabled = false

        val content = buildRequestContent()
        Log.e("submitRequest", content)

        // Data goi API
        val data = mapOf(
            "content" to content,
            "status" to "Pending"
        )

        // Call API
        ApiServiceHelper.post("/rpc/add_request", data) { respone ->
            activity?.runOnUiThread {
                binding.progressBar.visibility = View.GONE
                binding.root.alpha = 1.0f
                binding.submitButton.isEnabled = true

                val success = respone?.contains("Request submitted successfully") == true
                if (success) {
                    showSuccess("Gửi yêu cầu xin bảng điểm thành công")
                    findNavController().navigateUp()
                } else {
                    showError("Gửi yêu cầu xin bảng điểm thất bại")
                }
            }
        }
        //showSuccess("Gửi yêu cầu xin bảng điểm thành công")
       // findNavController().navigateUp()
    }


    private fun buildRequestContent(): String {
        val summaryFragment = pagerAdapter.getFragment(0) as? SummaryFragment
        return summaryFragment?.getRequestContent() ?: "Xin bang diem."
    }






    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    private fun showSuccess(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


