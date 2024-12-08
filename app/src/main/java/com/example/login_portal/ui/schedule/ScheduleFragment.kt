package com.example.login_portal.ui.schedule

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.FILL_PARENT
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TableRow
import android.widget.TextView
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.setMargins
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.login_portal.R
import com.example.login_portal.databinding.FragmentScheduleBinding
import androidx.gridlayout.widget.GridLayout
import com.google.android.material.bottomsheet.BottomSheetDialog


class ScheduleFragment : Fragment() {
    private lateinit var scheduleViewModel : ScheduleViewModel
    private var _binding: FragmentScheduleBinding? = null
    private val NUMBER_OF_COLUMN = 8
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

        creatNoteForSession()
        setupSemesterSpinner()
        createBackgroundTable()
        createForegroundTable()
        initializeTable()
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

    private fun creatNoteForSession(){
        binding.schedulePageMorningSession.setText(getString(R.string.schedule_page_morning))
        binding.schedulePageAfternoonSession.setText(getString(R.string.schedule_page_afternoon))
    }

    private fun createBackgroundTable(){
        val NUMBER_OF_PERIOD = 10
        val emptyRow = TableRow(context).apply {
            for (index in 0..NUMBER_OF_COLUMN) {
                addView(createTableCell("", 0, index, true, true))
            }
        }
        binding.schedulePageBackgroundGrid.addView(emptyRow)

        for (rowIndex in 1..NUMBER_OF_PERIOD) {
            val row = TableRow(context).apply {
            for (colIndex in 0..NUMBER_OF_COLUMN) {
                val cellText = ""
                addView(createTableCell(cellText,rowIndex,colIndex,false,true))
            }
                setBackgroundColor(
                    if (rowIndex % 2 == 0)
                        ContextCompat.getColor(requireContext(), R.color.table_row_even)
                    else
                        ContextCompat.getColor(requireContext(), R.color.table_row_odd)
                )
            }
            binding.schedulePageBackgroundGrid.addView(row)
        }
    }

