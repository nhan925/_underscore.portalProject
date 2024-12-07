package com.example.login_portal.ui.schedule

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.login_portal.R
import com.example.login_portal.databinding.FragmentScheduleBinding
import androidx.gridlayout.widget.GridLayout


class ScheduleFragment : Fragment() {
    private lateinit var scheduleViewModel : ScheduleViewModel
    private var _binding: FragmentScheduleBinding? = null
    private val NUMBER_OF_COLUMN = 9
    private val WIDTH_OF_TWOFIRST_COL = 200
    private val WIDTH_OF_OTHER_COL = 400

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        scheduleViewModel = ViewModelProvider(this).get(ScheduleViewModel::class.java)
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupSemesterSpinner()
        //initializeTable()
        scheduleViewModel.coursesLiveData.observe(viewLifecycleOwner){
            updateTable(it)
        }
        return root
    }

    private fun setupSemesterSpinner() {
        scheduleViewModel.semestersLiveData.observe(viewLifecycleOwner) { semesters ->
            Log.e("SEMESTER", semesters.toString())
            val adapterSemester = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_dropdown_item_1line,
                semesters?.toList() ?: emptyList()
            )
            binding.schedulePageSemesterSpinner.setAdapter(adapterSemester)
        }

        binding.schedulePageSemesterSpinner.setOnItemClickListener { parent, view, position, id ->
            val selectedSemester = scheduleViewModel.semestersLiveData.value?.get(position)
            if (selectedSemester != null) {
                scheduleViewModel.getSchedulePage(selectedSemester)
            }
        }
    }

    private fun initializeTable() {
        val NUMBER_OF_PERIOD = 10
        val headers = arrayOf(
            getString(R.string.schedule_page_session),
            getString(R.string.schedule_page_period),
            getString(R.string.schedule_page_monday),
            getString(R.string.schedule_page_tuesday),
            getString(R.string.schedule_page_wednesday),
            getString(R.string.schedule_page_thursday),
            getString(R.string.schedule_page_friday),
            getString(R.string.schedule_page_saturday),
            getString(R.string.schedule_page_sunday)
        )

        // Đặt số cột cho GridLayout
        binding.schedulePageScheduleTable.columnCount = headers.size

        // Add header cells trực tiếp vào GridLayout
        headers.forEachIndexed { index, headerText ->
            val headerCell = createTableCell(headerText,0, index, isHeader = true)
            binding.schedulePageScheduleTable.addView(headerCell)
        }

        // Add từng ô cho 10 hàng
        for (rowIndex in 1..NUMBER_OF_PERIOD) {
            for (colIndex in headers.indices) {
                val cellText = if (colIndex == 1) {
                    rowIndex.toString().padEnd(dpToPx(WIDTH_OF_TWOFIRST_COL)) // Hiển thị số thứ tự ở cột "Period"
                } else {
                    "".repeat(dpToPx(WIDTH_OF_OTHER_COL)) // Các ô khác để trống
                }

                val cell = createTableCell(cellText,rowIndex, colIndex)
                // Đặt layout params để xác định vị trí hàng và cột
                cell.layoutParams = GridLayout.LayoutParams().apply {
                    rowSpec = GridLayout.spec(rowIndex) // Vị trí hàng
                    columnSpec = GridLayout.spec(colIndex) // Vị trí cột
                }

                binding.schedulePageScheduleTable.addView(cell)
            }
        }
    }

    private fun createTableCell(text: String, rowNum :Int, columnNum: Int, isHeader: Boolean = false): TextView {
        return TextView(context).apply {
            setText(text)
            setPadding(24, 12, 24, 12)
            setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            gravity = android.view.Gravity.CENTER
            textSize = 16f


            if (isHeader) {
                setTypeface(null, android.graphics.Typeface.BOLD)
            }

            layoutParams = GridLayout.LayoutParams().apply {
                width = if (columnNum < 2) WIDTH_OF_TWOFIRST_COL else WIDTH_OF_OTHER_COL
                height = 150
                setMargins(2, 2, 2, 2)
                if (!isHeader){
                    setBackgroundColor(
                        if (rowNum % 2 == 0)
                            ContextCompat.getColor(requireContext(), R.color.table_row_even)
                        else
                            ContextCompat.getColor(requireContext(), R.color.table_row_odd)
                    )
                }
            }
        }
    }


    private fun updateTable(courses: List<Course>) {
        initializeTable()
        Log.e("course3", courses.toString())

        courses.forEach { course ->
            // Tính toán vị trí và phạm vi của đối tượng con
            val dayIndex = scheduleViewModel.DayOfWeek[course.Day!!] ?: return@forEach
            val startPeriod = course.StartPeriod ?: return@forEach
            val endPeriod = course.EndPeriod ?: return@forEach

            // Tính index dựa trên vị trí
            val index = startPeriod * NUMBER_OF_COLUMN + dayIndex
            Log.e("course4", index.toString())

            // Lấy đối tượng con tại vị trí index
            val child = binding.schedulePageScheduleTable.getChildAt(index) as TextView

            // Thay đổi nội dung
            child.setText(scheduleViewModel.formattedCourseForCell(course))
            child.setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.blue))

            // Cập nhật LayoutParams để span nhiều dòng
            val params = child.layoutParams as GridLayout.LayoutParams
            params.rowSpec = GridLayout.spec(startPeriod, endPeriod - startPeriod + 1) // Bắt đầu từ hàng và span
            params.columnSpec = GridLayout.spec(dayIndex, 1) // Đặt tại cột tương ứng
            child.layoutParams = params // Áp dụng LayoutParams mới
        }
    }

    fun dpToPx(dp: Int): Int {
        val density = Resources.getSystem().displayMetrics.density
        return (dp * density).toInt()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}