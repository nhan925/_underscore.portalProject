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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.example.login_portal.ui.notification.NotificationViewModel
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.login_portal.ui.notification.NotificationAdapter
import com.example.login_portal.ui.notification.NotificationDetailActivity

class Main : BaseActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMain3Binding
    private val notificationViewModel: NotificationViewModel by viewModels()
    var imgAvatar :ImageView? = null
    var txtUserName :TextView? = null
    var txtSchoolEmail :TextView? = null
    private var job: Job? = null
    private val detailActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val notificationId = data?.getIntExtra("notification_id", -1) ?: -1
                val action = data?.getStringExtra("action") ?: return@registerForActivityResult

                when (action) {
                    "mark_seen" -> notificationViewModel.markAsSeen(notificationId)
                    "unmark_important" -> notificationViewModel.unmarkAsImportant(notificationId)
                    "mark_important" -> notificationViewModel.markAsImportant(notificationId)
                    "delete" -> notificationViewModel.deleteNotification(notificationId)
                }
            }
        }
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
                R.id.nav_course, R.id.nav_Request, R.id.nav_Feedback_System, R.id.nav_Feedback_Course,
                R.id.nav_Payment_History, R.id.nav_Scholarship
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

        // Bell click listener
        binding.appBarMain.notificationBell.setOnClickListener {
            showNotificationPopup(this)
        }

        // Observe notifications for unread status
        notificationViewModel.notifications.observe(this) { notifications ->
            val unreadCount = notifications.count { !it.isSeen }
            toggleRedDot(unreadCount > 0)
        }
    }

    private fun toggleRedDot(shouldShow: Boolean) {
        binding.appBarMain.redDot.visibility = if (shouldShow) View.VISIBLE else View.GONE
    }

    private fun showNotificationPopup(context: Context) {
        val inflater = LayoutInflater.from(context)
        val popupView = inflater.inflate(R.layout.pop_up_notifications, null)
        val popupWindow = PopupWindow(
            popupView,
            (resources.displayMetrics.widthPixels * 0.75).toInt(),
            (resources.displayMetrics.heightPixels * 0.75).toInt(),
            true
        )

        val recyclerView = popupView.findViewById<RecyclerView>(R.id.notification_list)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val adapter = NotificationAdapter(mutableListOf()) { notification ->
            val intent = Intent(context, NotificationDetailActivity::class.java).apply {
                putExtra("notification_id", notification.id)
                putExtra("notification_title", notification.title)
                putExtra("notification_sender", notification.sender)
                putExtra("notification_time", notification.time)
                putExtra("notification_detail", notification.detail)
                putExtra("is_marked_as_important", notification.isMarkedAsImportant) // Truyền trạng thái
            }
            popupWindow.dismiss()
            detailActivityLauncher.launch(intent)
        }
        recyclerView.adapter = adapter

        notificationViewModel.notifications.observe(this) { notifications ->
            adapter.updateNotifications(notifications)
        }

        val viewAllButton = popupView.findViewById<TextView>(R.id.view_all_button)
        viewAllButton.setOnClickListener {
            val navController = findNavController(R.id.nav_host_fragment_content_main)
            navController.navigate(R.id.nav_Notification)
            popupWindow.dismiss()
        }

        popupWindow.setBackgroundDrawable(ContextCompat.getDrawable(context, android.R.color.transparent))
        popupWindow.showAsDropDown(binding.appBarMain.notificationBell, 0, -binding.appBarMain.notificationBell.height)
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