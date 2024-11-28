package com.example.login_portal.ui.requests

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.example.login_portal.R
import com.example.login_portal.databinding.FragmentRequestDetailAnswerBinding
import com.example.login_portal.databinding.FragmentRequestDetailContentBinding

class RequestDetailAnswer(val requestId: Int) : Fragment() {
    private var _binding: FragmentRequestDetailAnswerBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: RequestDetailAnswerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = RequestDetailAnswerViewModel(requestId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRequestDetailAnswerBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val title = binding.requestAnswerContentTitle
        val time = binding.requestAnswerContentTime
        val content = binding.requestDetailAnswerTabContentText

        viewModel.answer.observe(viewLifecycleOwner, Observer { data ->
            title.text = requireContext().resources.getString(R.string.request_detail_answer_title)
            time.text = data.getTimeString(requireContext())
            content.setText(data.AnswerContent)
        })

        return root
    }

    override fun onResume() {
        super.onResume()
        viewModel.reset()
    }
}