package com.example.login_portal.ui.course

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.login_portal.BaseActivity
import com.example.login_portal.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ChooseClassActivity : BaseActivity() {
    lateinit var viewModel: ChooseClassViewModel
    lateinit var courseIdTV: TextView
    lateinit var numClassesTV: TextView
    lateinit var courseNameTV: TextView
    lateinit var statusTV: TextView
    lateinit var goBackBtn: Button
    lateinit var recyclerView: RecyclerView
    lateinit var refreshBtn: FloatingActionButton
    lateinit var loadingOverlay: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_choose_class)

        courseIdTV = findViewById(R.id.choose_class_course_id)
        numClassesTV = findViewById(R.id.choose_class_num_classes)
        courseNameTV = findViewById(R.id.choose_class_course_name)
        statusTV = findViewById(R.id.choose_class_status)
        goBackBtn = findViewById(R.id.choose_class_back_btn)
        recyclerView = findViewById(R.id.choose_class_recycler_view)
        refreshBtn = findViewById(R.id.choose_class_reload_fab)
        loadingOverlay = findViewById(R.id.choose_class_loading_overlay)

        recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = ClassItemAdapter(listOf(), this)
        recyclerView.adapter = adapter

        val intent = intent
        viewModel = ChooseClassViewModel(
            intent.getStringExtra("COURSE_ID")!!,
            intent.getIntExtra("PERIOD_ID", 0),
            intent.getStringExtra("COURSE_NAME")!!,
            intent.getStringExtra("STATUS")!!
        )

        viewModel.reset {
            viewModel.info.observe(this,  Observer { data ->
                courseIdTV.text = data.CourseId
                courseNameTV.text = data.CourseName
                numClassesTV.text = data.NumberOfClasses.toString()
                statusTV.text = data.Status

                adapter.resetSource(data.Classes)
            })
        }

        goBackBtn.setOnClickListener { finish() }

        loadingOverlay.bringToFront()
        refreshBtn.setOnClickListener {
            loadingOverlay.visibility = View.VISIBLE
            viewModel.reset { loadingOverlay.visibility = View.INVISIBLE }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.reset { }
    }
}