package com.example.login_portal

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.example.login_portal.databinding.ActivityMain3Binding
import com.example.login_portal.ui.information.InformationsForInformationDao
import kotlinx.coroutines.*

class Main : BaseActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMain3Binding
    var imgAvatar :ImageView? = null
    var txtUserName :TextView? = null
    var txtSchoolEmail :TextView? = null
    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMain3Binding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val headerView = navView.getHeaderView(0)
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        imgAvatar = headerView.findViewById<ImageView>(R.id.nav_header_main_student_avatar)
        txtUserName = headerView.findViewById<TextView>(R.id.nav_header_main_student_fullname)
        txtSchoolEmail = headerView.findViewById<TextView>(R.id.nav_header_main_student_school_email)
        setHeaderInformation()

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_Dashboard, R.id.nav_Notification, R.id.nav_Setting,
                R.id.nav_Score, R.id.nav_Schedule, R.id.nav_Tuition, R.id.nav_InforStudent,
                R.id.nav_course, R.id.nav_Request, R.id.nav_Feedback_System, R.id.nav_Feedback_Course
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        job = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                setHeaderInformation() // Call the method
                delay(10000L) // Wait 10 seconds
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setHeaderInformation()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun setHeaderInformation(){
        InformationsForInformationDao.getInformation { data ->
            Glide.with(this)
                .load(data.AvatarUrl)
                .placeholder(R.drawable.baseline_person_24)
                .error(R.drawable.baseline_person_24)
                .into(imgAvatar!!)
            txtUserName!!.text = data.FullName
            txtSchoolEmail!!.text = data.SchoolEmail
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
    }
}