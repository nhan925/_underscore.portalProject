package com.example.login_portal.ui.dashboard

import CustomMarkerView
import PieChartMarkerView
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.airbnb.lottie.LottieAnimationView
import com.example.login_portal.R
import com.example.login_portal.databinding.FragmentDashboardBinding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.components.XAxis

import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener


private fun Float.toAcademicRank(context: Context): String = when {
    this >= 9.0f -> context.getString(R.string.inf55)
    this >= 8.0f -> context.getString(R.string.inf51)
    this >= 7.0f -> context.getString(R.string.inf52)
    this >= 5.0f -> context.getString(R.string.inf53)
    else -> context.getString(R.string.inf54)
}


private fun Float.toGPAColor(): Int = when {
    this >= 9.0f -> Color.rgb(0, 150, 136)    // Xanh lá - Xuất sắc
    this >= 8.0f -> Color.rgb(33, 150, 243)   // Xanh dương - Giỏi
    this >= 7.0f -> Color.rgb(255, 193, 7)    // Vàng - Khá
    this >= 5.0f -> Color.rgb(255, 152, 0)    // Cam - Trung bình
    else -> Color.rgb(244, 67, 54)            // Đỏ - Kém
}

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: DashboardViewModel
    private lateinit var pieChart: PieChart
    private lateinit var barChart: BarChart
    private lateinit var animationView: LottieAnimationView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.animationView.setAnimation(R.raw.ani_user)
        binding.animationView.playAnimation()


        pieChart = binding.pieChart
        barChart = binding.barChart


        // Loading state
        binding.loadingProgressBar.visibility = View.VISIBLE
        //binding.contentLayout.visibility = View.GONE

        viewModel.dashboardData.observe(viewLifecycleOwner) { data ->
            // Hide loading state
            binding.loadingProgressBar.visibility = View.GONE
            //binding.contentLayout.visibility = View.VISIBLE
            
            binding.studentName.text = data.studentFullName
            setupPieChart(data.currentCredit, data.totalCredit)
            setupBarChart(data.semesters)
        }

        navigateFragment()


    }

    private fun setupPieChart(currentCredit: Int, totalCredit: Int) {
        pieChart.apply {
            description.isEnabled = false
            isDrawHoleEnabled = false
            setHoleColor(Color.WHITE)
            holeRadius = 58f
            transparentCircleRadius = 61f
            setDrawCenterText(true)
            //centerText = "Tiến độ học tập"
            setCenterTextSize(16f)
            setUsePercentValues(true)
            setDrawEntryLabels(false)
            animateY(1400)
            legend.isEnabled = true

            val markerView = PieChartMarkerView(
                context = requireContext(),
                layoutResource = R.layout.custom_marker_view,
                totalCredit = totalCredit
            )
            marker = markerView

            setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    if (e is PieEntry) {
                        highlightValue(h)
                    }
                }

                override fun onNothingSelected() {
                    highlightValue(null)
                }
            })
        }

        val remainingCredits = totalCredit - currentCredit
        Log.e("DashboardFragment", "Remaining credits: $remainingCredits")
        Log.e("DashboardFragment", "Total credits: $totalCredit")
        Log.e("DashboardFragment", "Current credits: $currentCredit")

        val pieEntries = ArrayList<PieEntry>().apply {
            add(PieEntry(currentCredit.toFloat(), getString(R.string.inf49)))
            add(PieEntry(remainingCredits.toFloat(), getString(R.string.inf50)))
        }

        val pieDataSet = PieDataSet(pieEntries,"").apply {
            colors = listOf(
                Color.rgb(64, 89, 230),
                Color.rgb(149, 165, 244)
            )

        }




        pieChart.data = PieData(pieDataSet)
        pieChart.invalidate()
    }

    private fun setupBarChart(semesters: List<DashboardViewModel.Semester>) {
        barChart.apply {
//            description.apply {
//               isEnabled = true //text = "GPA"
//              textSize = 12f
//                textColor = Color.rgb(64, 89, 230)
//                yOffset = 15f
//                xOffset = 5f
//               textAlign = Paint.Align.CENTER
//
//           }
            description.isEnabled = false

            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(false)

            // Giảm margin
            setExtraOffsets(0f, 10f, 15f, 5f)

            xAxis.apply {
                setDrawGridLines(false)
                granularity = 1f
                position = XAxis.XAxisPosition.BOTTOM
                textSize = 8f
                labelRotationAngle = 55f
            }

            axisLeft.apply {
                axisMinimum = 0f
                axisMaximum = 10f
                granularity = 1f
                setDrawGridLines(true)
            }

            axisRight.isEnabled = false
            legend.isEnabled = false
            animateY(1000)

            // Tắt hiển thị giá trị mặc định trên cột
            setDrawValueAboveBar(false)

            // Xử lý khi nhấn vào cột
            setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    e?.let { entry ->
                        val index = entry.x.toInt()
                        val semester = semesters[index]
                        val rank = semester.gpa.toAcademicRank(requireContext())
                        // Hiển thị tooltip với GPA và xếp loại
                        val markerView = CustomMarkerView(
                            context = requireContext(),
                            layoutResource = R.layout.custom_marker_view,
                            semester = semester,
                            rank = rank
                        )
                        marker = markerView
                        highlightValue(h)
                    }


            }

                override fun onNothingSelected() {
                    highlightValue(null)
                }
            })
        }

        val barEntries = semesters.mapIndexed { index, semester ->
            BarEntry(index.toFloat(), semester.gpa)
        }

        val barDataSet = BarDataSet(barEntries, "GPA").apply {
            colors = semesters.map { it.gpa.toGPAColor() }
            valueTextSize = 12f
            setDrawValues(false)
        }

        val barData = BarData(barDataSet).apply {
            barWidth = 0.65f
        }

        barChart.data = barData
        barChart.invalidate()
    }

    private fun navigateFragment()
    {
        binding.CVDashboard.setOnClickListener()
        {
            findNavController().navigate(R.id.action_nav_Dashboard_to_nav_InforStudent)
        }

        binding.CVScore.setOnClickListener(){
            findNavController().navigate(R.id.action_nav_Dashboard_to_nav_Score)
        }



        binding.CVCourses.setOnClickListener(){
            findNavController().navigate(R.id.action_nav_Dashboard_to_nav_course)
        }

        binding.CVTimeline.setOnClickListener(){
            findNavController().navigate(R.id.action_nav_Dashboard_to_nav_Schedule)
        }

        binding.CVTuition.setOnClickListener(){
            findNavController().navigate(R.id.action_nav_Dashboard_to_nav_Tuition)

        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadDashboardData()
    }
}