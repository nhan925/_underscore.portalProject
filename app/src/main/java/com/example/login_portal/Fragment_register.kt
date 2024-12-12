package com.example.login_portal

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.collection.emptyLongSet
import androidx.fragment.app.Fragment
import com.example.login_portal.utils.ApiServiceHelper
import com.example.login_portal.utils.Validator
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import java.util.Locale


class Fragment_register : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        // Find the "Get New Password" button by ID
        val getPasswordButton = view.findViewById<Button>(R.id.btn_resetpassword_2)
        val usernameInput = view.findViewById<TextInputEditText>(R.id.textUsernameInput1)

        getPasswordButton.setOnClickListener {
            val username = usernameInput.text?.toString()

            if (username.isNullOrEmpty()) {
                Snackbar.make(view, "Please enter your username", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Show loading state
            //getPasswordButton.isEnabled = false

            // Call API to check username and send OTP
            ApiServiceHelper.checkUsernameAndSendOTP(username) { success ->
                // Ensure we're on the main thread
                requireActivity().runOnUiThread {
                    //getPasswordButton.isEnabled = true

                    if (success) {
                        try {
                            // Navigate to ForgotPassword activity with username
                            val intent = Intent(requireActivity(), Forgot_password::class.java).apply {
                                putExtra("USERNAME", username)
                            }
                            startActivity(intent)
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Snackbar.make(view, "Error navigating to next screen", Snackbar.LENGTH_LONG).show()
                        }
                    } else {
                        Snackbar.make(view, "Username not found or error sending OTP", Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }

        return view
    }




}
