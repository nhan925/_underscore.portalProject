package com.example.login_portal.ui.tuition

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.login_portal.databinding.FragmentTuitionBinding
import com.example.login_portal.ui.schedule.ScheduleViewModel


class TuitionFragment : Fragment() {

    private var _binding: FragmentTuitionBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val tuitionViewModel =
            ViewModelProvider(this).get(TuitionViewModel::class.java)

        _binding = FragmentTuitionBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textTuition
        tuitionViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}