package com.example.login_portal.ui.requests

import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.collection.emptyLongSet
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.example.login_portal.BaseActivity
import com.example.login_portal.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.Tab
import com.google.gson.Gson

class RequestDetail : BaseActivity() {
    lateinit var item: RequestItem

    inner class FragmentAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int {
            return 2 // Number of fragments
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> RequestDetailContent(item)
                1 -> RequestDetailAnswer(item.RequestId) // Second Fragment
                else -> throw IllegalStateException("Unexpected position: $position")
            }
        }
    }

    lateinit var container: ViewPager2
    lateinit var tablayout: TabLayout
    lateinit var fab: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_request_detail)

        item = Gson().fromJson(intent.getStringExtra("REQUEST_DETAIL"), RequestItem::class.java)

        container = findViewById(R.id.request_detail_container)
        tablayout = findViewById(R.id.request_detail_tablayout)
        fab = findViewById(R.id.request_detail_operation)

        fab.setOnClickListener { v: View ->
            val popup = PopupMenu(this, v)
            val operations: MutableList<String> = mutableListOf()

            if (item.Status == resources.getString(R.string.request_status_processing))
                operations.addAll(listOf(
                    resources.getString(R.string.request_detail_cancel_op),
                    resources.getString(R.string.request_detail_goback)
                ))
            else
                operations.addAll(listOf(
                    resources.getString(R.string.request_detail_resend_op),
                    resources.getString(R.string.request_detail_goback)
                ))


            operations.forEachIndexed { index, title ->
                popup.menu.add(0, index, index, title)
            }

            popup.setOnMenuItemClickListener { menuItem ->
                if (menuItem.itemId == 0)
                {
                    if (menuItem.title == resources.getString(R.string.request_detail_cancel_op)) {
                        RequestDao.cancel(item.RequestId, this)
                    }
                    else {
                        RequestDao.resend(item, this)
                    }
                    finish()
                }
                else
                    finish()
                true
            }
            popup.setOnDismissListener { }

            popup.show()
        }

        val adapter = FragmentAdapter(this)
        container.adapter = adapter
        container.registerOnPageChangeCallback(object: OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tablayout.selectTab(tablayout.getTabAt(position))
            }
        })

        tablayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: Tab?) {
                if (tab != null) {
                    container.currentItem = tab.position
                }
            }

            override fun onTabUnselected(tab: Tab?) { }

            override fun onTabReselected(tab: Tab?) { }
        })
    }
}