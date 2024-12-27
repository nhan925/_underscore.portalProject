package com.example.login_portal.ui.setting

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.example.login_portal.R
import com.example.login_portal.databinding.FragmentSetupPasswordBinding
import com.example.login_portal.utils.ApiServiceHelper
import com.example.login_portal.utils.PasswordStrength
import com.example.login_portal.utils.SecurePrefManager
import com.example.login_portal.utils.Validator




class SetupPassword : Fragment() {
    private var _binding: FragmentSetupPasswordBinding? = null
    private val binding get() = _binding!!
    private lateinit var securePrefManager: SecurePrefManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        securePrefManager = SecurePrefManager(requireContext())
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSetupPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setupTextChangeListeners()
        setupSubmitButton()
    }


    private fun setupTextChangeListeners() {
        // Clear errors when text changes
        binding.etOldPassword.doAfterTextChanged {
            binding.confirmPasswordLayout.error = null
        }
        binding.etNewPassword.doAfterTextChanged { editable ->
            val password = editable?.toString() ?: ""
            val strength = Validator.validateNewPassword(password)

            // Đặt màu và thông báo tùy theo độ mạnh của mật khẩu
            val color = when (strength) {
                PasswordStrength.TOO_SHORT, PasswordStrength.WEAK ->
                    ContextCompat.getColor(requireContext(), R.color.error)
                PasswordStrength.MEDIUM ->
                    ContextCompat.getColor(requireContext(), R.color.warning)
                PasswordStrength.STRONG ->
                    ContextCompat.getColor(requireContext(), R.color.success)
                PasswordStrength.VERY_STRONG ->
                    ContextCompat.getColor(requireContext(), R.color.success_dark)
            }

            binding.newPasswordLayout.apply {
                setHelperTextColor(ColorStateList.valueOf(color))
                helperText = getString(strength.message)
            }
        }


        binding.etConfirmPassword.doAfterTextChanged {
            val newPassword = binding.etNewPassword.text?.toString() ?: ""
            val confirmPassword = it?.toString() ?: ""
            if(newPassword != confirmPassword){
                binding.confirmPasswordLayout.error = getString(R.string.error_passwords_not_match)
            }else{
                binding.confirmPasswordLayout.error = null
            }
        }
    }


    private fun setupSubmitButton() {
        binding.btnSubmit.setOnClickListener {
            if (validateInputs()) {
                changePassword()
            }
        }
    }


    private fun validateInputs(): Boolean {
        var isValid = true

        // Validate old password
        if (binding.etOldPassword.text.isNullOrEmpty()) {
            binding.oldPasswordLayout.error = getString(R.string.error_old_password_required)
            isValid = false
        }


        // Validate new password
        val newPassword = binding.etNewPassword.text?.toString() ?: ""
        val passwordStrength = Validator.validateNewPassword(newPassword)
        if (newPassword.isEmpty()) {
            binding.newPasswordLayout.error = getString(R.string.error_new_password_required)
            isValid = false
        } else if (passwordStrength == PasswordStrength.TOO_SHORT ||
            passwordStrength == PasswordStrength.WEAK) {
            binding.newPasswordLayout.error = getString(R.string.error_password_weak)
            isValid = false
        }


        // Validate confirm password
        //val newPassword = binding.etNewPassword.text.toString()
        val confirmPassword = binding.etConfirmPassword.text.toString()
        if (confirmPassword.isEmpty()) {
            binding.confirmPasswordLayout.error = getString(R.string.error_confirm_password_required)
            isValid = false
        } else if (newPassword != confirmPassword) {
            binding.confirmPasswordLayout.error = getString(R.string.error_passwords_not_match)
            isValid = false
        }


        return isValid
    }


    private fun changePassword() {
        val currentPassword = binding.etOldPassword.text.toString()
        val newPassword = binding.etNewPassword.text.toString()


        // Show loading state
        setLoadingState(true)


        ApiServiceHelper.changePassword(currentPassword, newPassword) { result ->
            activity?.runOnUiThread {
                setLoadingState(false)
                handleChangePasswordResult(result)
            }
        }
    }


    private fun handleChangePasswordResult(result: String) {
        when (result) {
            "SUCCESS" -> {
                binding.etNewPassword.text?.toString()?.let { newpassword ->
                    securePrefManager.updatePassword(newpassword)
                }
                Toast.makeText(context, R.string.password_change_success, Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            }
            "WRONG" -> {
                binding.oldPasswordLayout.error = getString(R.string.error_wrong_password)
            }
            "DUPLICATE" -> {
                binding.newPasswordLayout.error = getString(R.string.error_same_password)
            }
            else -> {
                Toast.makeText(context, R.string.error_change_password, Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun setLoadingState(isLoading: Boolean) {
        binding.btnSubmit.isEnabled = !isLoading
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.etOldPassword.isEnabled = !isLoading
        binding.etNewPassword.isEnabled = !isLoading
        binding.etConfirmPassword.isEnabled = !isLoading
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



}





