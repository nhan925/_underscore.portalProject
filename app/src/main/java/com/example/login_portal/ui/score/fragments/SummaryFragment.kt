package com.example.login_portal.ui.score.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.login_portal.R
import com.example.login_portal.databinding.TabSummaryBinding

class SummaryFragment : Fragment() {
    private var _binding: TabSummaryBinding? = null
    private val binding get() = _binding!!
    private var totalCount = 0


    private data class TabState(
        var vnCount: Int = 0,
        var enCount: Int = 0,
        var year: String = "",
        var semester: String = "",
        var isVisible: Boolean = false
    )


    private val totalState = TabState()
    private val semesterState = TabState()
    private val yearState = TabState()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = TabSummaryBinding.inflate(inflater, container, false)
        return binding.root
    }


    fun updateTotal(vnCount: Int, enCount: Int) {
        totalState.apply {
            this.vnCount = vnCount
            this.enCount = enCount
            isVisible = vnCount > 0 || enCount > 0
        }
        updateUI()
    }


    fun updateSemester(year: String, semester: String, vnCount: Int, enCount: Int) {
        semesterState.apply {
            this.year = year
            this.semester = semester
            this.vnCount = vnCount
            this.enCount = enCount
            isVisible = vnCount > 0 || enCount > 0
        }
        updateUI()
    }


    fun updateYear(year: String, vnCount: Int, enCount: Int) {
        yearState.apply {
            this.year = year
            this.vnCount = vnCount
            this.enCount = enCount
            isVisible = vnCount > 0 || enCount > 0
        }
        updateUI()
    }

    private fun updateUI() {
        binding.totalLayout.visibility = if (totalState.isVisible) View.VISIBLE else View.GONE
        if (totalState.isVisible) {
            binding.tvTotalVN.text = context?.getString(
                R.string.total_vn,
                totalState.vnCount.toString().padStart(2, '0')
            )
            binding.tvTotalEN.text = context?.getString(
                R.string.total_en,
                totalState.enCount.toString().padStart(2, '0')
            )
        }

        binding.semesterLayout.visibility = if (semesterState.isVisible) View.VISIBLE else View.GONE
        if (semesterState.isVisible) {
            binding.tvSemesterTitle.text = context?.getString(
                R.string.semester_title,
                semesterState.year,
                semesterState.semester
            )
            binding.tvSemesterVN.text = context?.getString(
                R.string.total_vn,
                semesterState.vnCount.toString().padStart(2, '0')
            )
            binding.tvSemesterEN.text = context?.getString(
                R.string.total_en,
                semesterState.enCount.toString().padStart(2, '0')
            )
        }

        binding.yearLayout.visibility = if (yearState.isVisible) View.VISIBLE else View.GONE
        if (yearState.isVisible) {
            binding.tvYearTitle.text = context?.getString(
                R.string.year_title,
                yearState.year
            )
            binding.tvYearVN.text = context?.getString(
                R.string.total_vn,
                yearState.vnCount.toString().padStart(2, '0')
            )
            binding.tvYearEN.text = context?.getString(
                R.string.total_en,
                yearState.enCount.toString().padStart(2, '0')
            )
        }

        totalCount = (if (totalState.isVisible) totalState.vnCount + totalState.enCount else 0) +
                (if (semesterState.isVisible) semesterState.vnCount + semesterState.enCount else 0) +
                (if (yearState.isVisible) yearState.vnCount + yearState.enCount else 0)
        binding.tvTotalCount.text = totalCount.toString()
    }



    fun getRequestContent(): String {
        val contentBuilder = StringBuilder("Xin bảng điểm\r\n")

        // Thêm thông tin Tổng kết nếu có
        if (binding.totalLayout.visibility == View.VISIBLE) {
            contentBuilder.append("Tổng kết,")
                .append("Tiếng Việt: ")
                .append(binding.tvTotalVN.text.toString().substringAfter(": "))
                .append(",")
                .append("Tiếng Anh: ")
                .append(binding.tvTotalEN.text.toString().substringAfter(": "))
                .append("\r\n")
        }

        // Thêm thông tin Học kỳ nếu có
        if (binding.semesterLayout.visibility == View.VISIBLE) {
            contentBuilder
                .append(binding.tvSemesterTitle.text.toString())
                .append(", ")
                .append("Tiếng Việt: ")
                .append(binding.tvSemesterVN.text.toString().substringAfter(": "))
                .append(",")
                .append("Tiếng Anh: ")
                .append(binding.tvSemesterEN.text.toString().substringAfter(": "))
                .append("\r\n")
        }

        // Thêm thông tin Năm học nếu có
        if (binding.yearLayout.visibility == View.VISIBLE) {
            contentBuilder
                .append(binding.tvYearTitle.text.toString())
                .append(", ")
                .append(binding.tvYearVN.text.toString().substringAfter(":"))
                .append(", ")
                .append(binding.tvYearEN.text.toString().substringAfter(":"))
                .append("\r\n")
        }

        // Thêm tổng số bảng điểm
        contentBuilder.append("Tổng số bảng điểm: ")
            .append(binding.tvTotalCount.text)

        return contentBuilder.toString()
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}



