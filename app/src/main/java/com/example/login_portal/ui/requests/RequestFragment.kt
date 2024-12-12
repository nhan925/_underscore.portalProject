package com.example.login_portal.ui.requests

import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.login_portal.R
import com.example.login_portal.databinding.FragmentNotificationBinding
import com.example.login_portal.databinding.FragmentRequestBinding
import com.example.login_portal.ui.notification.NotificationViewModel
import com.example.login_portal.utils.ApiServiceHelper
import com.google.gson.Gson

class RequestFragment : Fragment() {

    private var _binding: FragmentRequestBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: RequestViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = RequestViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRequestBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val recyclerView: RecyclerView = binding.requestRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = RequestItemAdapter(listOf(), requireContext())
        recyclerView.adapter = adapter
        adapter.onItemClick = { item ->
            val intent = Intent(requireContext(), RequestDetail::class.java)
            intent.putExtra("REQUEST_DETAIL", Gson().toJson(item))
            startActivity(intent)
        }

        viewModel.requests.observe(viewLifecycleOwner, Observer { data ->
            adapter.resetSource(data)
        })

        val fabBtn = binding.requestFilterFAB
        fabBtn.setOnClickListener { v: View ->
            val popup = PopupMenu(requireContext(), v)

            val status = mutableListOf<String>()
            viewModel.status.observe(viewLifecycleOwner, Observer { data ->
                status.add(0, context?.getString(R.string.request_status_all)!!)
                status.addAll(1, data)
            })

            status.forEachIndexed { index, title ->
                popup.menu.add(0, index, index, title)
            }

            popup.setOnMenuItemClickListener { item ->
                if (item.itemId == 0)
                    viewModel.filterByStatus("")
                else
                    viewModel.filterByStatus(status[item.itemId])
                true
            }
            popup.setOnDismissListener { }

            popup.show()
        }

        return root
    }

    override fun onResume() {
        super.onResume()
        viewModel.reset()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}