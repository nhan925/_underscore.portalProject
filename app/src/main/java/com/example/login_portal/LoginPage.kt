package com.example.login_portal

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import com.example.login_portal.utils.LanguageManager
import com.example.login_portal.utils.SecurePrefManager
import com.example.login_portal.utils.Validator
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textfield.TextInputEditText

class MainActivity2 : BaseActivity() {
    private lateinit var languageManager: LanguageManager
    private var currentLanguage = "en"
    private var isFragmentVisible = false

    private lateinit var sercurePrefManager: SecurePrefManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sercurePrefManager = SecurePrefManager(this)
        if (sercurePrefManager.getUserData() != null) {
            // move to homepage
            navigateHome()
            return
        }
//        val sharedPreferences = getSharedPreferences("appPreferences", Context.MODE_PRIVATE)
//        currentLanguage = sharedPreferences.getString("appLanguage", "en") ?: "en"
//        updateLocale(currentLanguage)
        languageManager = LanguageManager(this)
        currentLanguage = languageManager.getCurrentLanguage()
        //languageManager.updateLocale(currentLanguage)

        setContentView(R.layout.activity_main2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val loginButton = findViewById<ExtendedFloatingActionButton>(R.id.loginButton)
        val registerButton = findViewById<ExtendedFloatingActionButton>(R.id.forgotButton)
        val fragmentContainer = findViewById<androidx.fragment.app.FragmentContainerView>(R.id.fragmentContainer)

        setupLoginButtonListener()

        loginButton.setOnClickListener {
            switchToLogin(loginButton, registerButton)
        }

        registerButton.setOnClickListener {
            switchToRegister(loginButton, registerButton)
        }


        val languageSwitchLayout = findViewById<LinearLayout>(R.id.languageSwitch)
        val languageFlag = findViewById<ImageView>(R.id.languageFlag)
        val languageText = findViewById<TextView>(R.id.languageText)

        languageManager.updateLanguageUI(languageFlag, languageText, currentLanguage)

        languageSwitchLayout.setOnClickListener {
            //toggleLanguage(this, languageFlag, languageText)
            currentLanguage = languageManager.toggleLanguage(languageFlag, languageText)
            //recreate()
           // AppUtils.restartApp(this)
        }

        if (savedInstanceState != null) {
            isFragmentVisible = savedInstanceState.getBoolean("isFragmentVisible", false)
            if (isFragmentVisible) {
                switchToRegister(loginButton, registerButton)
            }
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("isFragmentVisible", isFragmentVisible)
    }


    // navigation to HomePage in here
    private fun switchToLogin(loginButton: ExtendedFloatingActionButton, registerButton: ExtendedFloatingActionButton) {
        try {
            supportFragmentManager.popBackStack() // Close fragment if it's open
            isFragmentVisible = false

            val btnLogin = findViewById<ExtendedFloatingActionButton>(R.id.btn_login_2)
            val rememberMeCheckbox = findViewById<CheckBox>(R.id.rememberMeCheckbox)

            findViewById<View>(R.id.textView3).visibility = View.VISIBLE
            findViewById<View>(R.id.textView4).visibility = View.VISIBLE
            findViewById<View>(R.id.textView5).visibility = View.VISIBLE
            rememberMeCheckbox.visibility = View.VISIBLE
            findViewById<View>(R.id.passwordInput).visibility = View.VISIBLE
            findViewById<View>(R.id.usernameInput).visibility = View.VISIBLE
            btnLogin.visibility = View.VISIBLE
            findViewById<ExtendedFloatingActionButton>(R.id.googleSignInButtonFAB).visibility = View.VISIBLE
            // Hide fragment container
            findViewById<View>(R.id.fragmentContainer).visibility = View.GONE

            // Change button colors
            loginButton.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
            registerButton.setBackgroundColor(ContextCompat.getColor(this, R.color.blue))


        } catch (e: Exception) {
            Log.e("MainActivity2", "Error switching to login", e)
        }
    }


    private fun setupLoginButtonListener() {
        val btnLogin = findViewById<ExtendedFloatingActionButton>(R.id.btn_login_2)
        val rememberMeCheckbox = findViewById<CheckBox>(R.id.rememberMeCheckbox)
        val usernameInput = findViewById<TextInputEditText>(R.id.textUsernameInput)
        val passwordInput = findViewById<TextInputEditText>(R.id.textPasswordInput)

        btnLogin.setOnClickListener {
            val email = usernameInput.text.toString()
            val password = passwordInput.text.toString()
            val isRemembered = rememberMeCheckbox.isChecked

            val emailValidation = Validator.validateEmail(email)
            val passwordValidation = Validator.validatePassword(password)

            when {
                !emailValidation.isValid -> {
                    Toast.makeText(this, emailValidation.errorMessage, Toast.LENGTH_SHORT).show()
                    usernameInput.error = emailValidation.errorMessage
                }
                !passwordValidation.isValid -> {
                    Toast.makeText(this, passwordValidation.errorMessage, Toast.LENGTH_SHORT).show()
                    passwordInput.error = passwordValidation.errorMessage
                }
                else -> {
                    if (verifyCredentials(email, password)) {
                        if (isRemembered) {
                            sercurePrefManager.saveUserCredentials(email, password, true)
                        }
                        Toast.makeText(this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
                        navigateHome()
                    } else {
                        Toast.makeText(this, "Sai email hoặc mật khẩu", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun switchToRegister(loginButton: ExtendedFloatingActionButton, registerButton: ExtendedFloatingActionButton) {
        supportFragmentManager.commit {
            replace(R.id.fragmentContainer, Fragment_register())
            addToBackStack(null)
        }
        isFragmentVisible = true

        // Hide login-related
        findViewById<View>(R.id.textView3).visibility = View.GONE
        findViewById<View>(R.id.textView4).visibility = View.GONE
        findViewById<View>(R.id.textView5).visibility = View.GONE
        findViewById<View>(R.id.rememberMeCheckbox).visibility = View.GONE
        findViewById<View>(R.id.passwordInput).visibility = View.GONE
        findViewById<View>(R.id.usernameInput).visibility = View.GONE
        findViewById<ExtendedFloatingActionButton>(R.id.btn_login_2).visibility = View.GONE
        findViewById<ExtendedFloatingActionButton>(R.id.googleSignInButtonFAB).visibility = View.GONE

        // Show fragment container
        findViewById<View>(R.id.fragmentContainer).visibility = View.VISIBLE

        // Change button colors
        loginButton.setBackgroundColor(ContextCompat.getColor(this, R.color.blue))
        registerButton.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
    }

    private fun navigateHome(){
        startActivity(Intent(this, HomePageTest::class.java))
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        finish()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    private fun verifyCredentials(email: String, password: String): Boolean {
        return email == "phat@gmail.com" && password == "Test123!"
    }


}
