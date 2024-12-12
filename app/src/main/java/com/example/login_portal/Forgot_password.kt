package com.example.login_portal

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.login_portal.utils.ApiServiceHelper
import com.google.android.material.snackbar.Snackbar
import android.os.CountDownTimer

class Forgot_password : BaseActivity() {

    private lateinit var otpEditTexts: List<EditText>
    private lateinit var btnSubmit: Button
    private lateinit var tvTimer: TextView
    private var countDownTimer: CountDownTimer? = null
    private val OTP_TIMEOUT = 60L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_forgot_password)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        otpEditTexts = listOf(
            findViewById(R.id.etOtp1),
            findViewById(R.id.etOtp2),
            findViewById(R.id.etOtp3),
            findViewById(R.id.etOtp4),
            findViewById(R.id.etOtp5),
            findViewById(R.id.etOtp6)
        )
        setupOtpInput()


        //val btnSubmit = findViewById<Button>(R.id.btnSubmit)
        val tvEmailLabel = findViewById<TextView>(R.id.tvEmailInfo)
        val tvEmail=findViewById<TextView>(R.id.tvEmail)

        val username= intent.getStringExtra("USERNAME")

        username?.let{
            ApiServiceHelper.getUserEmail(it) { email ->
                if(!email.isNullOrEmpty()){
                    val maskedEmail = maskEmail(email)
                    tvEmail.text = maskedEmail

                } else{
                    Snackbar.make(findViewById(android.R.id.content),
                    "User not found", Snackbar.LENGTH_LONG).show()
                    finish()

                }
            }
        }?: run {
            Snackbar.make(findViewById(android.R.id.content),
                "Username not provided", Snackbar.LENGTH_LONG).show()
            finish()
        }

        btnSubmit = findViewById(R.id.btnSubmit)
        tvTimer = findViewById(R.id.tvTimer)

        // start timer
        startOtpTime()

        btnSubmit.setOnClickListener {

            val otp = getOtpString()
            if( otp.length ==6){
                //loading

                username?. let { userName ->
                    ApiServiceHelper.validateOTP(userName, otp) { resetToken ->
                        runOnUiThread{
                            //loading

                            if (!resetToken.isNullOrEmpty()){
                                //OTP valid
                                val intent = Intent(this, Reset_password::class.java).apply{
                                    putExtra("USERNAME", username)
                                    putExtra("RESET_TOKEN", resetToken)
                                }
                                startActivity(intent)
                                finish()
                            }
                            else{
                                Snackbar.make(findViewById(android.R.id.content),
                                    "Invalid OTP", Snackbar.LENGTH_LONG).show()

                                //clear
                                otpEditTexts.forEach { it.text.clear() }
                                otpEditTexts[0].requestFocus()
                            }
                        }
                    }
                }
            }
            else {
                Snackbar.make(findViewById(android.R.id.content),
                    "Nhap vao", Snackbar.LENGTH_LONG).show()
            }
        }

    findViewById<TextView>(R.id.tvResend).setOnClickListener{
        username?.let { userName ->
            //loading
            ApiServiceHelper.checkUsernameAndSendOTP(userName) { success ->
                runOnUiThread {
                    //loading
                    if (success) {
                        Snackbar.make(
                            findViewById(android.R.id.content),
                            "OTP sent", Snackbar.LENGTH_LONG
                        ).show()
                        // clear OTP
                        otpEditTexts.forEach { it.text.clear() }
                        otpEditTexts[0].requestFocus()

                        btnSubmit.isEnabled = true
                        startOtpTime()
                    } else {
                        Snackbar.make(
                            findViewById(android.R.id.content),
                            "Error sending OTP", Snackbar.LENGTH_LONG
                        ).show()
                    }
                }

            }

        }
    }

    }

    // bao mat email
    private fun maskEmail(email: String):String{
        val atIndex=email.indexOf("@")
        if(atIndex<=1) return email

        val username = email.substring(0,atIndex)
        val domain =email.substring(atIndex)

        val maskedUsername = when {
            username.length <=3 -> username.first()+ "*"+ username.last()
            username.length <=6 -> username.take(2) + "*".repeat(username.length -3 ) + username.last()
            else -> username.take(3) + "*".repeat(username.length-4)+username.last()
        }
        return maskedUsername+ domain
    }

    // set up option
    private fun setupOtpInput(){
        for ( i in otpEditTexts.indices){
            otpEditTexts[i].apply{

                filters = arrayOf<InputFilter>(InputFilter.LengthFilter(1))

                addTextChangedListener(object: TextWatcher {

                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int) {
                       // TODO("Not yet implemented")
                    }
                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                       // TODO("Not yet implemented")
                    }

                    override fun afterTextChanged(s: Editable?) {
                        if (s?.length ==1){
                            //move to next edit text
                            if (i<otpEditTexts.size-1){
                                otpEditTexts[i+1].requestFocus()
                            } else{
                                //hide keyboard in last box
                                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                                imm?.hideSoftInputFromWindow(windowToken, 0)
                            }
                        } else if ( s?.length==0) {
                            // move to previous edit text
                            if (i>0){
                                otpEditTexts[i-1].requestFocus()
                            }
                        }
                    }
                })

                // handle deletion/backspace
                setOnKeyListener { _, keyCode, event ->
                    if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN){
                        if (text.isEmpty() && i>0) {
                            otpEditTexts[i-1].apply{
                                requestFocus()
                                text.clear()
                            }
                            return@setOnKeyListener true
                        }

                    }
                    false
                }
            }

        }

        otpEditTexts[0].apply{
            requestFocus()
            postDelayed({
                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                imm?.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
            },200)
        }
    }

    private fun getOtpString(): String {
        return otpEditTexts.joinToString("") { it.text.toString() }
    }

    // timer
    private fun startOtpTime() {
        countDownTimer?.cancel()

        // Enable button
        btnSubmit.isEnabled =true

        // start timer
        countDownTimer = object : CountDownTimer(OTP_TIMEOUT * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                tvTimer.text = "Time remaining: $secondsRemaining s"
            }

            override fun onFinish() {
                tvTimer.text = "OTP expired"
                btnSubmit.isEnabled = false
                otpEditTexts.forEach { it.text.clear() }
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }




}