package com.example.login_portal.ui.information

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.login_portal.R
import com.example.login_portal.databinding.FragmentInformationDetailBinding
import kotlinx.coroutines.launch
import java.util.Date
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Information_Detail.newInstance] factory method to
 * create an instance of this fragment.
 */
class Information_Detail : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val informationViewModel: InformationViewModel by viewModels({ requireParentFragment() })
    private var _binding : FragmentInformationDetailBinding? = null
    val binding get() = _binding!!

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

        _binding = FragmentInformationDetailBinding.inflate(inflater,container,false)
        val root: View = binding.root

        val language = Locale.getDefault().language
        informationViewModel.informations.observe(viewLifecycleOwner){
            binding.infoDetailFragTvDob.text = informationViewModel.convert(it.DateOfBirth, Date::class.java,null,language)
            binding.infoDetailFragTvNationality.text = it.Nationality
            binding.infoDetailFragTvEthnicity.text = it.Ethnicity
            binding.infoDetailFragTvIdCard.text= it.IdentityCardNumber
            binding.infoDetailFragTvEmail.text = it.SchoolEmail
        }

        return root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Information_Detail.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Information_Detail().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}