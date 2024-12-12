package com.example.login_portal.ui.setting

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.login_portal.MainActivity2
import com.example.login_portal.R
import com.example.login_portal.databinding.FragmentScheduleBinding
import com.example.login_portal.databinding.FragmentSettingsBinding
import com.example.login_portal.ui.schedule.ScheduleViewModel
import com.example.login_portal.utils.ApiServiceHelper
import com.example.login_portal.utils.CallApiLogin
import com.example.login_portal.utils.LanguageManager
import com.example.login_portal.utils.SecurePrefManager


class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private lateinit var securePrefManager: SecurePrefManager
    private lateinit var languageManager: LanguageManager
    private var currentLanguage = "en"

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val scheduleViewModel =
            ViewModelProvider(this).get(SettingsViewModel::class.java)
        //languageManager = LanguageManager(this)
        currentLanguage = languageManager.getCurrentLanguage()

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        binding.btnLogout.setOnClickListener {
            logout()
        }

       setupLanguageSwitch()


        return binding.root
    }

    private fun setupLanguageSwitch() {
        val languageSwitchLayout = binding.languageSwitch
        val languageFlag = binding.languageFlag
        val languageText = binding.languageText

        languageManager.updateLanguageUI(languageFlag, languageText, currentLanguage)

        languageSwitchLayout.setOnClickListener {
            currentLanguage = languageManager.toggleLanguage(languageFlag, languageText)

            activity?.let { currentActivity ->
                languageManager.updateLanguageUI(languageFlag, languageText, currentLanguage)
                currentActivity.recreate()
            }
        }
    }



    private fun logout() {
        CallApiLogin.signOut { msalSignOutSuccess ->
            // Xóa dữ liệu người dùng trong local
            securePrefManager.clearUserData()




            // Chuyển về màn hình đăng nhập
            activity?.runOnUiThread {
                val intent = Intent(requireContext(), MainActivity2::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Đảm bảo xóa stack trước đó
                startActivity(intent)
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        languageManager = LanguageManager(requireContext())
        securePrefManager = SecurePrefManager(requireContext())
    }

}