    private fun createForegroundTable(){
        val NUMBER_OF_PERIOD = 10
        val headers = arrayOf(
            getString(R.string.schedule_page_period),
        )

        val headerRow = TableRow(context).apply {
            headers.forEachIndexed { index,headerText ->
                addView(createTableCell(headerText, 0,index,true,true))
            }
            setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.purple))
        }

        binding.schedulePageForegroundGrid .addView(headerRow)

        for (rowIndex in 1..NUMBER_OF_PERIOD) {
            val row = TableRow(context).apply {
                val cellText = rowIndex.toString()
                addView(createTableCell(cellText,rowIndex,0,false,true))
                setBackgroundColor(
                    if (rowIndex in 1..5)
                        ContextCompat.getColor(requireContext(), R.color.polygon_4_color)
                    else
                        ContextCompat.getColor(requireContext(), R.color.yellow)
                )
            }
            binding.schedulePageForegroundGrid.addView(row)
        }
    }

    private fun initializeTable() {
        val NUMBER_OF_PERIOD = 10
        val headers = arrayOf(
            //getString(R.string.schedule_page_session),
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
                val cellText = if (colIndex == 0) {
                    rowIndex.toString()
                } else {
                    ""
                }

                val cell = createTableCell(cellText,rowIndex, colIndex)
                val params = cell.layoutParams as? GridLayout.LayoutParams ?: GridLayout.LayoutParams()
                params.apply {
                    rowSpec = GridLayout.spec(rowIndex)
                    columnSpec = GridLayout.spec(colIndex)
                }
                cell.layoutParams = params

                binding.schedulePageScheduleTable.addView(cell)
            }
        }
    }

    private fun createTableCell(
        text: String,
        rowNum: Int,
        columnNum: Int,
        isHeader: Boolean = false,
        useTableRowParams: Boolean = false // Thêm tham số để quyết định loại LayoutParams
    ): TextView {
        return TextView(context).apply {
            setText(text)
            setPadding(16,16,16,16)
            setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
            gravity = Gravity.CENTER
            textSize = 16f

            if (isHeader) {
                setTypeface(null, android.graphics.Typeface.BOLD)
                setBackgroundColor(ContextCompat.getColor(requireContext(),R.color.light_purple))
                setGravity(FILL_PARENT)
            }

            // Tùy chọn loại LayoutParams
            layoutParams = if (useTableRowParams) {
                TableRow.LayoutParams().apply {
                    width = if (columnNum == 0 ) TableRow.LayoutParams.WRAP_CONTENT else WIDTH_OF_OTHER_COL
                    height = 150
                }
            } else {
                GridLayout.LayoutParams().apply {
                    width = if (columnNum == 0) GridLayout.LayoutParams.WRAP_CONTENT else WIDTH_OF_OTHER_COL
                    height = 150
                    rowSpec = GridLayout.spec(rowNum, 1)
                    columnSpec = GridLayout.spec(columnNum, 1)
                }
            }
            if (text!= "") {
                background = borderCell(context,ContextCompat.getColor(context, android.R.color.darker_gray))
            }
        }
    }



    private fun updateTable(courses: List<Course>) {
        binding.schedulePageScheduleTable.removeAllViews()
        initializeTable()
        Log.e("course3", courses.toString())

        courses.forEach { course ->
            // Tính toán vị trí và phạm vi của đối tượng con
            val dayIndex = scheduleViewModel.DayOfWeek[course.Day!!] ?: return@forEach
            val startPeriod = course.StartPeriod ?: return@forEach
            val endPeriod = course.EndPeriod ?: return@forEach
            Log.e("GridLayoutDebug", "startPeriod: $startPeriod, endPeriod: $endPeriod, dayIndex: $dayIndex")

            val newCell = createTableCell(
                text = scheduleViewModel.formattedCourseForCell(course),
                rowNum = startPeriod,
                columnNum = dayIndex
            )

            val params = newCell.layoutParams as? GridLayout.LayoutParams ?: GridLayout.LayoutParams()
            params.apply {
                rowSpec = GridLayout.spec(startPeriod, endPeriod - startPeriod + 1) // Span nhiều dòng
                columnSpec = GridLayout.spec(dayIndex, 1)
                setGravity(FILL_PARENT)
            }
            newCell.layoutParams = params
            val backgroundColor = ContextCompat.getColor(requireContext(), R.color.blue)
            val border = borderCell(newCell.context,ContextCompat.getColor(newCell.context, android.R.color.black))
            val drawable = LayerDrawable(arrayOf(ColorDrawable(backgroundColor), border))
            newCell.background = drawable
            newCell.setOnClickListener{
                createBottomSheetDialog(newCell.context,course)
            }

            // Thêm ô vào GridLayout
            binding.schedulePageScheduleTable.addView(newCell)
        }
    }

    fun dpToPx(dp: Int): Int {
        val density = Resources.getSystem().displayMetrics.density
        return (dp * density).toInt()
    }

    fun borderCell(context: Context, color: Int) : GradientDrawable{
        val border = GradientDrawable().apply {
            setStroke(2, color) // Viền
        }
        return border
    }

    fun createBottomSheetDialog(context: Context, course : Course){
        val dialog = BottomSheetDialog(context)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog_schedule, null)

        view.findViewById<TextView>(R.id.bsd_schedule_Lecturername_value).text = course.Lecturer?.FullName?: ""
        view.findViewById<TextView>(R.id.bsd_schedule_Gender_value).text = course.Lecturer?.Gender ?: ""
        view.findViewById<TextView>(R.id.bsd_schedule_Phone_value).text = course.Lecturer?.PhoneNumber ?: ""
        view.findViewById<TextView>(R.id.bsd_schedule_Email_value).text = course.Lecturer?.Email ?: ""
        view.findViewById<TextView>(R.id.bsd_schedule_AcademicRank_value).text = course.Lecturer?.AcademicRank ?: ""
        view.findViewById<TextView>(R.id.bsd_schedule_AcademicDegree_value).text = course.Lecturer?.AcademicDegree ?: ""
        view.findViewById<TextView>(R.id.bsd_schedule_Faculty_value).text = course.Lecturer?.FacultyName ?: ""
        val textView = view.findViewById<TextView>(R.id.bsd_schedule_course_url_value)
        textView.setOnClickListener {
            val url = course.CourseUrl
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }

        val btnClose = view.findViewById<ImageButton>(R.id.bsd_schedule_btn_close)
        btnClose.setOnClickListener {
            dialog.dismiss()
        }
        dialog.setCancelable(false)

        dialog.setContentView(view)
        dialog.show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}