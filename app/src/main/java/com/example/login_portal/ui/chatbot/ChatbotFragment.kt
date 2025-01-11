package com.example.login_portal.ui.chatbot

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.login_portal.R
import com.example.login_portal.databinding.FragmentChatbotBinding
import kotlinx.coroutines.launch


class ChatbotFragment : Fragment() {
    private lateinit var chatbotViewModel: ChatbotViewModel
    private var _binding : FragmentChatbotBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        chatbotViewModel = ViewModelProvider(this).get(ChatbotViewModel::class.java)
        _binding = FragmentChatbotBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val adapter = AdapterForChatbot(emptyList())

        val recyclerView : RecyclerView = binding.rvMessagesChatbot
        recyclerView.adapter = adapter
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager

        chatbotViewModel.messages.observe(viewLifecycleOwner){ messageList ->
            adapter.updateData(messageList)
            binding.rvMessagesChatbot.scrollToPosition(messageList.size - 1)
        }

        binding.btnSendChatbot.setOnClickListener {
            Log.e("ChatbotFragment", "Send button clicked!")

            lifecycleScope.launch {
                chatbotViewModel.sendMessage(binding.etMessageChatbot.text.toString(), emptyList())
            }
            binding.etMessageChatbot.text.clear()

        }

        return root
    }

}