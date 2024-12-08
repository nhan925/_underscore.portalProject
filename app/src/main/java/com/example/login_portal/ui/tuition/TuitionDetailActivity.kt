package com.example.login_portal.ui.tuition

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.login_portal.BaseActivity
import com.example.login_portal.R


class TuitionDetailActivity : BaseActivity() {

    private lateinit var yearTV : TextView
    private lateinit var semesterTV : TextView
    private lateinit var numberOfSubjectTV: TextView
    private lateinit var totalFeeTV: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var backButton: Button
    private lateinit var payButton: Button
    private val viewModel = TuitionViewModel.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tuition_detail)

        yearTV = findViewById(R.id.tuition_detail_year)
        semesterTV = findViewById(R.id.tuition_detail_semester)
        numberOfSubjectTV = findViewById(R.id.tuition_detail_number_of_subject)
        totalFeeTV = findViewById(R.id.tuition_detail_total_fee)
        backButton = findViewById(R.id.tuition_detail_back_button)
        payButton = findViewById(R.id.tuition_detail_pay_button)

        yearTV.text = intent.getStringExtra("year")
        semesterTV.text = intent.getIntExtra("semester", 0).toString()
        numberOfSubjectTV.text = intent.getIntExtra("totalCourse", 0).toString()
        totalFeeTV.text = intent.getIntExtra("totalFee", 0).toString()

        val adapter = AdapterForTuitionDetail(emptyList())
        recyclerView = findViewById(R.id.tuition_detail_recycler)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel.tuitionDetail.observe(this) { tuitionDetailList ->
            adapter.updateData(tuitionDetailList)
        }

        viewModel.getTuitionDetailListFromDatabase(yearTV.text.toString(), semesterTV.text.toString().toInt())

        payButton.setOnClickListener {
            Toast.makeText(this, "Pay successfully", Toast.LENGTH_SHORT).show()
        }

        backButton.setOnClickListener {
            finish()
        }
    }


}