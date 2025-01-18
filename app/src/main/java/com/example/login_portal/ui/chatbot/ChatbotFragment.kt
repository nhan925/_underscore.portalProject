package com.example.login_portal.ui.chatbot

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
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

    private var imageCount = 0
    private val imageList = mutableListOf<Bitmap>()

    private val selectImageRequestCode = 1002
    private val maxImage = 3 // Phải là số nguyên
    private val maxSizeOfImage = 5 // Phải là số nguyên

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        chatbotViewModel = ViewModelProvider(this).get(ChatbotViewModel::class.java)
        _binding = FragmentChatbotBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val adapter = AdapterForChatbot(requireContext(),emptyList())

        val recyclerView : RecyclerView = binding.rvMessagesChatbot
        recyclerView.adapter = adapter
        val layoutManager = LinearLayoutManager(requireContext())
        recyclerView.layoutManager = layoutManager

        chatbotViewModel.messages.observe(viewLifecycleOwner){ messageList ->
            if (messageList.isEmpty()) {
                recyclerView.visibility = View.GONE
                binding.tvNoDataChatbot.visibility = View.VISIBLE
            } else {
                recyclerView.visibility = View.VISIBLE
                binding.tvNoDataChatbot.visibility = View.GONE
                adapter.updateData(messageList)
                binding.rvMessagesChatbot.scrollToPosition(messageList.size - 1)
            }
        }
        binding.etMessageChatbot.setText("")
        binding.btnSendChatbot.isEnabled = false
        binding.etMessageChatbot.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                binding.btnSendChatbot.isEnabled = !s.isNullOrEmpty()
            }
        })
        solveEventClick()

        return root
    }

    @SuppressLint("StringFormatInvalid")
    private fun solveEventClick(){
        binding.btnSendChatbot.setOnClickListener {
            binding.lottieTypingChatbot.visibility = View.VISIBLE
            lifecycleScope.launch {
                try {
                    chatbotViewModel.sendMessage(requireContext(),binding.etMessageChatbot.text.toString(), imageList)
                }
                catch (e: IllegalArgumentException){
                    showResultDialog(requireContext().getString(R.string.alert_dialog_about_image_chatbot, maxImage, maxSizeOfImage))
                }
                catch (e:IllegalStateException){
                    showResultDialog(requireContext().getString(R.string.chatbot_error_message_exceed_chathistory),true)
                }

                binding.lottieTypingChatbot.visibility = View.GONE
            }
            binding.etMessageChatbot.text.clear()
            imageCount = 0
            imageList.clear()

            binding.imageContainer1.visibility = View.GONE
            binding.imageContainer2.visibility = View.GONE
            binding.imageContainer3.visibility = View.GONE

            binding.btnAttachImageChatbot.isEnabled = true
        }

        binding.btnAttachImageChatbot.setOnClickListener{
            selectImage()
        }

        binding.btnCreatNewChat.setOnClickListener{
            chatbotViewModel.creatNewChat()
        }

        binding.btnDeleteImageChatbot1.setOnClickListener {
            removeImage(0)
        }

        binding.btnDeleteImageChatbot2.setOnClickListener {
            removeImage(1)
        }

        binding.btnDeleteImageChatbot3.setOnClickListener {
            removeImage(2)
        }
    }

    private fun selectImage() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png", "image/gif","image/bmp","image/tiff"))
        }
        startActivityForResult(Intent.createChooser(intent, "Select Avatar"), selectImageRequestCode)
    }

    @SuppressLint("StringFormatInvalid")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == selectImageRequestCode && resultCode == RESULT_OK) {
            val imageUri: Uri? = data?.data
            Log.e("URI", "Failed to decode bitmap from URI: ${imageUri!!.isAbsolute}")
            if (imageUri != null) {
                val contentResolver = requireContext().contentResolver
                val cursor = contentResolver.query(imageUri, arrayOf(MediaStore.Images.Media.SIZE), null, null, null)

                cursor?.use {
                    if (it.moveToFirst()) {
                        val sizeIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
                        val fileSize = it.getLong(sizeIndex)
                        val fileSizeInMB = fileSize / (1024 * 1024)

                        if (fileSizeInMB > maxSizeOfImage) {
                            showResultDialog(requireContext().getString(R.string.alert_dialog_about_image_chatbot, maxImage, maxSizeOfImage))
                            return
                        }
                    }
                }
                try {
                    val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, imageUri)
                    imageList.add(bitmap)
                    imageCount++

                    updateImageContainer()
                    if (imageCount == 3) {
                        binding.btnAttachImageChatbot.isEnabled = false
                    }
                } catch (e: Exception) {
                    Log.e("URI", "Failed to decode bitmap from URI: $imageUri", e)
                }
                Log.e("CHECK","Chưa return")
            }
        }
    }

    private fun updateImageContainer() {
        binding.imageContainer1.visibility = View.GONE
        binding.imageContainer2.visibility = View.GONE
        binding.imageContainer3.visibility = View.GONE

        imageList.forEachIndexed { index, bitmap ->
            when (index) {
                0 -> {
                    binding.imageContainer1.visibility = View.VISIBLE
                    binding.imagePreviewSendChatbot1.setImageBitmap(bitmap)
                }
                1 -> {
                    binding.imageContainer2.visibility = View.VISIBLE
                    binding.imagePreviewSendChatbot2.setImageBitmap(bitmap)
                }
                2 -> {
                    binding.imageContainer3.visibility = View.VISIBLE
                    binding.imagePreviewSendChatbot3.setImageBitmap(bitmap)
                }
            }
        }

        binding.btnAttachImageChatbot.isEnabled = imageList.size < 3
    }


    private fun removeImage(index: Int) {
        if (index in imageList.indices) {
            imageList.removeAt(index)
            imageCount--

            updateImageContainer()
            if (imageCount < 3) {
                binding.btnAttachImageChatbot.isEnabled = true
            }
        }
    }

    fun showResultDialog(message: String, hasBtnRestart: Boolean = false) {
        val builder = AlertDialog.Builder(requireContext())
            .setTitle(requireContext().getString(R.string.alert_dialog_title_chatbot))
            .setMessage(message)
            .setPositiveButton(requireContext().getString(R.string.alert_dialog_btn_close_chatbot)) { dialog, _ ->
                dialog.dismiss()
            }

        if (hasBtnRestart) {
            builder.setNegativeButton(requireContext().getString(R.string.alert_dialog_btn_restart_chatbot)) { dialog, _ ->
                chatbotViewModel.creatNewChat()
                dialog.dismiss()
            }
        }
        builder.show()
    }
}