package com.example.login_portal.ui.information

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.ViewSwitcher
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.login_portal.R
import com.example.login_portal.databinding.FragmentInformationContactBinding
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Information_Contact.newInstance] factory method to
 * create an instance of this fragment.
 */
class Information_Contact : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val informationViewModel: InformationViewModel by viewModels({ requireParentFragment() })
    private var _binding : FragmentInformationContactBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onResume() {
        super.onResume()
        viewLifecycleOwner.lifecycleScope.launch {
            informationViewModel.reset()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentInformationContactBinding.inflate(inflater,container,false)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = informationViewModel
        var root : View = binding.root

        binding.infoContactFragAcceptChanges.setOnClickListener{
            var message = informationViewModel.CheckAcceptChanges();
            if(message == "Success"){
                informationViewModel.AcceptChange()
                informationViewModel.Save()
            }
            else{
                showResultDialog(requireContext(), message)
            }
            toggleEditMode()
        }

        binding.infoContactFragCancelChanges.setOnClickListener{
            toggleEditMode()
        }

        binding.infoContactFragSwitchToEditMode.setOnClickListener{
            toggleEditMode()

        }
        return root
    }

    private fun toggleEditMode() {
        binding.infoContactFragViewSwitcherEmail.showNext()
        binding.infoContactFragViewSwitcherPhone.showNext()
        binding.infoContactFragViewSwitcherAddress.showNext()
        if (informationViewModel.isEditing.value == true) {
            informationViewModel.CancelChanges()
        }
        informationViewModel.isEditing.value = informationViewModel.isEditing.value !=true
    }

    fun showResultDialog(context: Context, message: String) {
        AlertDialog.Builder(context)
            .setTitle(requireContext().getString(R.string.infor_student_frag_error_alert_dialog))
            .setMessage(message)
            .setPositiveButton(requireContext().getString(R.string.infor_student_frag_close_dialog)) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Information_Contact.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Information_Contact().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}