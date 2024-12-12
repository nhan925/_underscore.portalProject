package com.example.login_portal

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.login_portal.utils.ApiServiceHelper
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.microsoft.identity.common.java.nativeauth.providers.responses.signup.SignUpStartApiResult

class Reset_password : BaseActivity() {

    private val PASSWORD_PATTERN = "(?=(.*[0-9]))((?=.*[A-Za-z0-9])(?=.*[A-Z])(?=.*[a-z]))^.{8,}$".toRegex()
    private lateinit var etNewPassword: TextInputEditText
    private lateinit var etNewPasswordLayout: TextInputLayout
    private lateinit var etConfirmPassword: TextInputEditText
    private lateinit var etConfirmPasswordLayout: TextInputLayout
    private lateinit var btnResetPassword: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reset_password)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val username = intent.getStringExtra("USERNAME")
        val resetToken = intent.getStringExtra("RESET_TOKEN")

        if(username == null || resetToken == null){
            Snackbar.make(findViewById(android.R.id.content),
                "Username or reset token not provided", Snackbar.LENGTH_LONG).show()
            finish()
            return
        }
//
//        val etNewPassword = findViewById<TextInputEditText>(R.id.testResetPassword1)
//        val etNewPasswordLayout = findViewById<TextInputLayout>(R.id.resetPassword1)
//        val etConfirmPassword = findViewById<TextInputEditText>(R.id.testResetPassword2)
//        val etConfirmPasswordLayout = findViewById<TextInputLayout>(R.id.resetPassword2)
//        val btnResetPassword = findViewById<Button>(R.id.btn_reset_password)

        etNewPassword = findViewById(R.id.testResetPassword1)
        etNewPasswordLayout = findViewById(R.id.resetPassword1)
        etConfirmPassword = findViewById(R.id.testResetPassword2)
        etConfirmPasswordLayout = findViewById(R.id.resetPassword2)
        btnResetPassword = findViewById(R.id.btn_reset_password)
        btnResetPassword.isEnabled = false

        setupValidation()
        setupButtonSubmit(username, resetToken)

//        btnResetPassword.setOnClickListener {
//            val etNewPasswordValue = etNewPassword.text.toString()
//            val etConfirmPasswordValue = etConfirmPassword.text.toString()
//
//            // Reset error
//            etNewPasswordLayout.error = null
//            etConfirmPasswordLayout.error = null
//
//            //Validate input
//            if(etNewPasswordValue.isEmpty()){
//                etNewPasswordLayout.error = "Password cannot be empty"
//                return@setOnClickListener
//            }
//
//            if(etConfirmPasswordValue.isEmpty()){
//                etConfirmPasswordLayout.error = "Please confirm your password"
//                return@setOnClickListener
//            }
//
//            if(!etNewPasswordValue.matches(PASSWORD_PATTERN) or !etConfirmPasswordValue.matches(PASSWORD_PATTERN)){
//                etNewPasswordLayout.error = "Password must contain at least 8 characters, including uppercase, lowercase and number"
//                etConfirmPasswordLayout.error = "Password must contain at least 8 characters, including uppercase, lowercase and number"
//                return@setOnClickListener
//            }
//
//            if(etNewPasswordValue != etConfirmPasswordValue){
//                etConfirmPasswordLayout.error = "Passwords do not match"
//                return@setOnClickListener
//            }
//
//            //loading
//
//            ApiServiceHelper.updateNewPassWord(username, resetToken, etNewPasswordValue){ sucess ->
//                runOnUiThread {
//                    //lloading
//
//                    if(sucess){
//                        intent = Intent(this, Reset_successfull::class.java)
//                        startActivity(intent)
//                        finish()
//                    }else{
//                        Snackbar.make(findViewById(android.R.id.content),
//                            "Reset password failed", Snackbar.LENGTH_LONG).show()
//                    }
//                }
//            }
//        }
    }



    private fun setupValidation() {
        // Validate new password as user types
        etNewPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // to do here
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //to do here
            }
            override fun afterTextChanged(s: Editable?) {
                validateNewPassword(s?.toString())
                validatePasswordMatch()
                updateSubmitButtonState()
            }
        })

        // Validate confirm password as user types
        etConfirmPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // to do herre
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // to do here
            }
            override fun afterTextChanged(s: Editable?) {
                validatePasswordMatch()
                updateSubmitButtonState()
            }
        })
    }

    private fun validateNewPassword(password: String?): Boolean {
        return when {
            password.isNullOrEmpty() -> {
                etNewPasswordLayout.error = "Password cannot be empty"
                false
            }
            !password.matches(PASSWORD_PATTERN) -> {
                etNewPasswordLayout.error = "Password must contain at least 8 characters, including uppercase, lowercase and number"
                false
            }
            else -> {
                etNewPasswordLayout.error = null
                true
            }
        }
    }

    private fun validatePasswordMatch(): Boolean {
        val newPassword = etNewPassword.text?.toString()
        val confirmPassword = etConfirmPassword.text?.toString()

        if ( !newPassword.isNullOrEmpty() && newPassword != confirmPassword) {
                etConfirmPasswordLayout.error = "Passwords do not match"
                return false
        } else{
            etConfirmPasswordLayout.error = null
            return true
        }
    }

    private fun updateSubmitButtonState() {
        val newPassword = etNewPassword.text?.toString()
        btnResetPassword.isEnabled = validateNewPassword(newPassword) && validatePasswordMatch()
    }

    private fun setupButtonSubmit(username: String, resetToken : String) {
        btnResetPassword.setOnClickListener {
            val newPassword = etNewPassword.text?.toString() ?: return@setOnClickListener

            //loading

            ApiServiceHelper.updateNewPassWord(username, resetToken, newPassword) { success ->
                runOnUiThread {
                    //loading
                    if(success){
                        intent = Intent(this, Reset_successfull::class.java)
                        startActivity(intent)
                        finish()
                    }
                    else{
                        btnResetPassword.isEnabled = true
                        Snackbar.make(findViewById(android.R.id.content),
                            "Reset password failed", Snackbar.LENGTH_LONG).show()
                    }
                    }

            }
        }
    }

}