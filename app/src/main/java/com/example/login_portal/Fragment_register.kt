package com.example.login_portal

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.collection.emptyLongSet
import androidx.fragment.app.Fragment
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
        val emailEditText = view.findViewById<TextInputEditText>(R.id.textUsernameInput1)

        getPasswordButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val emailValidation = Validator.validateEmail(email)
            if (!emailValidation.isValid) {
                Toast.makeText(activity, emailValidation.errorMessage, Toast.LENGTH_SHORT).show()
                emailEditText.error = emailValidation.errorMessage
                return@setOnClickListener
            }

            val intent = Intent(activity, Forgot_password::class.java)
            intent.putExtra("email", email)
            startActivity(intent)



        }

        return view
    }


}
