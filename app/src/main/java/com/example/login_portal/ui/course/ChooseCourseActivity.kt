package com.example.login_portal.ui.course

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.example.login_portal.BaseActivity
import com.example.login_portal.R
import com.example.login_portal.ui.requests.RequestDetailAnswer
import com.example.login_portal.ui.requests.RequestDetailContent
import com.example.login_portal.ui.requests.RequestItem
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.Tab
import kotlin.math.max

class ChooseCourseActivity : BaseActivity() {
    private lateinit var viewModel: ChooseCourseViewModel

    inner class FragmentAdapter(
        fragmentActivity: FragmentActivity,
        var unregisteredFragment: UnregisteredFragment,
        var studiedFragment: StudiedFragment,
        var registeredFragment: RegisteredFragment

    ) : FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int {
            return 3
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> unregisteredFragment
                1 -> studiedFragment
                2 -> registeredFragment
                else -> throw IllegalStateException("Unexpected position: $position")
            }
        }
    }

    lateinit var majorTV: TextView
    lateinit var maxCreditsTV: TextView
    lateinit var registeredCoursesTV: TextView
    lateinit var registeredCreditsTV: TextView
    lateinit var container: ViewPager2
    lateinit var tabLayout: TabLayout
    lateinit var backBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_choose_course)

        val intent = intent
        viewModel = ChooseCourseViewModel(intent.getIntExtra("PERIOD_ID", 0))

        majorTV = findViewById(R.id.choose_course_act_major)
        maxCreditsTV = findViewById(R.id.choose_course_act_max_credits)
        registeredCoursesTV = findViewById(R.id.choose_course_act_registered_courses)
        registeredCreditsTV = findViewById(R.id.choose_course_act_registered_credits)
        container = findViewById(R.id.choose_course_act_container)
        tabLayout = findViewById(R.id.choose_course_tablayout)
        backBtn = findViewById(R.id.choose_course_back_btn)

        viewModel.reset {
            val clickable = viewModel.info.value?.RegisteredCredits!! < viewModel.info.value?.MaxCredits!!
            val unregisteredAdapter = CourseItemAdapter(listOf(), this, clickable = clickable)
            val registeredAdapter = CourseItemAdapter(listOf(), this, true)
            val studiedAdapter = CourseItemAdapter(listOf(), this, clickable = clickable)

            viewModel.info.observe(this, Observer { data ->
                majorTV.text = data.Major
                maxCreditsTV.text = data.MaxCredits.toString()
                registeredCoursesTV.text = data.RegisteredCourses.size.toString()
                registeredCreditsTV.text = data.RegisteredCredits.toString()

                unregisteredAdapter.resetSource(data.UnregisteredCourses)
                registeredAdapter.resetSource(data.RegisteredCourses)
                studiedAdapter.resetSource(data.StudiedCourses)
            })

            val adapter = FragmentAdapter(this,
                UnregisteredFragment(unregisteredAdapter),
                StudiedFragment(studiedAdapter),
                RegisteredFragment(registeredAdapter))
            container.adapter = adapter

            val startChooseClassActivity: (Course, String) -> Unit = { course, status ->
                val _intent = Intent(this, ChooseClassActivity::class.java)
                _intent.putExtra("COURSE_ID", course.Id)
                _intent.putExtra("PERIOD_ID", viewModel.periodId)
                _intent.putExtra("COURSE_NAME", course.Name)
                _intent.putExtra("STATUS", status)
                startActivity(_intent)
            }

            unregisteredAdapter.onItemClick = { item ->
                if (viewModel.info.value?.RegisteredCredits!! + item.Credits > viewModel.info.value?.MaxCredits!!) {
                    Toast.makeText(this, getString(R.string.choose_course_alert), Toast.LENGTH_SHORT).show()
                }
                else {
                    startChooseClassActivity.invoke(item, getString(R.string.choose_couse_status_unregistered))
                }
            }

            registeredAdapter.onItemClick = { item ->
                startChooseClassActivity.invoke(item, getString(R.string.choose_couse_status_registered))
            }

            studiedAdapter.onItemClick = { item ->
                if (viewModel.info.value?.RegisteredCredits!! + item.Credits > viewModel.info.value?.MaxCredits!!) {
                    Toast.makeText(this, getString(R.string.choose_course_alert), Toast.LENGTH_SHORT).show()
                }
                else {
                    startChooseClassActivity.invoke(item, getString(R.string.choose_couse_status_studied))
                }
            }
        }

        container.registerOnPageChangeCallback(object: OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tabLayout.selectTab(tabLayout.getTabAt(position))
            }
        })

        tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: Tab?) {
                if (tab != null) {
                    container.currentItem = tab.position
                }
            }

            override fun onTabUnselected(tab: Tab?) { }

            override fun onTabReselected(tab: Tab?) { }
        })

        backBtn.setOnClickListener { finish() }
    }

    override fun onResume() {
        super.onResume()
        viewModel.reset { }
    }
}