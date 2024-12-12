package com.example.login_portal.ui.requests

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.example.login_portal.R
import com.example.login_portal.databinding.FragmentRequestBinding
import com.example.login_portal.databinding.FragmentRequestDetailContentBinding
import com.google.gson.Gson

class RequestDetailContent(var processingItem: RequestItem) : Fragment() {
    private var _binding: FragmentRequestDetailContentBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: RequestDetailContentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = RequestDetailContentViewModel()
        viewModel.init(processingItem)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRequestDetailContentBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val title = binding.requestDetailContentTitle
        val time = binding.requestDetailContentTime
        val content = binding.requestDetailDetailTabContentText

        viewModel.title.observe(viewLifecycleOwner, Observer { data ->
            title.text = data
        })

        viewModel.time.observe(viewLifecycleOwner, Observer { data ->
            time.text = data
        })

        viewModel.content.observe(viewLifecycleOwner, Observer { data ->
            content.setText(data)
        })

        return root
    }
}