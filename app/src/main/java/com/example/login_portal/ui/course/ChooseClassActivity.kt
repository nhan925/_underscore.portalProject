package com.example.login_portal.ui.course

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.collection.emptyLongSet
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.login_portal.BaseActivity
import com.example.login_portal.R
import com.example.login_portal.ui.requests.RequestDao
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
    lateinit var fab: FloatingActionButton

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
        fab = findViewById(R.id.choose_class_fab)

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

                if (data.RegisteredClassId.isNullOrEmpty())
                    adapter.resetSource(data.Classes)
                else
                    adapter.resetSource(data.Classes, data.Classes.indexOfFirst { it.Id == data.RegisteredClassId})
            })
        }

        goBackBtn.setOnClickListener { finish() }

        loadingOverlay.bringToFront()
        refreshBtn.setOnClickListener {
            loadingOverlay.visibility = View.VISIBLE
            viewModel.reset { loadingOverlay.visibility = View.INVISIBLE }
        }

        fab.setOnClickListener { v: View ->
            val popup = PopupMenu(this, v)

            val operations = mutableListOf(getString(R.string.choose_class_register))

            if (intent.getStringExtra("STATUS")!! == getString(R.string.choose_couse_status_registered))
                operations.add(1, getString(R.string.choose_class_cancel))

            operations.forEachIndexed { index, title ->
                popup.menu.add(0, index, index, title)
            }

            popup.setOnMenuItemClickListener { menuItem ->
                if (menuItem.itemId == 0) // register
                {
                    if ((recyclerView.adapter as ClassItemAdapter).selectedPosition == RecyclerView.NO_POSITION) {
                        Toast.makeText(this, getString(R.string.choose_class_not_selected_alert), Toast.LENGTH_SHORT).show()
                        return@setOnMenuItemClickListener true
                    }

                    if (viewModel.info.value?.Classes?.get((recyclerView.adapter as ClassItemAdapter).selectedPosition)?.Id
                        == viewModel.info.value?.RegisteredClassId) {
                        Toast.makeText(this, getString(R.string.choose_class_registered_alert), Toast.LENGTH_SHORT).show()
                        return@setOnMenuItemClickListener true
                    }

                    val registerClass: () -> Unit = {
                        val _class = viewModel.info.value?.Classes?.get((recyclerView.adapter as ClassItemAdapter).selectedPosition)
                        loadingOverlay.visibility = View.VISIBLE
                        viewModel.registerClass(_class!!.Id, viewModel.info.value?.RegisteredClassId) { response ->
                            val message: String
                            when (response) {
                                "DUP" -> {
                                    message = getString(R.string.choose_class_register_duplicate_time)
                                }
                                "NOSLOT" -> {
                                    message = getString(R.string.choose_class_register_limit)
                                }
                                "OK" -> {
                                    message = getString(R.string.choose_class_register_success)
                                    viewModel.status = getString(R.string.choose_couse_status_registered)
                                }
                                else -> {
                                    message = getString(R.string.choose_class_register_error)
                                }
                            }
                            viewModel.reset {
                                loadingOverlay.visibility = View.INVISIBLE
                                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    if (viewModel.status == getString(R.string.choose_couse_status_studied)) {
                        MaterialAlertDialogBuilder(this)
                            .setTitle(getString(R.string.choose_class_dialog_title))
                            .setMessage(resources.getString(R.string.choose_class_dialog_message))
                            .setNegativeButton(resources.getString(R.string.choose_class_dialog_decline)) { dialog, which ->
                                dialog.cancel()
                            }
                            .setPositiveButton(resources.getString(R.string.choose_class_accept)) { dialog, which ->
                                registerClass.invoke()
                            }
                            .show()
                    }
                    else {
                        registerClass.invoke()
                    }
                }
                else { // unregister
                    loadingOverlay.visibility = View.VISIBLE
                    viewModel.cancelClass(viewModel.info.value?.RegisteredClassId!!) { response ->
                        when (response) {
                            "OK" -> {
                                Toast.makeText(this, getString(R.string.choose_class_cancel_success), Toast.LENGTH_SHORT).show()
                                finish()
                            }
                            else -> {
                                loadingOverlay.visibility = View.INVISIBLE
                                Toast.makeText(this, getString(R.string.choose_class_cancel_failed), Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                true
            }
            popup.setOnDismissListener { }

            popup.show()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.reset { }
    }
}